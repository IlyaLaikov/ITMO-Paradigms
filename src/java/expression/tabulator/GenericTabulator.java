package expression.tabulator;

import expression.GenericExpression;
import expression.exceptions.ExpressionException;
import expression.mode.*;
import expression.parser.ExpressionParser;

import java.util.Map;

public class GenericTabulator implements Tabulator {
    private final static Map<String, Mode<?>> STRING_TO_MODE = Map.ofEntries(
            Map.entry("i", new CheckedIntegerMode()),
            Map.entry("d", new DoubleMode()),
            Map.entry("bi", new BigIntegerMode()),
            Map.entry("u", new IntegerMode()),
            Map.entry("l", new LongMode()),
            Map.entry("f", new FloatMode())
    );

    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
        if (STRING_TO_MODE.containsKey(mode)) {
            return tabulate(STRING_TO_MODE.get(mode), expression, x1, x2, y1, y2, z1, z2);
        } else {
            throw new IllegalArgumentException("Unsupported mode: " + mode);
        }
    }

    public <N extends Number> Object[][][] tabulate(Mode<N> mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
        ExpressionParser<N> parser = new ExpressionParser<>(mode);
        GenericExpression<N> genericExpression = parser.parse(expression);
        Object[][][] ans = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];
        for (int x = 0; x <= x2 - x1; x++) {
            for (int y = 0; y <= y2 - y1; y++) {
                for (int z = 0; z <= z2 - z1; z++) {
                    try {
                        ans[x][y][z] = genericExpression.evaluate(mode.valueOf(x + x1), mode.valueOf(y + y1), mode.valueOf(z + z1));
                    } catch (ExpressionException ignored) {}
                }
            }
        }
        return ans;
    }
}
