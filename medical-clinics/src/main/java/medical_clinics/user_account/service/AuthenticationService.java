package medical_clinics.user_account.service;

import lombok.RequiredArgsConstructor;
import medical_clinics.shared.JwtService;
import medical_clinics.web.dto.LoginRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public String authenticate ( LoginRequest request ) {
        Authentication authentication = authenticationManager.authenticate (
                new UsernamePasswordAuthenticationToken (
                        request.getEmail ( ),
                        request.getPassword ( )
                )
        );

        return "Bearer " + jwtService.generateToken ( authentication );
    }
}
