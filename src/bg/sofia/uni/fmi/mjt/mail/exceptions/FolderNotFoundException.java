package bg.sofia.uni.fmi.mjt.mail.exceptions;

import javax.lang.model.UnknownEntityException;

public class FolderNotFoundException extends UnknownEntityException {
    public FolderNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
