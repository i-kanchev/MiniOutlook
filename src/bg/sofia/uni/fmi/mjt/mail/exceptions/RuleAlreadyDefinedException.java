package bg.sofia.uni.fmi.mjt.mail.exceptions;

public class RuleAlreadyDefinedException extends RuntimeException {
    public RuleAlreadyDefinedException(String errorMessage) {
        super(errorMessage);
    }
}
