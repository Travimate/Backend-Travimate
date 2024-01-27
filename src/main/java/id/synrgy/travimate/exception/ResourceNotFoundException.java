package id.synrgy.travimate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(UUID id) {
        super("Data dengan ID: " + id + " tidak ditemukan");
    }
    public ResourceNotFoundException(String name) {
        super("Data dengan Nama: " + name + " tidak ditemukan");
    }
}
