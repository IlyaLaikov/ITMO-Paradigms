package expression;

import expression.exceptions.ExpressionException;

public class Variable<N extends Number> implements GenericExpression<N> {
    private final String name;
    private final int hashCode;

    public Variable(String name) {
        this.name = name;
        this.hashCode = name.hashCode();
    }

    @Override
    public N evaluate(final N x, final N y, final N z) {
        switch (name) {
            case "x":
                return x;
            case "y":
                return y;
            case "z":
                return z;
            default:
                throw new ExpressionException("Too low arguments");
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String toMiniString() {
        return name;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
