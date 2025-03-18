package medical_clinics.web.exception_handler;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder

public class ExceptionResponse {

    private Integer errorCode;
    private List<String> messages;

}
