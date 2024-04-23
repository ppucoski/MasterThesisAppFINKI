package vp.magisterski.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class ThesisDoesNotExistException extends RuntimeException {
    public ThesisDoesNotExistException(String thesisId) {
        super(String.format("The thesis with ID: %s does not exist", thesisId));
    }
}