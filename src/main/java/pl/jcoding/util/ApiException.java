package pl.jcoding.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiException extends Throwable {

    private ApiExceptionCode code;

    public ApiException() {
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiException(Throwable cause) {
        super(cause);
    }

    public ApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static ApiException of (Throwable cause, ApiExceptionCode code) {
        ApiException ae = new ApiException(cause);
        ae.setCode(code);
        return ae;
    }

    public static ApiException of (ApiExceptionCode code) {
        ApiException ae = new ApiException();
        ae.setCode(code);
        return ae;
    }

}
