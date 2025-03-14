package medical_clinics.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI () {
        return new OpenAPI ( )
                .info (
                        new Info ( ).title ( "Group of clinics demo application. Course defence project." )
                                .description (
                                        " The main role of the application is to allow patients " +
                                                "to make an appointments for physician as well as to provide " +
                                                "the patient with the ability to access their own medical record."
                                )
                                .version ( "v0.0.1" )
                                .contact (
                                        new Contact ( ).name ( "Dimitar Tsanev" ).url ( "https://github.com/Dimitar-Tsanev/" )
                                )
                )
                .servers (
                        List.of ( new Server ( ).url ( "http://localhost:8080/api/v1/" ).description ( "Development" ) )
                )
                .components (
                        new Components ( ).addSecuritySchemes ( "Bearer token",
                                new SecurityScheme ( )
                                        .type ( SecurityScheme.Type.HTTP )
                                        .in ( SecurityScheme.In.HEADER )
                                        .scheme ( "bearer" )
                                        .bearerFormat ( "JWT" ) )
                )
                .security ( List.of ( new SecurityRequirement ( ).addList ( "Bearer token" ) ) );
    }
}
