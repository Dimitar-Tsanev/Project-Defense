package medical_clinics.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import medical_clinics.patient.service.PatientService;
import medical_clinics.shared.security.AuthenticationService;
import medical_clinics.user_account.service.UserAccountService;
import medical_clinics.web.dto.LoginRequest;
import medical_clinics.web.dto.RegisterRequest;
import medical_clinics.web.dto.response.UserDataResponse;
import medical_clinics.web.exception_handler.ExceptionResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserAccountService userAccountService;
    private final PatientService patientService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "Create a new user account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User account created successfully",
                    headers = @Header(name = HttpHeaders.LOCATION,
                            description = "Login link")
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "409", description = "Conflict with another user data",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
    })
    @PostMapping("/register")
    public ResponseEntity<String> register ( @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User creation account information", required = true,
            content = @Content(schema = @Schema(implementation = RegisterRequest.class),
                    examples = @ExampleObject(value =
                            "{ " +
                                    "\"email\": \"example@example.com\"," +
                                    "\"password\": \"1Abcdef?\"," +
                                    "\"firstName\": \"John\"," +
                                    "\"lastName\": \"Doe\"," +
                                    "\"phone\": \"+123456789\" " +
                                    " }"
                    )
            )) @RequestBody @Valid RegisterRequest request ) {

        userAccountService.register ( request );
        String location = "http://localhost:8080/api/v1/auth/login";

        return ResponseEntity.status ( HttpStatus.CREATED )
                .header ( HttpHeaders.LOCATION, location )
                .body ( "Successfully registered" );
    }

    @Operation(summary = "Login user in our system (send authorization token)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User login successfully",
                    content = @Content(schema = @Schema(implementation = UserDataResponse.class)),
                    headers = @Header(name = HttpHeaders.AUTHORIZATION, description = "contain jwt bearer token")
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "User data not found",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "User not authenticated",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
    })
    @PostMapping("/login")
    public ResponseEntity<UserDataResponse> login ( @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User login information", required = true,
            content = @Content(schema = @Schema(implementation = LoginRequest.class),
                    examples = @ExampleObject(value =
                            "{ " +
                                    "\"email\": \"example@example.com\"," +
                                    "\"password\": \"1Abcdef?\"" +
                                    " }"
                    )
            )) @RequestBody @Valid LoginRequest request ) {

        String token = authenticationService.authenticate ( request );

        UserDataResponse userData = userAccountService.getAccountData ( request.getEmail ( ) );

        userData.setPatientInfo ( patientService.getPatientInfoByUserAccountId ( userData.getAccountId () ) );


        return ResponseEntity.ok ( ).header ( HttpHeaders.AUTHORIZATION, token ).body ( userData );
    }
}
