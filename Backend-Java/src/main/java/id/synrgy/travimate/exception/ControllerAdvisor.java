package id.synrgy.travimate.exception;

import id.synrgy.travimate.dto.response.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@RestControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceWithIdNotFoundException(ResourceNotFoundException exception, WebRequest req) {
        logger.info(exception.getMessage());
        return ResponseHandler.generateResponse(
                exception.getMessage(), HttpStatus.NOT_FOUND, null);
    }
    @ExceptionHandler(ExistingResourceFoundException.class)
    public ResponseEntity<?> handleResourceExistingFoundException(ExistingResourceFoundException exception, WebRequest req) {
        logger.info(exception.getMessage());
        return ResponseHandler.generateResponse(
                exception.getMessage(), HttpStatus.MULTI_STATUS, null);
    }

}
