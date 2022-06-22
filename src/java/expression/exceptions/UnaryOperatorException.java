package expression.exceptions;

public class UnaryOperatorException extends ExpressionException{
    public UnaryOperatorException(String message, int x) {
        super(message + ": " + x);
    }
}
