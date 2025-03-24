package medical_clinics.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import medical_clinics.user_account.service.UserAccountService;
import medical_clinics.web.dto.UserAccountEditRequest;
import medical_clinics.web.dto.response.AccountInformation;
import medical_clinics.web.exception_handler.ExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor

@RestController
@RequestMapping("/users")
public class UserAccountController {
    private final UserAccountService userAccountService;

    @Operation(
            summary = "Update user information",
            security = @SecurityRequirement(name = "Bearer token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User edited successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Bearer token not found or invalid",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "User data not found",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "409", description = "Conflict with data from other user",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @PutMapping("/user/{accountId}")
    public ResponseEntity<Void> updateUserAccount ( @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Information for user data update ", required = true,
            content = @Content(schema = @Schema(implementation = UserAccountEditRequest.class)
            )) @PathVariable UUID accountId, @RequestBody @Valid UserAccountEditRequest request ) {

        userAccountService.editUserAccount ( accountId, request );
        return ResponseEntity.noContent ( ).build ( );
    }

    @Operation(
            summary = "Update user role",
            security = @SecurityRequirement(name = "Bearer token", scopes = "ROLE_ADMIN")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User role changed successfully"),
            @ApiResponse(responseCode = "401", description = "Bearer token not found or invalid",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "User data not found",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @PatchMapping("/user/{accountId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> switchRole ( @PathVariable UUID accountId ) {
        userAccountService.switchUserAccountRole ( accountId );
        return ResponseEntity.noContent ( ).build ( );
    }

    @Operation(
            summary = "Block user account",
            security = @SecurityRequirement(name = "Bearer token", scopes = "ROLE_ADMIN")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User blocked"),
            @ApiResponse(responseCode = "401", description = "Bearer token not found or invalid",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "User data not found",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @PatchMapping("/user/ban/{accountId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> blockUserAccount ( @PathVariable UUID accountId ) {
        userAccountService.blockUserAccount ( accountId );
        return ResponseEntity.noContent ( ).build ( );
    }

    @Operation(
            summary = "Delete (inactivate) user account",
            security = @SecurityRequirement(name = "Bearer token")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Inactivated successfully"),
            @ApiResponse(responseCode = "401", description = "Bearer token not found or invalid",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "User data not found",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @DeleteMapping("/user/{accountId}")
    public ResponseEntity<Void> deleteUserAccount ( @PathVariable UUID accountId ) {
        userAccountService.deleteUserAccount ( accountId );
        return ResponseEntity.noContent ( ).build ( );
    }

    @Operation(
            summary = "Get all users information",
            security = @SecurityRequirement(name = "Bearer token", scopes = "ROLE_ADMIN")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = AccountInformation[].class))
            ),
            @ApiResponse(responseCode = "401", description = "Bearer token not found or invalid",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccountInformation>> getAllUsersAccounts () {
        return ResponseEntity.ok ( userAccountService.getAllAccounts ( ) );
    }
}
