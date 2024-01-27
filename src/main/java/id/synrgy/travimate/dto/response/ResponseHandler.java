package id.synrgy.travimate.dto.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {
    public static ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object responseObj) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("status", status.value());
        map.put("data", responseObj);
        return new ResponseEntity<>(map,status);
    }
    public static ResponseEntity<Object> generateResponseSuccess(Object responseObj){
        return generateResponse(
                "success", HttpStatus.OK, responseObj
        );
    }
    public static ResponseEntity<Object> generateResponseFail(Exception e){
        return generateResponse(
                e.getMessage(), HttpStatus.BAD_REQUEST, null
        );
    }
}