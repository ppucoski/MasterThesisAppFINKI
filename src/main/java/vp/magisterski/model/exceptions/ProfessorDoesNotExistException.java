package vp.magisterski.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProfessorDoesNotExistException extends RuntimeException {
    public ProfessorDoesNotExistException(String professorId) {
        super(String.format("The professor with ID: %s does not exist", professorId));
    }
}