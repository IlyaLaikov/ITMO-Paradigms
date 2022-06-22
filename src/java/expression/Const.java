package expression;

public class Const<N extends Number> implements GenericExpression<N> {
    private final N value;
    private final String toString;

    public Const(N value) {
        this.value = value;
        this.toString = value.toString();
    }

    @Override
    public N evaluate(final N x, final N y, final N z) {
        return value;
    }

    @Override
    public String toString() {
        return toString;
    }

    @Override
    public String toMiniString() {
        return toString;
    }
}
