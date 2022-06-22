package expression;

import expression.mode.Mode;

public class Subtract<N extends Number> extends AbstractBinaryOperator<N> {
    private static final String OPERATOR = "-";
    private static final int PRIORITY = 2;
    private static final boolean IS_SYMMETRICAL = false;

    public Subtract(Mode<N> mode, GenericExpression<N> first, GenericExpression<N> second) {
        super(mode, first, second);
    }

    @Override
    protected N apply(N first, N second) {
        return mode.subtract(first, second);
    }

    @Override
    protected String getOperator() {
        return OPERATOR;
    }

    @Override
    protected int getPriority() {
        return PRIORITY;
    }

    @Override
    protected boolean isSymmetrical() {
        return IS_SYMMETRICAL;
    }
}
