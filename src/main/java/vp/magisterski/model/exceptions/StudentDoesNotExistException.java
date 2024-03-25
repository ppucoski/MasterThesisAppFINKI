package vp.magisterski.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class StudentDoesNotExistException extends RuntimeException {
    public StudentDoesNotExistException(String studentIndex) {
        super(String.format("The student with ID: %s does not exist", studentIndex));
    }
}
