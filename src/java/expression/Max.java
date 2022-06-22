package expression;

import expression.mode.Mode;

public class Max<N extends Number> extends AbstractBinaryOperator<N> {
    private static final String OPERATOR = "max";
    private static final int PRIORITY = 3;
    private static final boolean IS_SYMMETRICAL = true;

    public Max(Mode<N> mode, GenericExpression<N> first, GenericExpression<N> second) {
        super(mode, first, second);
    }

    @Override
    protected N apply(N first, N second) {
        return mode.max(first, second);
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

    @Override
    protected boolean checkBracketsByPriorityOther(GenericExpression<N> expr) {
        if (expr instanceof AbstractBinaryOperator) {
            AbstractBinaryOperator<N> exprOp = (AbstractBinaryOperator<N>) expr;
            return exprOp instanceof Min || exprOp.getPriority() > getPriority();
        }
        return false;
    }
}
