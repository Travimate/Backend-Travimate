package id.synrgy.travimate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ExistingResourceFoundException extends RuntimeException {
    public ExistingResourceFoundException(UUID id) {
        super("Data dengan ID: " + id + " sudah ada");
    }
    public ExistingResourceFoundException(String name) {
        super("Data dengan Nama: " + name + " sudah ada");
    }
}
