package medical_clinics.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import medical_clinics.shared.security.AuthenticationService;
import medical_clinics.user_account.service.UserAccountService;
import medical_clinics.web.dto.LoginRequest;
import medical_clinics.web.dto.RegisterRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserAccountService userAccountService;

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<RegisterRequest> register ( @RequestBody @Valid RegisterRequest request ) {
        userAccountService.register ( request );
        return ResponseEntity.status ( HttpStatus.CREATED ).build ( );
    }

    @PostMapping("/login")
    public ResponseEntity<String> login ( @RequestBody @Valid LoginRequest request ) {
        return ResponseEntity.ok ( ).header (
                HttpHeaders.AUTHORIZATION,
                authenticationService.authenticate ( request )
        ).build ( );
    }
}
