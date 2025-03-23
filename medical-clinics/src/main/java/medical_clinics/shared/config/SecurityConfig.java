package medical_clinics.shared.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private static final String JWT_ALGORITHM = JWSAlgorithm.HS256.getName ( );

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain filterChain ( HttpSecurity httpSecurity ) throws Exception {
        return httpSecurity
                .cors ( cors -> corsConfigurationSource () )
                .csrf ( AbstractHttpConfigurer::disable )
                .sessionManagement ( session ->
                        session.sessionCreationPolicy ( STATELESS )
                )
                .oauth2ResourceServer ( config ->
                        config.jwt ( jwtConfigurer ->
                                jwtConfigurer.jwtAuthenticationConverter ( authoritiesConverter ( ) )
                        )
                )
                .authorizeHttpRequests ( authorizeRequests ->
                        authorizeRequests.requestMatchers (
                                        "/auth/**", "/api-docs*/**", "/swagger-ui/**"
                                )
                                .permitAll ( )
                                .requestMatchers ( HttpMethod.GET, "/clinics*/**", "/physicians" ).permitAll ( )
                                .anyRequest ( )
                                .authenticated ( )
                )
                .httpBasic ( withDefaults ( ) )
                .build ( );
    }

    @Bean
    JwtAuthenticationConverter authoritiesConverter () {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter ( );
        grantedAuthoritiesConverter.setAuthorityPrefix ( "ROLE_" );
        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter ( );
        authenticationConverter.setJwtGrantedAuthoritiesConverter ( grantedAuthoritiesConverter );
        return authenticationConverter;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource () {
        CorsConfiguration configuration = new CorsConfiguration ( );
        configuration.setAllowedOrigins ( List.of ( "*" ) );
        configuration.setAllowedMethods ( List.of ( "GET", "POST", "PUT", "PATCH", "DELETE" ) );
        configuration.setAllowedHeaders ( List.of ( "*" ) );
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource ( );
        source.registerCorsConfiguration ( "/**", configuration );

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder ( );
    }

    @Bean
    public JwtDecoder jwtDecoder () {
        return NimbusJwtDecoder.withSecretKey ( secretKey ( ) ).build ( );
    }

    @Bean
    public JwtEncoder jwtEncoder () {
        JWKSource<SecurityContext> keySource = new ImmutableSecret<> ( secretKey ( ) );
        return new NimbusJwtEncoder ( keySource );
    }

    private SecretKey secretKey () {
        return new SecretKeySpec ( jwtSecret.getBytes ( ), JWT_ALGORITHM );
    }
}
