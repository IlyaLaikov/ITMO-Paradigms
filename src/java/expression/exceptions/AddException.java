package expression.exceptions;

public class AddException extends BinaryOperatorException {
    public AddException(String message, int x, int y) {
        super(message, x, y, "+");
    }
}
