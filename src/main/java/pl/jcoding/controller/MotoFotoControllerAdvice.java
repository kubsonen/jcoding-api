package pl.jcoding.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.jcoding.util.ApiException;
import pl.jcoding.util.ApiExceptionCode;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice(assignableTypes = MotoFotoController.class)
public class MotoFotoControllerAdvice extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MotoFotoControllerAdvice.class);

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleUnhandled(Throwable t) {

        Throwable cause = t.getCause();
        if (cause != null && cause instanceof ApiException) {
            ApiException ae = (ApiException) cause;
            LOGGER.error("Handle Api Exception");
            HttpStatus status = HttpStatus.BAD_REQUEST;
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("status", status.value());
            body.put("errorCode", ae.getCode().getErrorCode());
            body.put("code", ae.getCode().name());
            return ResponseEntity.badRequest().body(body);
        }

        LOGGER.error("Handle unhandled error");
        t.printStackTrace();
        Map<String, Object> body = new LinkedHashMap<>();
        ApiExceptionCode code = ApiExceptionCode.UNHANDLED_ERROR;
        body.put("errorCode", code.getErrorCode());
        body.put("code", code.name());
        return ResponseEntity.badRequest().body("");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        LOGGER.error("Handle validation error");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("errors", ex.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList()));
        return new ResponseEntity<>(body, headers, status);
    }

}
