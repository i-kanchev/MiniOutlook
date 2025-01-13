package bg.sofia.uni.fmi.mjt.mail.exceptions;

import javax.lang.model.UnknownEntityException;

public class InvalidPathException extends UnknownEntityException {
    public InvalidPathException(String errorMessage) {
        super(errorMessage);
    }
}
