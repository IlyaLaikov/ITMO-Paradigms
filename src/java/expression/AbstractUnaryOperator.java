package expression;

import java.util.Objects;

import expression.mode.Mode;

public abstract class AbstractUnaryOperator<N extends Number> extends AbstractOperator<N> {
    protected final GenericExpression<N> child;

    private final int hashCode;
    private String toString;
    private String toMiniString;

    protected AbstractUnaryOperator(Mode<N> mode, GenericExpression<N> child) {
        super(mode);
        this.child = Objects.requireNonNull(child);
        this.hashCode = Objects.hash(mode, child, getClass());
    }

    protected abstract N apply(N child);

    protected abstract String getOperator();

    @Override
    public N evaluate(final N x, final N y, final N z) {
        return apply(
                child.evaluate(x, y, z));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractUnaryOperator) {
            AbstractUnaryOperator<?> op = (AbstractUnaryOperator<?>) obj;
            return mode.equals(op.mode)
                    && child.equals(op.child)
                    && op.getClass().equals(getClass());
        }
        return false;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = getOperator() + "(" + child + ")";
        }
        return toString;
    }

    @Override
    public String toMiniString() {
        if (toMiniString == null) {
            toMiniString = getOperator() + operandWrapper(child, child instanceof AbstractBinaryOperator);
        }
        return toMiniString;
    }

    private String operandWrapper(GenericExpression<N> expr, boolean inBrackets) {
        return (inBrackets ? "(" : " ") + expr.toMiniString() + (inBrackets ? ")" : "");
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
