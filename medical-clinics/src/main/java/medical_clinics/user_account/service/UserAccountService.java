package medical_clinics.user_account.service;

import lombok.RequiredArgsConstructor;
import medical_clinics.patient.service.PatientService;
import medical_clinics.shared.exception.UserAlreadyExistsException;
import medical_clinics.user_account.model.UserAccount;
import medical_clinics.user_account.property.UserProperty;
import medical_clinics.user_account.repository.UserAccountRepository;
import medical_clinics.web.dto.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final PatientService patientService;
    private final PasswordEncoder passwordEncoder;
    private final UserProperty userProperty;

    public void register( RegisterRequest registerRequest ) {
        Optional<UserAccount> user = userAccountRepository.findByEmail(registerRequest.getEmail ());

        if ( user.isPresent ()) {
            throw new UserAlreadyExistsException ( "User with this email already exists" );
        }

        UserAccount newAccount = UserAccount.builder().
                email( registerRequest.getEmail ()).
                password( passwordEncoder.encode( registerRequest.getPassword ()) ).
                role (userProperty.getRole () ).
                status ( userProperty.getStatus () ).
                isMessagingBlocked ( userProperty.isMessagingBlocked () ).
                build();

        UserAccount userAccount = userAccountRepository.save ( newAccount );

        if(registerRequest.getPhone () != null) {
            Optional<UUID> patientId = patientService.findByPhone ( registerRequest.getPhone () );

            if ( patientId.isPresent () ) {
                patientService.addPatientAccount(patientId.get (), userAccount);
                return;
            }
            patientService.addPatient (registerRequest.getFirstName (),
                    registerRequest.getLastName (),
                    registerRequest.getPhone (),
                    userAccount);
        }

        patientService.addPatient ( registerRequest.getFirstName (),
                registerRequest.getLastName (),
                userAccount );
    }
}
