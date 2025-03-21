package medical_clinics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
@ConfigurationPropertiesScan
public class MedicalClinicsApplication {

    public static void main ( String[] args ) {
        SpringApplication.run ( MedicalClinicsApplication.class, args );
    }
}
