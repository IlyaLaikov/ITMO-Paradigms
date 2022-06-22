package expression.exceptions;

public class SubtractException extends BinaryOperatorException {
    public SubtractException(String message, int x, int y) {
        super(message, x, y, "-");
    }
}
