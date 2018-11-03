package pl.straburzynski.ebooks.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CategoryFoundException extends RuntimeException {
    public CategoryFoundException(String message) {
        super(message);
    }
}
