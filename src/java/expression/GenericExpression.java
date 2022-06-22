package expression;

public interface GenericExpression<N extends Number> extends ToMiniString {
    N evaluate(final N x, final N y, final N z);
}
