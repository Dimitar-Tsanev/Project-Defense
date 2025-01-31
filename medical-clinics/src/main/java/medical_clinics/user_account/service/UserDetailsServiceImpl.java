package medical_clinics.user_account.service;

import lombok.AllArgsConstructor;
import medical_clinics.user_account.model.UserDetailsImpl;
import medical_clinics.user_account.repository.UserAccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername ( String email ) throws UsernameNotFoundException {
        return userAccountRepository
                .findByEmail ( email )
                .map ( UserDetailsImpl::new )
                .orElseThrow ( () ->
                        new UsernameNotFoundException (
                                "User with [%s] email not found".formatted ( email )
                        )
                );
    }


}
