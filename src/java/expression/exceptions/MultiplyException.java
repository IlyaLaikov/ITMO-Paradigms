package expression.exceptions;

public class MultiplyException extends BinaryOperatorException {
    public MultiplyException(String message, int x, int y) {
        super(message, x, y, "+");
    }
}
