package medical_clinics.user_account.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
public class UserDetailsImpl extends UserAccount implements UserDetails {

    private final UserAccount user;

    public UserDetailsImpl ( UserAccount user ) {
        this.user = user;
    }

    @Override
    public UUID getId () {
        return this.user.getId ( );
    }

    @Override
    public String getEmail () {
        return this.user.getEmail ( );
    }

    @Override
    public String getPassword () {
        return this.user.getPassword ( );
    }

    @Override
    public Role getRole () {
        return this.user.getRole ( );
    }

    @Override
    public UserStatus getStatus () {
        return this.user.getStatus ( );
    }

    @Override
    public boolean isMessagingBlocked () {
        return this.user.isMessagingBlocked ( );
    }

    @Override
    public String getConfirmationCode () {
        return this.user.getConfirmationCode ( );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities () {
        return List.of ( new SimpleGrantedAuthority ( "ROLE_" + user.getRole ( ) ) );
    }

    @Override
    public String getUsername () {
        return user.getEmail ( );
    }

    @Override
    public boolean isAccountNonExpired () {
        return !user.getStatus ( ).equals ( UserStatus.INACTIVE );
    }

    @Override
    public boolean isAccountNonLocked () {
        return !user.getStatus ( ).equals ( UserStatus.BLOCKED );
    }

    @Override
    public boolean isCredentialsNonExpired () {
        return !user.getStatus ( ).equals ( UserStatus.PENDING );
    }

    @Override
    public boolean isEnabled () {
        return user.getStatus ( ).equals ( UserStatus.ACTIVE );
    }
}
