package expression;

import java.util.Objects;

import expression.mode.Mode;

public abstract class AbstractBinaryOperator<N extends Number> extends AbstractOperator<N> {
    protected final GenericExpression<N> first;
    protected final GenericExpression<N> second;

    private final int hashCode;
    private String toString;
    private String toMiniString;

    public AbstractBinaryOperator(final Mode<N> mode, final GenericExpression<N> first,
            final GenericExpression<N> second) {
        super(mode);
        this.first = Objects.requireNonNull(first);
        this.second = Objects.requireNonNull(second);
        this.hashCode = Objects.hash(mode, first, second, getClass());
    }

    protected abstract N apply(N first, N second);

    protected abstract String getOperator();

    protected abstract int getPriority();

    protected abstract boolean isSymmetrical();

    @Override
    public N evaluate(final N x, final N y, final N z) {
        return apply(
                first.evaluate(x, y, z),
                second.evaluate(x, y, z));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractBinaryOperator) {
            AbstractBinaryOperator<?> op = (AbstractBinaryOperator<?>) obj;
            return mode.equals(op.mode)
                    && first.equals(op.first)
                    && second.equals(op.second)
                    && op.getClass().equals(getClass());
        }
        return false;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = "(" + first + " " + getOperator() + " " + second + ")";
        }
        return toString;
    }

    @Override
    public String toMiniString() {
        if (toMiniString == null) {
            toMiniString = operandWrapper(first, checkBracketsByPriority(first))
                    + " " + getOperator() + " "
                    + operandWrapper(second, checkSecondByAsymmetrical(second));
        }
        return toMiniString;
    }

    protected boolean checkBracketsByPriority(GenericExpression<N> expr) {
        if (expr instanceof AbstractBinaryOperator) {
            AbstractBinaryOperator<N> exprOp = (AbstractBinaryOperator<N>) expr;
            return exprOp.getPriority() > getPriority();
        }
        return false;
    }

    protected boolean checkBracketsByPriorityOther(GenericExpression<N> expr) {
        return checkBracketsByPriority(expr);
    }

    protected boolean checkSecondByAsymmetrical(GenericExpression<N> expr) {
        if (expr instanceof AbstractBinaryOperator) {
            AbstractBinaryOperator<N> exprOp = (AbstractBinaryOperator<N>) expr;
            if (!exprOp.isSymmetrical() && exprOp.getPriority() >= getPriority()) {
                return true;
            } else if (!isSymmetrical() && exprOp.getPriority() >= getPriority()) {
                return true;
            }
        }
        return checkBracketsByPriorityOther(expr);
    }

    private String operandWrapper(GenericExpression<N> expr, boolean inBrackets) {
        return (inBrackets ? "(" : "") + expr.toMiniString() + (inBrackets ? ")" : "");
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
