package medical_clinics.user_account.services;

import medical_clinics.shared.exception.PersonalInformationDontMatchException;
import medical_clinics.user_account.exceptions.UserAccountNotFoundException;
import medical_clinics.user_account.exceptions.UserAlreadyExistsException;
import medical_clinics.user_account.model.Role;
import medical_clinics.user_account.model.UserAccount;
import medical_clinics.user_account.model.UserStatus;
import medical_clinics.user_account.repository.UserAccountRepository;
import medical_clinics.user_account.service.UserAccountService;
import medical_clinics.web.dto.UserAccountEditRequest;
import medical_clinics.web.dto.response.AccountInformation;
import medical_clinics.web.dto.response.UserDataResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAccountServiceUTest {

    @Mock
    UserAccountRepository userAccountRepository;

    @InjectMocks
    UserAccountService userAccountService;


    @Test
    void when_loadUserByUsername_withUserExists_thenReturnUserDetails () {
        String email = "test@test.com";
        UUID id = UUID.randomUUID ( );
        String password = "!1Aaaaaaaa";

        UserAccount userAccount = UserAccount.builder ( )
                .id ( id )
                .email ( email )
                .password ( password )
                .role ( Role.PHYSICIAN )
                .status ( UserStatus.ACTIVE )
                .build ( );

        when ( userAccountRepository.findByEmail ( email ) ).thenReturn ( Optional.of ( userAccount ) );

        UserDetails userDetails = userAccountService.loadUserByUsername ( email );

        assertEquals ( email, userDetails.getUsername ( ) );
        assertEquals ( password, userDetails.getPassword ( ) );
        assertEquals ( 1, userDetails.getAuthorities ( ).size ( ) );
        assertEquals ( Role.PHYSICIAN.name ( ), userDetails.getAuthorities ( ).iterator ( ).next ( ).getAuthority ( ) );
        assertTrue ( userDetails.isAccountNonExpired ( ) );
        assertTrue ( userDetails.isAccountNonLocked ( ) );
        assertTrue ( userDetails.isCredentialsNonExpired ( ) );
        assertTrue ( userDetails.isEnabled ( ) );

        verify ( userAccountRepository, times ( 1 ) ).findByEmail ( email );
    }

    @Test
    void when_loadUserByUsername_withUserBlocked_thenReturnUserDetails () {
        String email = "test@test.com";
        UUID id = UUID.randomUUID ( );
        String password = "!1Aaaaaaaa";

        UserAccount userAccount = UserAccount.builder ( )
                .id ( id )
                .email ( email )
                .password ( password )
                .role ( Role.PHYSICIAN )
                .status ( UserStatus.BLOCKED )
                .build ( );

        when ( userAccountRepository.findByEmail ( email ) ).thenReturn ( Optional.of ( userAccount ) );

        UserDetails userDetails = userAccountService.loadUserByUsername ( email );

        assertEquals ( email, userDetails.getUsername ( ) );
        assertEquals ( password, userDetails.getPassword ( ) );
        assertEquals ( 1, userDetails.getAuthorities ( ).size ( ) );
        assertEquals ( Role.PHYSICIAN.name ( ), userDetails.getAuthorities ( ).iterator ( ).next ( ).getAuthority ( ) );
        assertTrue ( userDetails.isAccountNonExpired ( ) );
        assertFalse ( userDetails.isAccountNonLocked ( ) );
        assertFalse ( userDetails.isCredentialsNonExpired ( ) );
        assertFalse ( userDetails.isEnabled ( ) );

        verify ( userAccountRepository, times ( 1 ) ).findByEmail ( email );
    }

    @Test
    void when_loadUserByUsername_withUserInactive_thenReturnUserDetails () {
        String email = "test@test.com";
        UUID id = UUID.randomUUID ( );
        String password = "!1Aaaaaaaa";

        UserAccount userAccount = UserAccount.builder ( )
                .id ( id )
                .email ( email )
                .password ( password )
                .role ( Role.PHYSICIAN )
                .status ( UserStatus.INACTIVE )
                .build ( );

        when ( userAccountRepository.findByEmail ( email ) ).thenReturn ( Optional.of ( userAccount ) );

        UserDetails userDetails = userAccountService.loadUserByUsername ( email );

        assertEquals ( email, userDetails.getUsername ( ) );
        assertEquals ( password, userDetails.getPassword ( ) );
        assertEquals ( 1, userDetails.getAuthorities ( ).size ( ) );
        assertEquals ( Role.PHYSICIAN.name ( ), userDetails.getAuthorities ( ).iterator ( ).next ( ).getAuthority ( ) );
        assertFalse ( userDetails.isAccountNonExpired ( ) );
        assertTrue ( userDetails.isAccountNonLocked ( ) );
        assertFalse ( userDetails.isCredentialsNonExpired ( ) );
        assertFalse ( userDetails.isEnabled ( ) );

        verify ( userAccountRepository, times ( 1 ) ).findByEmail ( email );
    }

    @Test
    void when_loadUserByUsername_withUserNotFound_thenThrowsException () {

        when ( userAccountRepository.findByEmail ( any ( ) ) ).thenReturn ( Optional.empty ( ) );

        assertThrows (
                UsernameNotFoundException.class,
                () -> userAccountService.loadUserByUsername ( "test@test" )
        );

        verify ( userAccountRepository, times ( 1 ) ).findByEmail ( "test@test" );
    }

    @Test
    void when_getById_withIdFound_thenReturnUserAccount () {
        String email = "test@test.com";
        UUID id = UUID.randomUUID ( );

        UserAccount userAccount = UserAccount.builder ( )
                .id ( id )
                .email ( email )
                .build ( );

        when ( userAccountRepository.findById ( id ) ).thenReturn ( Optional.of ( userAccount ) );

        UserAccount account = userAccountService.getById ( id );

        assertEquals ( email, account.getEmail ( ) );
        assertEquals ( id, account.getId ( ) );

        verify ( userAccountRepository, times ( 1 ) ).findById ( id );
    }

    @Test
    void when_when_getById_withIdFound_thenThrowsException () {

        when ( userAccountRepository.findById ( any ( ) ) ).thenReturn ( Optional.empty ( ) );

        assertThrows (
                UserAccountNotFoundException.class,
                () -> userAccountService.getById ( UUID.randomUUID ( ) )
        );

        verify ( userAccountRepository, times ( 1 ) ).findById ( any ( ) );
    }

    @Test
    void when_getAllAccounts_ReturnsAllAccounts () {
        List<UserAccount> accounts = List.of (
                new UserAccount ( ), new UserAccount ( ), new UserAccount ( ), new UserAccount ( )
        );

        when ( userAccountRepository.findAll ( ) ).thenReturn ( accounts );

        List<AccountInformation> accountsInformation = userAccountService.getAllAccounts ( );

        assertEquals ( accounts.size ( ), accountsInformation.size ( ) );

        verify ( userAccountRepository, times ( 1 ) ).findAll ( );
    }

    @Test
    void when_blockUserAccount_BlocksUserAccount () {
        String email = "test@test.com";
        UUID id = UUID.randomUUID ( );

        UserAccount userAccount = UserAccount.builder ( )
                .id ( id )
                .email ( email )
                .role ( Role.PHYSICIAN )
                .status ( UserStatus.ACTIVE )
                .build ( );

        when ( userAccountRepository.findById ( id ) ).thenReturn ( Optional.of ( userAccount ) );

        userAccountService.blockUserAccount ( id );

        verify ( userAccountRepository, times ( 1 ) ).findById ( id );
        verify ( userAccountRepository, times ( 1 ) ).save ( userAccount );
        verify ( userAccountRepository, never ( ) ).delete ( any ( ) );
    }

    @Test
    void when_blockUserAccount_IdNotFound_thenThrowsException () {
        when ( userAccountRepository.findById ( any ( ) ) ).thenReturn ( Optional.empty ( ) );

        assertThrows ( UserAccountNotFoundException.class,
                () -> userAccountService.blockUserAccount ( UUID.randomUUID ( ) ) );

        verify ( userAccountRepository, times ( 1 ) ).findById ( any ( ) );
        verify ( userAccountRepository, never ( ) ).save ( any ( ) );
        verify ( userAccountRepository, never ( ) ).delete ( any ( ) );
    }

    @Test
    void when_deleteUserAccount_BlocksUserAccount () {
        String email = "test@test.com";
        UUID id = UUID.randomUUID ( );

        UserAccount userAccount = UserAccount.builder ( )
                .id ( id )
                .email ( email )
                .role ( Role.PHYSICIAN )
                .status ( UserStatus.ACTIVE )
                .build ( );

        when ( userAccountRepository.findById ( id ) ).thenReturn ( Optional.of ( userAccount ) );

        userAccountService.deleteUserAccount ( id );

        verify ( userAccountRepository, times ( 1 ) ).findById ( id );
        verify ( userAccountRepository, times ( 1 ) ).save ( userAccount );
        verify ( userAccountRepository, never ( ) ).delete ( any ( ) );
    }

    @Test
    void when_deleteUserAccount_IdNotFound_thenThrowsException () {
        when ( userAccountRepository.findById ( any ( ) ) ).thenReturn ( Optional.empty ( ) );

        assertThrows ( UserAccountNotFoundException.class,
                () -> userAccountService.deleteUserAccount ( UUID.randomUUID ( ) ) );

        verify ( userAccountRepository, times ( 1 ) ).findById ( any ( ) );
        verify ( userAccountRepository, never ( ) ).save ( any ( ) );
        verify ( userAccountRepository, never ( ) ).delete ( any ( ) );
    }

    @Test
    void when_getAccountData_withValidEmail_ReturnsAccountData () {
        String email = "test@test.com";
        UUID id = UUID.randomUUID ( );

        UserAccount userAccount = UserAccount.builder ( )
                .id ( id )
                .email ( email )
                .role ( Role.PHYSICIAN )
                .build ( );

        when ( userAccountRepository.findByEmail ( email ) ).thenReturn ( Optional.of ( userAccount ) );

        UserDataResponse userDataResponse = userAccountService.getAccountData ( email );

        assertEquals ( id, userDataResponse.getAccountId ( ) );
        assertEquals ( Role.PHYSICIAN, userDataResponse.getRole ( ) );
        assertNull ( userDataResponse.getPatientInfo ( ) );

        verify ( userAccountRepository, times ( 1 ) ).findByEmail ( email );
    }

    @Test
    void when_getAccountData_withNotFound_ReturnsAccountData () {
        when ( userAccountRepository.findByEmail ( any ( ) ) ).thenReturn ( Optional.empty ( ) );

        assertThrows ( UserAccountNotFoundException.class,
                () -> userAccountService.getAccountData ( "test@test.com" ) );

        verify ( userAccountRepository, times ( 1 ) ).findByEmail ( any ( ) );
    }

    @Test
    void when_editUserAccount_withFormAndParameterDifference_thenExpectThrowException () {
        UserAccountEditRequest editRequest = UserAccountEditRequest.builder ( )
                .id ( UUID.randomUUID ( ) )
                .build ( );

        assertThrows ( PersonalInformationDontMatchException.class,
                () -> userAccountService.editUserAccount ( UUID.randomUUID ( ), editRequest ) );
        verify ( userAccountRepository, never ( ) ).findById ( any ( ) );
        verify ( userAccountRepository, never ( ) ).findByEmail ( any ( ) );
        verify ( userAccountRepository, never ( ) ).save ( any ( ) );
    }

    @Test
    void when_editUserAccount_withIdNotFound_thenExpectThrowException () {
        UUID id = UUID.randomUUID ( );
        UserAccountEditRequest editRequest = UserAccountEditRequest.builder ( )
                .id ( id )
                .build ( );

        when ( userAccountRepository.findById ( id ) ).thenReturn ( Optional.empty ( ) );

        assertThrows ( UserAccountNotFoundException.class,
                () -> userAccountService.editUserAccount ( id, editRequest ) );

        verify ( userAccountRepository, times ( 1 ) ).findById ( id );
        verify ( userAccountRepository, never ( ) ).save ( any ( ) );
    }

    @Test
    void when_editUserAccount_WithChangeEmailButInOtherAccount_thenExpectThrow () {
        UUID id = UUID.randomUUID ( );
        String email = "test@test.com";

        UserAccountEditRequest editRequest = UserAccountEditRequest.builder ( )
                .id ( id )
                .email ( email )
                .build ( );

        when ( userAccountRepository.findById ( id ) ).thenReturn (
                Optional.of ( UserAccount.builder ( ).email ( "some@mail.com" ).build ( ) )
        );

        when ( userAccountRepository.findByEmail ( any ( ) ) ).thenReturn ( Optional.of ( new UserAccount ( ) ) );

        assertThrows ( UserAlreadyExistsException.class, () -> userAccountService.editUserAccount ( id, editRequest ) );
        verify ( userAccountRepository, times ( 1 ) ).findById ( id );
        verify ( userAccountRepository, times ( 1 ) ).findByEmail ( email );
        verify ( userAccountRepository, never ( ) ).save ( any ( ) );
    }
}
