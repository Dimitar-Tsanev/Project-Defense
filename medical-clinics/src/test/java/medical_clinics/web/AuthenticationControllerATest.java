package medical_clinics.web;


import com.fasterxml.jackson.databind.ObjectMapper;
import medical_clinics.patient.exceptions.PatientNotFoundException;
import medical_clinics.patient.service.PatientService;
import medical_clinics.shared.security.AuthenticationService;
import medical_clinics.user_account.exceptions.UserAccountNotFoundException;
import medical_clinics.user_account.exceptions.UserAlreadyExistsException;
import medical_clinics.user_account.model.Role;
import medical_clinics.user_account.service.UserAccountService;
import medical_clinics.web.dto.LoginRequest;
import medical_clinics.web.dto.RegisterRequest;
import medical_clinics.web.dto.response.PatientInfo;
import medical_clinics.web.dto.response.UserDataResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerATest {

    @MockitoBean
    UserAccountService userAccountService;

    @MockitoBean
    AuthenticationService authenticationService;

    @MockitoBean
    PatientService patientService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void when_register_WithValidRequest_thenShouldReturnCreated201 () throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder ( )
                .email ( "example@example.com" )
                .password ( "!1Aaaaaa" )
                .firstName ( "Example" )
                .lastName ( "Example" )
                .build ( );

        MockHttpServletRequestBuilder request = post ( "/auth/register" )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( new ObjectMapper ( ).writeValueAsBytes ( registerRequest ) )
                .with ( jwt ( ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isCreated ( ) )
                .andExpect ( header ( ).string ( HttpHeaders.LOCATION, "http://localhost:8080/api/v0/auth/login" ) );

        verify ( userAccountService, times ( 1 ) ).register ( any ( ) );
    }

    @Test
    void when_register_WithInvalidBlankFiled_thenShouldReturnBadRequest400 () throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder ( )
                .email ( " " )
                .password ( "" )
                .firstName ( null )
                .lastName ( null )
                .build ( );

        MockHttpServletRequestBuilder request = post ( "/auth/register" )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( new ObjectMapper ( ).writeValueAsBytes ( registerRequest ) )
                .with ( jwt ( ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isBadRequest ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( userAccountService, never ( ) ).register ( any ( ) );
    }

    @Test
    void when_register_WithInvalidSize_thenShouldReturnBadRequest400 () throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder ( )
                .email ( "example@example.com" )
                .password ( "!1Aa" )
                .firstName ( "A" )
                .lastName ( "A" )
                .phone ( "+12" )
                .build ( );

        MockHttpServletRequestBuilder request = post ( "/auth/register" )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( new ObjectMapper ( ).writeValueAsBytes ( registerRequest ) )
                .with ( jwt ( ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isBadRequest ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( userAccountService, never ( ) ).register ( any ( ) );
    }

    @Test
    void when_register_WithInvalidPattern_thenShouldReturnBadRequest400 () throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder ( )
                .email ( "example@example" )
                .password ( "aaaaaaaaaaaaa" )
                .firstName ( "Example" )
                .lastName ( "Example" )
                .phone ( "A123456789" )
                .build ( );

        MockHttpServletRequestBuilder request = post ( "/auth/register" )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( new ObjectMapper ( ).writeValueAsBytes ( registerRequest ) )
                .with ( jwt ( ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isBadRequest ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( userAccountService, never ( ) ).register ( any ( ) );
    }

    @Test
    void when_register_WithUsedEmail_thenShouldReturnBadRequest400 () throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder ( )
                .email ( "example@example.com" )
                .password ( "!1Aaaaaa" )
                .firstName ( "Example" )
                .lastName ( "Example" )
                .build ( );

        doThrow ( new UserAlreadyExistsException ( "" ) ).when ( userAccountService ).register ( any ( ) );

        MockHttpServletRequestBuilder request = post ( "/auth/register" )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( new ObjectMapper ( ).writeValueAsBytes ( registerRequest ) )
                .with ( jwt ( ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isConflict ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( userAccountService, times ( 1 ) ).register ( any ( ) );
    }

    @Test
    void when_login_WithValidRequest_thenShouldReturnOk200 () throws Exception {
        LoginRequest loginRequest = LoginRequest.builder ( )
                .email ( "example@example.com" )
                .password ( "!1Aaaaaa" )
                .build ( );

        UserDataResponse userData = new UserDataResponse ( );
        userData.setRole ( Role.PATIENT );
        userData.setAccountId ( UUID.randomUUID ( ) );

        when ( authenticationService.authenticate ( any ( ) ) ).thenReturn ( "" );
        when ( userAccountService.getAccountData ( any ( ) ) ).thenReturn ( userData );
        when ( patientService.getPatientInfoByUserAccountId ( any ( ) ) ).thenReturn ( new PatientInfo ( ) );

        MockHttpServletRequestBuilder request = post ( "/auth/login" )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( new ObjectMapper ( ).writeValueAsBytes ( loginRequest ) )
                .with ( jwt ( ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isOk ( ) )
                .andExpect ( header ( ).exists ( HttpHeaders.AUTHORIZATION ) )
                .andExpect ( jsonPath ( "accountId" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "role" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "patientInfo" ).isMap () );

        verify ( authenticationService,times ( 1 ) ).authenticate ( any () );
        verify ( userAccountService, times ( 1 ) ).getAccountData ( any ( ) );
        verify ( patientService, times ( 1 ) ).getPatientInfoByUserAccountId ( any ( ) );
    }

    @Test
    void when_login_WithBlankParameters_thenShouldReturnBadRequest400 () throws Exception {
        LoginRequest loginRequest = LoginRequest.builder ( )
                .email ( "" )
                .password ( "              " )
                .build ( );

        MockHttpServletRequestBuilder request = post ( "/auth/login" )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( new ObjectMapper ( ).writeValueAsBytes ( loginRequest ) )
                .with ( jwt ( ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isBadRequest ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( authenticationService,never () ).authenticate ( any () );
        verify ( userAccountService, never () ).getAccountData ( any ( ) );
        verify ( patientService, never () ).getPatientInfoByUserAccountId ( any ( ) );
    }


    @Test
    void when_login_WithInvalidParameters_thenShouldReturnBadRequest400 () throws Exception {
        LoginRequest loginRequest = LoginRequest.builder ( )
                .email ( "example@example" )
                .password ( "A1aaaaaa")
                .build ( );

        MockHttpServletRequestBuilder request = post ( "/auth/login" )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( new ObjectMapper ( ).writeValueAsBytes ( loginRequest ) )
                .with ( jwt ( ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isBadRequest ( ) )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( authenticationService,never () ).authenticate ( any () );
        verify ( userAccountService, never () ).getAccountData ( any ( ) );
        verify ( patientService, never () ).getPatientInfoByUserAccountId ( any ( ) );
    }

    @Test
    void when_login_WithUserDetailsNotFound_thenShouldReturnUnauthorized401 () throws Exception {
        LoginRequest loginRequest = LoginRequest.builder ( )
                .email ( "example@example.com" )
                .password ( "!1Aaaaaa" )
                .build ( );

        MockHttpServletRequestBuilder request = post ( "/auth/login" )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( new ObjectMapper ( ).writeValueAsBytes ( loginRequest ) )
                .with ( jwt ( ) );

        when ( authenticationService.authenticate ( any ( ) ) ).thenThrow (  new UsernameNotFoundException ( "" ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isUnauthorized ());

        verify ( authenticationService, times ( 1 ) ).authenticate ( any () );
        verify ( userAccountService, never () ).getAccountData ( any ( ) );
        verify ( patientService, never () ).getPatientInfoByUserAccountId ( any ( ) );
    }

    @Test
    void when_login_WithUserNotFound_thenShouldReturnNotFound404 () throws Exception {
        LoginRequest loginRequest = LoginRequest.builder ( )
                .email ( "example@example.com" )
                .password ( "!1Aaaaaa" )
                .build ( );

        MockHttpServletRequestBuilder request = post ( "/auth/login" )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( new ObjectMapper ( ).writeValueAsBytes ( loginRequest ) )
                .with ( jwt ( ) );

        when ( authenticationService.authenticate ( any ( ) ) ).thenReturn ( "" );
        when ( userAccountService.getAccountData ( any ( ) ) ).thenThrow ( new UserAccountNotFoundException ( ""));

        mockMvc.perform ( request )
                .andExpect ( status ( ).isNotFound () )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( authenticationService, times ( 1 ) ).authenticate ( any () );
        verify ( userAccountService, times ( 1 )).getAccountData ( any ( ) );
        verify ( patientService, never () ).getPatientInfoByUserAccountId ( any ( ) );
    }

    @Test
    void when_login_WithPatientDataNotFound_thenShouldReturnNotFound404 () throws Exception {
        LoginRequest loginRequest = LoginRequest.builder ( )
                .email ( "example@example.com" )
                .password ( "!1Aaaaaa" )
                .build ( );

        MockHttpServletRequestBuilder request = post ( "/auth/login" )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( new ObjectMapper ( ).writeValueAsBytes ( loginRequest ) )
                .with ( jwt ( ) );

        when ( authenticationService.authenticate ( any ( ) ) ).thenReturn ( "" );
        when ( userAccountService.getAccountData ( any ( ) ) ).thenReturn ( new UserDataResponse () );
        when ( patientService.getPatientInfoByUserAccountId ( any ( ) ) ).thenThrow ( new PatientNotFoundException("") );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isNotFound () )
                .andExpect ( jsonPath ( "errorCode" ).isNotEmpty ( ) )
                .andExpect ( jsonPath ( "messages" ).isNotEmpty ( ) );

        verify ( authenticationService, times ( 1 ) ).authenticate ( any () );
        verify ( userAccountService, times ( 1 )).getAccountData ( any ( ) );
        verify ( patientService, times ( 1 ) ).getPatientInfoByUserAccountId ( any ( ) );
    }

    @Test
    void when_login_WithAuthenticationFailed_thenShouldReturnUnauthorized401 () throws Exception {
        LoginRequest loginRequest = LoginRequest.builder ( )
                .email ( "example@example.com" )
                .password ( "!1Aaaaaa" )
                .build ( );

        MockHttpServletRequestBuilder request = post ( "/auth/login" )
                .contentType ( MediaType.APPLICATION_JSON )
                .content ( new ObjectMapper ( ).writeValueAsBytes ( loginRequest ) )
                .with ( jwt ( ) );

        when ( authenticationService.authenticate ( any ( ) ) ).thenThrow ( new BadCredentialsException ( "" ) );

        mockMvc.perform ( request )
                .andExpect ( status ( ).isUnauthorized () )
                .andExpect ( jsonPath ( "errorCode" ).value ( "401" ) )
                .andExpect ( jsonPath ( "messages" ).value ( "Invalid email or password" ) );

        verify ( authenticationService, times ( 1 ) ).authenticate ( any () );
        verify ( userAccountService, never ()).getAccountData ( any ( ) );
        verify ( patientService, never () ).getPatientInfoByUserAccountId ( any ( ) );
    }
}
