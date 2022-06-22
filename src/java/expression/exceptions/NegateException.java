package expression.exceptions;

public class NegateException extends UnaryOperatorException {
    public NegateException(String message, int x) {
        super(message, x);
    }
}
