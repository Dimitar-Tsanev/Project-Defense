package medical_clinics.user_account.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import medical_clinics.shared.exception.UserAccountNotFoundException;
import medical_clinics.shared.exception.UserAlreadyExistsException;
import medical_clinics.shared.mappers.UserAccountMapper;
import medical_clinics.user_account.model.Role;
import medical_clinics.user_account.model.UserAccount;
import medical_clinics.user_account.model.UserStatus;
import medical_clinics.user_account.property.UserProperty;
import medical_clinics.user_account.repository.UserAccountRepository;
import medical_clinics.web.dto.*;
import medical_clinics.web.dto.events.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAccountService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;
    private final UserProperty userProperty;

    @Transactional
    public void register ( RegisterRequest registerRequest ) {
        Optional<UserAccount> user = userAccountRepository.findByEmail ( registerRequest.getEmail ( ) );

        if ( user.isPresent ( ) ) {
            throw new UserAlreadyExistsException ( "User with this email already exists" );
        }

        UserAccount userAccount = userAccountRepository.save (
                UserAccountMapper.registrationMapper ( registerRequest, passwordEncoder, userProperty )
        );

        NewUserAccountEvent newUserAccountEvent = UserAccountMapper.mapToNewUserAccountEvent (
                userAccount, registerRequest.getFirstName ( ),
                registerRequest.getLastName ( ), registerRequest.getPhone ( )
        );

        eventPublisher.publishEvent ( newUserAccountEvent );
    }

    public UserDataResponse getAccountData ( String email ) {
        UserAccount account = userAccountRepository.findByEmail ( email ).orElseThrow (
                () -> new UserAccountNotFoundException ( "User with this email does not exist" )
        );

        return UserDataResponse.builder ( )
                .accountId ( account.getId ( ) )
                .role ( account.getRole ( ) )
                .build ( );
    }

    @Transactional
    public void editUserAccount ( UserAccountEditRequest accountEdit ) {
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

        if ( accountEdit.getNewPassword ( ) != null ) {
            userAccount.setPassword ( passwordEncoder.encode ( accountEdit.getNewPassword ( ) ) );
            userAccountRepository.save ( userAccount );
        }

        eventPublisher.publishEvent ( UserAccountMapper.mapToEditedAccountEvent ( accountEdit, oldEmail ) );
    }

    public void promoteToAdmin ( UUID accountId ) {
        UserAccount userAccount = getById ( accountId );
        userAccount.setRole ( Role.ADMIN );
        userAccountRepository.save ( userAccount );
    }

    @Transactional
    public void demoteAccount ( UUID accountId ) {
        UserAccount userAccount = getById ( accountId );
        Role role = userProperty.getRole ( );

        eventPublisher.publishEvent ( new DemoteAccountEvent ( userAccount.getId ( ), role ) );
        userAccount.setRole ( role );

        userAccountRepository.save ( userAccount );
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

    @EventListener
    @Transactional
    void demoteAccount ( DismissedStaffEvent dismissedStaffEvent ) {
        demoteAccount ( dismissedStaffEvent.getUserAccountId ( ) );
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

    private void changeAccountEmail ( UserAccount userAccount, String newEmail ) {
        userAccount.setEmail ( newEmail );
        userAccountRepository.save ( userAccount );
    }

    private UserAccount getById ( UUID id ) {
        return userAccountRepository.findById ( id ).orElseThrow ( () ->
                new UserAccountNotFoundException ( "User with provided id not found" )
        );
    }
}
