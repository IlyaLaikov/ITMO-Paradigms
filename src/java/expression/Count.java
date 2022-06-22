package expression;

import expression.mode.Mode;

public class Count<N extends Number> extends AbstractUnaryOperator<N> {
    private static final String OPERATOR = "count";

    public Count(Mode<N> mode, GenericExpression<N> child) {
        super(mode, child);
    }

    @Override
    protected N apply(N child) {
        return mode.count(child);
    }

    @Override
    protected String getOperator() {
        return OPERATOR;
    }
}
