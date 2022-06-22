package expression.exceptions;

public class ParserException extends RuntimeException {
    public ParserException(String message) {
        super(message);
    }
    public ParserException(String message, Throwable exc) {
        super(message, exc);
    }
}
