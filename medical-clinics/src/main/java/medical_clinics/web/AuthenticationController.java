package medical_clinics.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import medical_clinics.shared.security.AuthenticationService;
import medical_clinics.user_account.service.UserAccountService;
import medical_clinics.web.dto.LoginRequest;
import medical_clinics.web.dto.RegisterRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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
        return ResponseEntity.accepted ( ).build ( );
    }

    @PostMapping("/login")
    public ResponseEntity<String> login ( @RequestBody @Valid LoginRequest request ) {
        return ResponseEntity.ok ( ).header (
                HttpHeaders.AUTHORIZATION,
                authenticationService.authenticate ( request )
        ).build ( );
    }

    @GetMapping("/some")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_PATIENT')")
    public ResponseEntity<?> some () {
        return ResponseEntity.ok ( "yo" );
    }
}
