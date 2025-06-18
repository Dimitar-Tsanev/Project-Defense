package medical_clinics.user_account.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import medical_clinics.shared.exception.PersonalInformationDontMatchException;
import medical_clinics.user_account.exceptions.UserAccountNotFoundException;
import medical_clinics.user_account.exceptions.UserAlreadyExistsException;
import medical_clinics.user_account.mapper.UserAccountMapper;
import medical_clinics.user_account.model.Role;
import medical_clinics.user_account.model.UserAccount;
import medical_clinics.user_account.model.UserStatus;
import medical_clinics.user_account.property.InitializedAdmin;
import medical_clinics.user_account.property.UserProperty;
import medical_clinics.user_account.repository.UserAccountRepository;
import medical_clinics.web.dto.RegisterRequest;
import medical_clinics.web.dto.UserAccountEditRequest;
import medical_clinics.web.dto.events.*;
import medical_clinics.web.dto.response.AccountInformation;
import medical_clinics.web.dto.response.UserDataResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAccountService implements UserDetailsService {
    private final UserAccountRepository userAccountRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;
    private final UserProperty userProperty;
    private final InitializedAdmin defaultAdmin;

    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    void initAdmin () {
        if ( !userAccountRepository.existsByRole ( Role.ADMIN ) ) {
            UserAccount admin = UserAccount.builder ( )
                    .role ( Role.ADMIN )
                    .email ( defaultAdmin.getMail () )
                    .password ( passwordEncoder.encode ( defaultAdmin.getPassword ( ) ) )
                    .status ( UserStatus.ACTIVE )
                    .build ( );

            UserAccount adminAccount = userAccountRepository.save ( admin );

            publishNewUserAccountEvent (
                    adminAccount, defaultAdmin.getFirstName ( ), defaultAdmin.getLastName ( ), defaultAdmin.getPhone ( )
            );
        }
    }

    @Override
    public UserDetails loadUserByUsername ( String email ) throws UsernameNotFoundException {
        return userAccountRepository
                .findByEmail ( email )
                .map ( UserAccountMapper::mapToUserDetails )
                .orElseThrow ( () ->
                        new UsernameNotFoundException (
                                "User with [%s] email not found".formatted ( email )
                        )
                );
    }

    @Transactional
    public void register ( RegisterRequest registerRequest ) {
        Optional<UserAccount> user = userAccountRepository.findByEmail ( registerRequest.getEmail ( ) );

        if ( user.isPresent ( ) ) {
            throw new UserAlreadyExistsException ( "User with this email already exists" );
        }

        UserAccount userAccount = userAccountRepository.save (
                UserAccountMapper.registrationMapper ( registerRequest, passwordEncoder, userProperty )
        );

        publishNewUserAccountEvent ( userAccount, registerRequest.getFirstName ( ),
                registerRequest.getLastName ( ), registerRequest.getPhone ( )
        );
    }

    public UserDataResponse getAccountData ( String email ) {
        UserAccount account = userAccountRepository.findByEmail ( email ).orElseThrow (
                () -> new UserAccountNotFoundException ( "User with this email does not exist" )
        );

        UserDataResponse userData = new UserDataResponse ( );
        userData.setAccountId ( account.getId ( ) );
        userData.setRole ( account.getRole ( ) );

        return userData;
    }

    public UserAccount getById ( UUID id ) {
        return userAccountRepository.findById ( id ).orElseThrow ( () ->
                new UserAccountNotFoundException ( "User with provided id not found" )
        );
    }

    @Transactional
    public void editUserAccount ( UUID pathId, UserAccountEditRequest accountEdit ) {
        if ( !pathId.equals ( accountEdit.getId ( ) ) ) {
            throw new PersonalInformationDontMatchException ( "Id provided with the form is differ from request parameter" );
        }

        Optional<UserAccount> userAccountIfExist = userAccountRepository.findById ( accountEdit.getId ( ) );

        String newEmail = accountEdit.getEmail ( );

        if ( userAccountIfExist.isEmpty ( ) ) {
            throw new UserAccountNotFoundException ( "User not found" );
        }

        UserAccount userAccount = userAccountIfExist.get ( );
        String oldEmail = userAccount.getEmail ( );

        if ( !newEmail.equals ( oldEmail ) ) {
            if ( userAccountRepository.findByEmail ( newEmail ).isPresent ( ) ) {
                throw new UserAlreadyExistsException ( "User with this email already exists" );
            }
            changeAccountEmail ( userAccount, newEmail );
        }

        if ( accountEdit.getNewPassword ( ) != null && !accountEdit.getNewPassword ( ).isBlank ( ) ) {
            userAccount.setPassword ( passwordEncoder.encode ( accountEdit.getNewPassword ( ) ) );
            userAccountRepository.save ( userAccount );
        }
        eventPublisher.publishEvent ( UserAccountMapper.mapToEditedAccountEvent ( accountEdit, oldEmail ) );
    }

    @Transactional
    public void switchUserAccountRole ( UUID userId ) {
        UserAccount userAccount = getById ( userId );

        if ( userAccount.getRole ( ).equals ( Role.ADMIN ) ) {
            demoteAccount ( userAccount );
            return;
        }

        promoteToAdmin ( userAccount );
    }

    public void deleteUserAccount ( UUID accountId ) {
        UserAccount userAccount = getById ( accountId );

        userAccount.setStatus ( UserStatus.INACTIVE );
        userAccountRepository.save ( userAccount );
    }

    public void blockUserAccount ( UUID accountId ) {
        UserAccount userAccount = getById ( accountId );

        userAccount.setStatus ( UserStatus.BLOCKED );
        userAccountRepository.save ( userAccount );
    }

    public List<AccountInformation> getAllAccounts () {
        return userAccountRepository.findAll ( ).stream ( )
                .map ( UserAccountMapper::mapToAccountInformation )
                .toList ( );
    }

    @Transactional
    void demoteAccount ( UserAccount userAccount ) {
        Role role = userProperty.getDefaultRole ( );

        userAccount.setRole ( role );
        eventPublisher.publishEvent ( new DemoteAccountEvent ( userAccount.getId ( ), role ) );

        userAccountRepository.save ( userAccount );
    }

    @EventListener
    @Transactional
    void demoteAccount ( DismissedStaffEvent dismissedStaffEvent ) {
        UserAccount account = getById ( dismissedStaffEvent.getUserAccountId ( ) );
        demoteAccount ( account );
    }

    @EventListener
    void changeRoleToPhysician ( PhysicianAccountEvent physician ) {
        String email = physician.getEmail ( );

        Optional<UserAccount> user = userAccountRepository.findByEmail ( email );

        if ( user.isPresent ( ) ) {
            UserAccount account = user.get ( );

            account.setRole ( Role.PHYSICIAN );

            account = userAccountRepository.save ( account );

            eventPublisher.publishEvent ( new PatientRoleChangeToPhysician ( account ) );
        }
    }

    @EventListener
    void physicianChanged ( PhysicianChangeEvent physicianChangeEvent ) {
        String oldEmail = physicianChangeEvent.getOldEmail ( );
        String newEmail = physicianChangeEvent.getNewEmail ( );

        Optional<UserAccount> account = userAccountRepository.findByEmail ( oldEmail );

        if ( account.isPresent ( ) && !newEmail.equals ( oldEmail ) ) {
            changeAccountEmail ( account.get ( ), newEmail );
        }
    }

    void publishNewUserAccountEvent ( UserAccount userAccount, String firstName, String lastName, String phone ) {
        NewUserAccountEvent newUserAccountEvent = UserAccountMapper.mapToNewUserAccountEvent (
                userAccount, firstName,
                lastName, phone
        );

        eventPublisher.publishEvent ( newUserAccountEvent );
    }

    private void promoteToAdmin ( UserAccount userAccount ) {
        userAccount.setRole ( Role.ADMIN );
        userAccountRepository.save ( userAccount );
    }

    private void changeAccountEmail ( UserAccount userAccount, String newEmail ) {
        userAccount.setEmail ( newEmail );
        userAccountRepository.save ( userAccount );
    }
}
