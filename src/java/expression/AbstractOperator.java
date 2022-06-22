package expression;

import java.util.Objects;

import expression.mode.Mode;

public abstract class AbstractOperator<N extends Number> implements GenericExpression<N> {
    protected final Mode<N> mode;

    public AbstractOperator(Mode<N> mode) {
        this.mode = Objects.requireNonNull(mode);
    }
}
