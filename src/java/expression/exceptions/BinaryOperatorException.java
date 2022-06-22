package expression.exceptions;

public class BinaryOperatorException extends ExpressionException {
    public BinaryOperatorException(String message, int x, int y, String operator) {
        super(message + ": " + x + " " + operator + " " + y);
    }

    public BinaryOperatorException(String message, Throwable exc) {
        super(message, exc);
    }
}
