package medical_clinics.web;

import medical_clinics.user_account.service.UserAccountService;
import medical_clinics.web.dto.response.AccountInformation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserAccountController.class)
public class UserAccountControllerATests {

    @MockitoBean
    UserAccountService userAccountService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void when_getAllUsersAccountsAsAdmin_thenReturnsOk200 () throws Exception {

        when ( userAccountService.getAllAccounts ( ) ).thenReturn ( List.of ( new AccountInformation ( ) ) );

        MockHttpServletRequestBuilder request = get ( "/users/" )
                .with ( jwt ( )
                        .authorities ( new SimpleGrantedAuthority ( "ADMIN" ) )
                );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isOk ( ) )
                .andExpect ( jsonPath ( "$", hasSize ( 1 ) ) );

        verify ( userAccountService, times ( 1 ) ).getAllAccounts ( );
    }

    @Test
    void when_getAllUsersAccountsAnonymous_thenReturnsUnauthorized401 () throws Exception {
        when ( userAccountService.getAllAccounts ( ) ).thenReturn ( List.of ( new AccountInformation ( ) ) );

        MockHttpServletRequestBuilder request = get ( "/users/" )
                .with ( jwt ( ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isUnauthorized ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( userAccountService, never ( ) ).getAllAccounts ( );
    }

    @Test
    void when_getAllUsersAccountsAsPatient_thenReturnsForbidden403 () throws Exception {
        when ( userAccountService.getAllAccounts ( ) ).thenReturn ( List.of ( new AccountInformation ( ) ) );

        RequestPostProcessor sec = SecurityMockMvcRequestPostProcessors.jwt ( )
                .jwt ( jwt -> jwt.claim ( "scope", List.of ( "PATIENT" ) ) );

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

        when ( userAccountService.getAllAccounts ( ) ).thenReturn ( List.of ( new AccountInformation ( ) ) );

        MockHttpServletRequestBuilder request = get ( "/users/" )
                .with ( jwt ( )
                        .authorities ( new SimpleGrantedAuthority ( "PHYSICIAN" ) )
                );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isForbidden ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( userAccountService, never ( ) ).getAllAccounts ( );
    }
}
