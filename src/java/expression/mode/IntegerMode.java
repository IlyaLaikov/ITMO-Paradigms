package expression.mode;

import expression.exceptions.DivideException;

public class IntegerMode implements Mode<Integer> {
    @Override
    public Integer negate(Integer a) {
        return -a;
    }

    @Override
    public Integer count(Integer a) {
        return Integer.bitCount(a);
    }

    @Override
    public Integer add(Integer a, Integer b) {
        return a + b;
    }

    @Override
    public Integer subtract(Integer a, Integer b) {
        return a - b;
    }

    @Override
    public Integer multiply(Integer a, Integer b) {
        return a * b;
    }

    @Override
    public Integer divide(Integer a, Integer b) {
        try {
            return a / b;
        } catch (ArithmeticException exc) {
            throw new DivideException("divide by zero", exc);
        }
    }

    @Override
    public Integer min(Integer a, Integer b) {
        return Integer.min(a, b);
    }

    @Override
    public Integer max(Integer a, Integer b) {
        return Integer.max(a, b);
    }

    @Override
    public Integer valueOf(int value) {
        return value;
    }

    @Override
    public Integer valueOf(String s) {
        return Integer.valueOf(s);
    }
}
