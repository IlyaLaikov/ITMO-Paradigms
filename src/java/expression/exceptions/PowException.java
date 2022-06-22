package expression.exceptions;

public class PowException extends BinaryOperatorException {
    public PowException(String message, int x, int y) {
        super(message, x, y, "**");
    }
}
