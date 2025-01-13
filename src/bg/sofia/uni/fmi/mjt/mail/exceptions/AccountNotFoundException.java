package bg.sofia.uni.fmi.mjt.mail.exceptions;

import javax.lang.model.UnknownEntityException;

public class AccountNotFoundException extends UnknownEntityException {
    public AccountNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
