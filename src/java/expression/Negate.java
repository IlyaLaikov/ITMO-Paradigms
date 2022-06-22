package expression;

import expression.mode.Mode;

public class Negate<N extends Number> extends AbstractUnaryOperator<N> {
    private static final String OPERATOR = "-";

    public Negate(Mode<N> mode, GenericExpression<N> child) {
        super(mode, child);
    }

    @Override
    protected N apply(N child) {
        return mode.negate(child);
    }

    @Override
    protected String getOperator() {
        return OPERATOR;
    }
}
