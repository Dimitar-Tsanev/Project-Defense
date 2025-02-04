package medical_clinics.user_account.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import medical_clinics.patient.service.PatientService;
import medical_clinics.physician.service.PhysicianService;
import medical_clinics.shared.exception.UserAccountNotFoundException;
import medical_clinics.shared.exception.UserAlreadyExistsException;
import medical_clinics.user_account.mapper.UserAccountMapper;
import medical_clinics.user_account.model.UserAccount;
import medical_clinics.user_account.property.UserProperty;
import medical_clinics.user_account.repository.UserAccountRepository;
import medical_clinics.web.dto.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final PatientService patientService;
    private final PasswordEncoder passwordEncoder;
    private final UserProperty userProperty;
    private final PhysicianService physicianService;

    @Transactional
    public void register ( RegisterRequest registerRequest ) {
        Optional<UserAccount> user = userAccountRepository.findByEmail ( registerRequest.getEmail ( ) );

        if ( user.isPresent ( ) ) {
            throw new UserAlreadyExistsException ( "User with this email already exists" );
        }
        UserAccount userAccount = userAccountRepository.save (
                UserAccountMapper.registrationMapper ( registerRequest, userProperty, passwordEncoder )
        );
        boolean isPhysicianEmail = physicianService.isEmailOfPhysician (registerRequest.getEmail ());

        if (isPhysicianEmail) {
            physicianService.addPhysicianAccount(registerRequest, userAccount );
            return;
        }
        patientService.addPatientAccount ( registerRequest, userAccount );
    }

    public Optional<UserAccount> getAccountIdByEmail ( String email ) {
        return userAccountRepository.findByEmail ( email );
    }

    public void changeAccountEmail ( String oldEmail, String newEmail ) {
        UserAccount userAccount = userAccountRepository.findByEmail ( oldEmail )
                .orElseThrow (()->
                       new UserAccountNotFoundException ( "User with provided email does not exist" )
                );

        userAccount.setEmail ( newEmail );
        userAccountRepository.save(userAccount);
    }

    public boolean findByEmail ( String email ) {
        return userAccountRepository.findByEmail ( email ).isPresent ( );
    }
}
