package expression.mode;

import expression.exceptions.*;

public class CheckedIntegerMode implements Mode<Integer> {
    @Override
    public Integer negate(Integer a) {
        if (a == Integer.MIN_VALUE) {
            throw new NegateException("overflow", a);
        }
        return -a;
    }

    @Override
    public Integer count(Integer a) {
        return Integer.bitCount(a);
    }

    @Override
    public Integer add(Integer a, Integer b) {
        if (b > 0 && Integer.MAX_VALUE - b < a || b < 0 && Integer.MIN_VALUE - b > a) {
            throw new AddException("overflow", a, b);
        }
        return a + b;
    }

    @Override
    public Integer subtract(Integer a, Integer b) {
        if (b > 0 && Integer.MIN_VALUE + b > a || b < 0 && Integer.MAX_VALUE + b < a) {
            throw new SubtractException("overflow", a, b);
        }
        return a - b;
    }

    @Override
    public Integer multiply(Integer a, Integer b) {
        if (a != 0 && b != 0 && ((a * b) / a != b || (a * b) / b != a)) {
            throw new MultiplyException("overflow", a, b);
        }
        return a * b;
    }

    @Override
    public Integer divide(Integer a, Integer b) {
        if (b == 0) {
            throw new DivideException("divide by zero", a, b);
        } else if (a == Integer.MIN_VALUE && b == -1) {
            throw new DivideException("overflow", a, b);
        }
        return a / b;
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
