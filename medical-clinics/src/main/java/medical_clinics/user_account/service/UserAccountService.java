package medical_clinics.user_account.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import medical_clinics.shared.exception.UserAccountNotFoundException;
import medical_clinics.shared.exception.UserAlreadyExistsException;
import medical_clinics.shared.mappers.UserAccountMapper;
import medical_clinics.user_account.model.Role;
import medical_clinics.user_account.model.UserAccount;
import medical_clinics.user_account.property.UserProperty;
import medical_clinics.user_account.repository.UserAccountRepository;
import medical_clinics.web.dto.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Optional;

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
    void checkIfUserAccountExists ( NewPhysicianEvent createPhysician ) {
        String email = createPhysician.getPhysician ().getEmail ( );

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
}
