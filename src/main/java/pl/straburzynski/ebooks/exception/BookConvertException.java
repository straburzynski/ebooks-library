package pl.straburzynski.ebooks.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BookConvertException extends RuntimeException {
    public BookConvertException(String message) {
        super(message);
    }
}
