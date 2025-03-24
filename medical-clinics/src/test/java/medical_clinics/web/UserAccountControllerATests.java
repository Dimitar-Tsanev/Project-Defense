package medical_clinics.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import medical_clinics.shared.config.SecurityConfig;
import medical_clinics.user_account.exceptions.UserAccountNotFoundException;
import medical_clinics.user_account.model.UserAccount;
import medical_clinics.user_account.repository.UserAccountRepository;
import medical_clinics.user_account.service.UserAccountService;
import medical_clinics.web.dto.UserAccountEditRequest;
import medical_clinics.web.dto.response.AccountInformation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(UserAccountController.class)
public class UserAccountControllerATests {

    @MockitoBean
    UserAccountService userAccountService;

    @MockitoBean
    UserAccountRepository userAccountRepository;


    @MockitoBean
    PasswordEncoder passwordEncoder;

    @Autowired
    MockMvc mockMvc;

    @Test
    void when_getAllUsersAccountsAsAdmin_thenReturnsOk200 () throws Exception {
        when ( userAccountService.getAllAccounts ( ) ).thenReturn ( List.of ( new AccountInformation ( ) ) );

        MockHttpServletRequestBuilder request = get ( "/users/" )
                .with ( jwt ( )
                        .authorities ( new SimpleGrantedAuthority ( "ROLE_ADMIN" ) ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isOk ( ) )
                .andExpect ( jsonPath ( "$", hasSize ( 1 ) ) );

        verify ( userAccountService, times ( 1 ) ).getAllAccounts ( );
    }

    @Test
    void when_getAllUsersAccountsAnonymous_thenReturnsUnauthorized401 () throws Exception {
        MockHttpServletRequestBuilder request = get ( "/users/" );

        mockMvc.perform ( request ).andExpect ( status ( ).isUnauthorized ( ) );

        verify ( userAccountService, never ( ) ).getAllAccounts ( );
    }

    @Test
    void when_getAllUsersAccountsAsPatient_thenReturnsForbidden403 () throws Exception {
        RequestPostProcessor sec = SecurityMockMvcRequestPostProcessors.jwt ( )
                .jwt ( jwt -> jwt.claim ( "scope", List.of ( "ROLE_PATIENT" ) ) );

        MockHttpServletRequestBuilder request = get ( "/users/" )
                .with ( sec );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isForbidden ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( userAccountService, never ( ) ).getAllAccounts ( );
    }

    @Test
    void when_getAllUsersAccountsAsPhysician_thenReturnsForbidden403 () throws Exception {
        MockHttpServletRequestBuilder request = get ( "/users/" )
                .with ( jwt ( )
                        .authorities ( new SimpleGrantedAuthority ( "ROLE_PHYSICIAN" ) )
                );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isForbidden ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( userAccountService, never ( ) ).getAllAccounts ( );
    }

    @Test
    void when_deleteUserAccount_withoutJWT_thenReturnsForbidden401 () throws Exception {
        UUID id = UUID.randomUUID ( );
        MockHttpServletRequestBuilder request = delete ( "/users/user/{id}", id );

        mockMvc.perform ( request ).andExpect ( status ( ).isUnauthorized ( ) );

        verify ( userAccountService, never ( ) ).deleteUserAccount ( any ( ) );
    }

    @Test
    void when_deleteUserAccount_withJWT_thenReturnsNoContent204 () throws Exception {
        UUID id = UUID.randomUUID ( );
        MockHttpServletRequestBuilder request = delete ( "/users/user/{id}", id )
                .with ( jwt ( )
                        .authorities ( new SimpleGrantedAuthority ( "ROLE_USER" ) )
                );

        mockMvc.perform ( request ).andExpect ( status ( ).isNoContent ( ) );

        verify ( userAccountService, times ( 1 ) ).deleteUserAccount ( any ( ) );
    }

    @Test
    void when_deleteUserAccount_withUserIdNotFound_thenReturnsNotFound404 () throws Exception {
        UUID id = UUID.randomUUID ( );

        doThrow ( new UserAccountNotFoundException ( "" ) ).when ( userAccountService ).deleteUserAccount ( any ( ) );

        MockHttpServletRequestBuilder request = delete ( "/users/user/{id}", id )
                .with ( jwt ( )
                        .authorities ( new SimpleGrantedAuthority ( "ROLE_USER" ) )
                );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isNotFound ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( userAccountService, times ( 1 ) ).deleteUserAccount ( any ( ) );
    }

    @Test
    void when_blockUserAccount_withInvalidJWT_thenReturnsUnauthorized401 () throws Exception {
        UUID id = UUID.randomUUID ( );

        MockHttpServletRequestBuilder request = patch ( "/users/user/ban/{id}", id );

        mockMvc.perform ( request ).andExpect ( status ( ).isUnauthorized ( ) );
        verify ( userAccountService, never ( ) ).blockUserAccount ( any ( ) );
    }

    @Test
    void when_blockUserAccount_withUnauthorizedRole_thenReturnsForbidden403 () throws Exception {
        UUID id = UUID.randomUUID ( );

        MockHttpServletRequestBuilder request = patch ( "/users/user/ban/{id}", id ).with ( jwt ( )
                .authorities ( new SimpleGrantedAuthority ( "ROLE_PHYSICIAN" ) )
        );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isForbidden ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( userAccountService, never ( ) ).blockUserAccount ( any ( ) );
    }

    @Test
    void when_blockUserAccount_withValidData_thenReturnsNoContent204 () throws Exception {
        UUID id = UUID.randomUUID ( );

        MockHttpServletRequestBuilder request = patch ( "/users/user/ban/{id}", id ).with ( jwt ( )
                .authorities ( new SimpleGrantedAuthority ( "ROLE_ADMIN" ) )
        );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isNoContent ( ) );

        verify ( userAccountService, times ( 1 ) ).blockUserAccount ( any ( ) );
    }

    @Test
    void when_blockUserAccount_withUserNotFound_thenReturnsNotFound404 () throws Exception {
        UUID id = UUID.randomUUID ( );

        MockHttpServletRequestBuilder request = patch ( "/users/user/ban/{id}", id ).with ( jwt ( )
                .authorities ( new SimpleGrantedAuthority ( "ROLE_PHYSICIAN" ) )
        );

        doThrow ( new UserAccountNotFoundException ( "" ) ).when ( userAccountService ).blockUserAccount ( any ( ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isForbidden ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( userAccountService, never ( ) ).blockUserAccount ( any ( ) );
    }

    @Test
    void when_switchRole_withInvalidJWT_thenReturnsUnauthorized401 () throws Exception {
        UUID id = UUID.randomUUID ( );

        MockHttpServletRequestBuilder request = patch ( "/users/user/{id}", id );

        mockMvc.perform ( request ).andExpect ( status ( ).isUnauthorized ( ) );
        verify ( userAccountService, never ( ) ).switchUserAccountRole ( any ( ) );
    }

    @Test
    void when_switchRole_withUnauthorizedRole_thenReturnsForbidden403 () throws Exception {
        UUID id = UUID.randomUUID ( );

        MockHttpServletRequestBuilder request = patch ( "/users/user/{id}", id )
                .with ( jwt ( )
                        .authorities ( new SimpleGrantedAuthority ( "ROLE_PHYSICIAN" ) )
                );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isForbidden ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( userAccountService, never ( ) ).switchUserAccountRole ( any ( ) );
    }

    @Test
    void when_switchRole_withValidData_thenReturnsNoContent204 () throws Exception {
        UUID id = UUID.randomUUID ( );

        MockHttpServletRequestBuilder request = patch ( "/users/user/{id}", id )
                .with ( jwt ( )
                        .authorities ( new SimpleGrantedAuthority ( "ROLE_ADMIN" ) )
                );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isNoContent ( ) );

        verify ( userAccountService, times ( 1 ) ).switchUserAccountRole ( any ( ) );
    }

    @Test
    void when_switchRole_withUserNotFound_thenReturnsNotFound404 () throws Exception {
        UUID id = UUID.randomUUID ( );

        MockHttpServletRequestBuilder request = patch ( "/users/user/{id}", id ).with ( jwt ( )
                .authorities ( new SimpleGrantedAuthority ( "ROLE_PHYSICIAN" ) )
        );

        doThrow ( new UserAccountNotFoundException ( "" ) ).when ( userAccountService ).switchUserAccountRole ( any ( ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isForbidden ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( userAccountService, never ( ) ).switchUserAccountRole ( any ( ) );
    }

    @Test
    void when_updateUserAccount_withValidMinimumParameters_thenReturnsNoContent204 () throws Exception {
        UUID id = UUID.randomUUID ( );

        UserAccountEditRequest userAccountEditRequest = new UserAccountEditRequest ( );
        userAccountEditRequest.setId ( id );
        userAccountEditRequest.setEmail ( "test@test.com" );
        userAccountEditRequest.setFirstName ( "test" );
        userAccountEditRequest.setLastName ( "test" );

        UserAccount account = UserAccount.builder ( )
                .id ( id )
                .password ( "!1Aaaaaa" )
                .build ( );

        when ( userAccountRepository.findById ( any ( ) ) ).thenReturn ( Optional.of ( account ) );

        MockHttpServletRequestBuilder request = put ( "/users/user/{id}", id )
                .with ( jwt ( )
                        .authorities ( new SimpleGrantedAuthority ( "ROLE_USER" ) )
                )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( new ObjectMapper ( ).writeValueAsBytes ( userAccountEditRequest ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isNoContent ( ) );

        verify ( userAccountService, times ( 1 ) ).editUserAccount ( any ( ), any ( ) );
        verify ( passwordEncoder, never ( ) ).matches ( any ( ), any ( ) );
    }

    @Test
    void when_updateUserAccount_withOnlyNewPassword_thenReturnsBadRequest401 () throws Exception {
        UUID id = UUID.randomUUID ( );

        UserAccountEditRequest userAccountEditRequest = new UserAccountEditRequest ( );
        userAccountEditRequest.setId ( id );
        userAccountEditRequest.setEmail ( "test@test.com" );
        userAccountEditRequest.setFirstName ( "test" );
        userAccountEditRequest.setLastName ( "test" );
        userAccountEditRequest.setNewPassword ( "Aaaaaaaa1!" );

        UserAccount account = UserAccount.builder ( )
                .id ( id )
                .password ( "!1Aaaaaa" )
                .build ( );

        when ( userAccountRepository.findById ( any ( ) ) ).thenReturn ( Optional.of ( account ) );

        MockHttpServletRequestBuilder request = put ( "/users/user/{id}", id )
                .with ( jwt ( )
                        .authorities ( new SimpleGrantedAuthority ( "ROLE_USER" ) )
                )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( new ObjectMapper ( ).writeValueAsBytes ( userAccountEditRequest ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isBadRequest ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( userAccountService, never ( ) ).editUserAccount ( any ( ), any ( ) );
        verify ( passwordEncoder, never ( ) ).matches ( any ( ), any ( ) );
    }

    @Test
    void when_updateUserAccount_withOldPasswordNoMach_thenReturnsBadRequest401 () throws Exception {
        UUID id = UUID.randomUUID ( );

        UserAccountEditRequest userAccountEditRequest = new UserAccountEditRequest ( );
        userAccountEditRequest.setId ( id );
        userAccountEditRequest.setEmail ( "test@test.com" );
        userAccountEditRequest.setFirstName ( "test" );
        userAccountEditRequest.setLastName ( "test" );
        userAccountEditRequest.setNewPassword ( "Aaaaaaaa1!" );
        userAccountEditRequest.setOldPassword ( "!1Aaaaaaa" );

        UserAccount account = UserAccount.builder ( )
                .id ( id )
                .password ( "!1Aaaaaa" )
                .build ( );

        when ( userAccountRepository.findById ( any ( ) ) ).thenReturn ( Optional.of ( account ) );
        when ( passwordEncoder.matches ( any ( ), any ( ) ) ).thenReturn ( false );

        MockHttpServletRequestBuilder request = put ( "/users/user/{id}", id )
                .with ( jwt ( )
                        .authorities ( new SimpleGrantedAuthority ( "ROLE_USER" ) )
                )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( new ObjectMapper ( ).writeValueAsBytes ( userAccountEditRequest ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isBadRequest ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( userAccountService, never ( ) ).editUserAccount ( any ( ), any ( ) );
        verify ( passwordEncoder, times ( 1 ) ).matches ( any ( ), any ( ) );
    }

    @Test
    void when_updateUserAccount_withUserNotFound_thenReturnsBadRequest401 () throws Exception {
        UUID id = UUID.randomUUID ( );

        UserAccountEditRequest userAccountEditRequest = new UserAccountEditRequest ( );
        userAccountEditRequest.setId ( id );
        userAccountEditRequest.setEmail ( "test@test.com" );
        userAccountEditRequest.setFirstName ( "test" );
        userAccountEditRequest.setLastName ( "test" );
        userAccountEditRequest.setNewPassword ( "Aaaaaaaa1!" );
        userAccountEditRequest.setOldPassword ( "!1Aaaaaaa" );

        when ( userAccountRepository.findById ( any ( ) ) ).thenReturn ( Optional.empty ( ) );

        MockHttpServletRequestBuilder request = put ( "/users/user/{id}", id )
                .with ( jwt ( )
                        .authorities ( new SimpleGrantedAuthority ( "ROLE_USER" ) )
                )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( new ObjectMapper ( ).writeValueAsBytes ( userAccountEditRequest ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isBadRequest ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( userAccountService, never ( ) ).editUserAccount ( any ( ), any ( ) );
        verify ( passwordEncoder, never ( ) ).matches ( any ( ), any ( ) );
    }

    @Test
    void when_updateUserAccount_withOldPasswordMatch_thenReturnsNoContent204 () throws Exception {
        UUID id = UUID.randomUUID ( );

        UserAccountEditRequest userAccountEditRequest = new UserAccountEditRequest ( );
        userAccountEditRequest.setId ( id );
        userAccountEditRequest.setEmail ( "test@test.com" );
        userAccountEditRequest.setFirstName ( "test" );
        userAccountEditRequest.setLastName ( "test" );
        userAccountEditRequest.setNewPassword ( "Aaaaaaaa1!" );
        userAccountEditRequest.setOldPassword ( "!1Aaaaaaa" );

        UserAccount account = UserAccount.builder ( )
                .id ( id )
                .password ( "!1Aaaaaa" )
                .build ( );

        when ( userAccountRepository.findById ( any ( ) ) ).thenReturn ( Optional.of ( account ) );
        when ( passwordEncoder.matches ( any ( ), any ( ) ) ).thenReturn ( true );

        MockHttpServletRequestBuilder request = put ( "/users/user/{id}", id )
                .with ( jwt ( )
                        .authorities ( new SimpleGrantedAuthority ( "ROLE_USER" ) )
                )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( new ObjectMapper ( ).writeValueAsBytes ( userAccountEditRequest ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isNoContent ( ) );

        verify ( userAccountService, times ( 1 ) ).editUserAccount ( any ( ), any ( ) );
        verify ( passwordEncoder, times ( 1 ) ).matches ( any ( ), any ( ) );
    }

    @Test
    void when_updateUserAccount_withUncaughtException_thenReturnsInternalServerError500 () throws Exception {

        UUID id = UUID.randomUUID ( );

        UserAccountEditRequest userAccountEditRequest = new UserAccountEditRequest ( );
        userAccountEditRequest.setId ( id );
        userAccountEditRequest.setEmail ( "test@test.com" );
        userAccountEditRequest.setFirstName ( "test" );
        userAccountEditRequest.setLastName ( "test" );
        userAccountEditRequest.setNewPassword ( "Aaaaaaaa1!" );
        userAccountEditRequest.setOldPassword ( "!1Aaaaaaa" );

        UserAccount account = UserAccount.builder ( )
                .id ( id )
                .password ( "!1Aaaaaa" )
                .build ( );

        when ( userAccountRepository.findById ( any ( ) ) ).thenReturn ( Optional.of ( account ) );
        when ( passwordEncoder.matches ( any ( ), any ( ) ) ).thenReturn ( true );

        doThrow ( new DataIntegrityViolationException ( "" ) )
                .when ( userAccountService )
                .editUserAccount ( any ( ), any ( ) );

        MockHttpServletRequestBuilder request = put ( "/users/user/{id}", id )
                .with ( jwt ( )
                        .authorities ( new SimpleGrantedAuthority ( "ROLE_USER" ) )
                )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( new ObjectMapper ( ).writeValueAsBytes ( userAccountEditRequest ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isInternalServerError () );

        verify ( userAccountService, times ( 1 ) ).editUserAccount ( any ( ), any ( ) );
        verify ( passwordEncoder, times ( 1 ) ).matches ( any ( ), any ( ) );
    }
}
