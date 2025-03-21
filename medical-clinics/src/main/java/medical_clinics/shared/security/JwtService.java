package medical_clinics.shared.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;

    public String generateToken ( Authentication authentication ) {
        Instant now = Instant.now ( );

        String scope = authentication.getAuthorities ( ).stream ( )
                .map ( GrantedAuthority::getAuthority )
                .collect ( Collectors.joining ( " " ) );
        JwsHeader header = JwsHeader.with ( MacAlgorithm.HS256 ).build ( );
        JwtClaimsSet claims = JwtClaimsSet.builder ( )
                .issuer ( "self" )
                .issuedAt ( now )
                .expiresAt ( now.plus ( 2, ChronoUnit.HOURS ) )
                .subject ( authentication.getName ( ) )
                .claim ( "scope", scope )
                .build ( );

        return jwtEncoder.encode ( JwtEncoderParameters.from ( header, claims ) ).getTokenValue ( );
    }
}
