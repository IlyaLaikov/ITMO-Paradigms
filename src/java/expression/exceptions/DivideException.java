package expression.exceptions;

public class DivideException extends BinaryOperatorException {
    public DivideException(String message, int x, int y) {
        super(message, x, y, "/");
    }

    public DivideException(String message, Throwable exc) {
        super(message, exc);
    }
}
