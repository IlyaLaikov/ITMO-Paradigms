package expression;

import expression.mode.Mode;

public class Add<N extends Number> extends AbstractBinaryOperator<N> {
    private static final String OPERATOR = "+";
    private static final int PRIORITY = 2;
    private static final boolean IS_SYMMETRICAL = true;

    public Add(Mode<N> mode, GenericExpression<N> first, GenericExpression<N> second) {
        super(mode, first, second);
    }

    @Override
    protected N apply(N first, N second) {
        return mode.add(first, second);
    }

    @Override
    protected boolean checkSecondByAsymmetrical(GenericExpression<N> expr) {
        if (expr instanceof AbstractBinaryOperator) {
            AbstractBinaryOperator<N> exprOp = (AbstractBinaryOperator<N>) expr;
            if (!exprOp.isSymmetrical() && exprOp.getPriority() > getPriority()) {
                return true;
            } else if (exprOp instanceof Min || exprOp instanceof Max) {
                return true;
            }
            return (!isSymmetrical()) && (exprOp.getPriority() >= getPriority());
        }
        return false;
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
