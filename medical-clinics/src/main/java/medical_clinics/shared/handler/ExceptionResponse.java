package medical_clinics.shared.handler;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder

public class ExceptionResponse {

    private Integer errorCode;
    private List<String> messages;

}
