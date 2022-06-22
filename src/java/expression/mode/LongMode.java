package expression.mode;

import expression.exceptions.DivideException;

public class LongMode implements Mode<Long> {
    @Override
    public Long negate(Long a) {
        return -a;
    }

    @Override
    public Long count(Long a) {
        return (long) Long.bitCount(a);
    }

    @Override
    public Long add(Long a, Long b) {
        return a + b;
    }

    @Override
    public Long subtract(Long a, Long b) {
        return a - b;
    }

    @Override
    public Long multiply(Long a, Long b) {
        return a * b;
    }

    @Override
    public Long divide(Long a, Long b) {
        try {
            return a / b;
        } catch (ArithmeticException exc) {
            throw new DivideException("divide by zero", exc);
        }
    }

    @Override
    public Long min(Long a, Long b) {
        return Long.min(a, b);
    }

    @Override
    public Long max(Long a, Long b) {
        return Long.max(a, b);
    }

    @Override
    public Long valueOf(int value) {
        return (long) value;
    }

    @Override
    public Long valueOf(String s) {
        return Long.valueOf(s);
    }
}
