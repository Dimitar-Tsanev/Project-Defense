package medical_clinics.shared.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthenticationManagerConfig {

    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationManager authenticationManager () {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider ( );
        authenticationProvider.setUserDetailsService ( userDetailsService );
        authenticationProvider.setPasswordEncoder ( passwordEncoder );

        return new ProviderManager ( authenticationProvider );
    }
}
