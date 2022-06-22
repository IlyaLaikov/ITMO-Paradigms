package expression.exceptions;

public class LogException extends BinaryOperatorException {
    public LogException(String message, int x, int y) {
        super(message, x, y, "//");
    }
}
