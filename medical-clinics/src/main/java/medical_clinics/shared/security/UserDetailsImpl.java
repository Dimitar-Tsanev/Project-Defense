package medical_clinics.shared.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import medical_clinics.user_account.model.Role;
import medical_clinics.user_account.model.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Getter

public class UserDetailsImpl implements UserDetails {

    private UUID userId;
    private String email;
    private String password;
    private Role role;
    private UserStatus status;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities () {
        return List.of ( new SimpleGrantedAuthority ( "ROLE_" + role.name () ) );
    }

    @Override
    public String getUsername () {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired () {
        return !status.equals ( UserStatus.INACTIVE );
    }

    @Override
    public boolean isAccountNonLocked () {
        return !status.equals ( UserStatus.BLOCKED );
    }

    @Override
    public boolean isCredentialsNonExpired () {
        return status.equals ( UserStatus.ACTIVE);
    }

    @Override
    public boolean isEnabled () {
        return status.equals ( UserStatus.ACTIVE );
    }
}
