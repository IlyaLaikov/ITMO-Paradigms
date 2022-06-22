package expression.parser;

import expression.*;
import expression.exceptions.ParserException;
import expression.mode.Mode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExpressionParser <N extends Number> implements GenericParser<N> {
    final Mode<N> mode;

    public ExpressionParser(final Mode<N> mode) {
        this.mode = mode;
    }

    @Override
    public GenericExpression<N> parse(final String source) {
        try {
            return parse(new StringSource(source));
        } catch (IllegalArgumentException exc) {
            throw new ParserException(exc.getMessage(), exc);
        }
    }

    public GenericExpression<N> parse(final CharSource source) {
        return new InnerExpressionParser<N>(mode, source).parseExpression();
    }

    private static class InnerExpressionParser <N extends Number> extends BaseParser {
        private int scopeBalance = 0;
        final Mode<N> mode;

        public InnerExpressionParser(final Mode<N> mode, final CharSource source) {
            super(source);
            this.mode = mode;
        }

        public GenericExpression<N> parseExpression() {
            GenericExpression<N> result = parseSubexpression();
            if (eof() && scopeBalance == 0) {
                return result;
            }
            throw error("End of Expression expected");
        }

        class OperatorBank {
            private final List<GenericExpression<N>> exprList = new ArrayList<>(COUNT_OF_PRIORITY_LEVELS + 1);
            private final List<String> operatorList = new ArrayList<>(COUNT_OF_PRIORITY_LEVELS);
            private int size = 0;

            public OperatorBank(GenericExpression<N> firstExpr) {
                exprList.add(firstExpr);
                for (int i = 0; i < COUNT_OF_PRIORITY_LEVELS; ++i) {
                    exprList.add(null);
                    operatorList.add(null);
                }
            }

            public void add(String op, GenericExpression<N> expr) {
                while (size > 0 && PRIORITY.get(op) >= PRIORITY.get(operatorList.get(size - 1))) {
                    mergeLast();
                }
                operatorList.set(size, op);
                exprList.set(size + 1, expr);
                ++size;
            }

            public GenericExpression<N> getExpr() {
                while (size > 0) {
                    mergeLast();
                }
                return exprList.get(0);
            }

            private void mergeLast() {
                --size;
                exprList.set(size, makeExpression(operatorList.get(size), exprList.get(size), exprList.get(size + 1)));
            }

            private GenericExpression<N> makeExpression(String operator, GenericExpression<N> a, GenericExpression<N> b) {
                switch (operator) {
                    case "+":
                        return new Add<>(mode, a, b);
                    case "-":
                        return new Subtract<>(mode, a, b);
                    case "*":
                        return new Multiply<>(mode, a, b);
                    case "/":
                        return new Divide<>(mode, a, b);
                    case "min":
                        return new Min<>(mode, a, b);
                    case "max":
                        return new Max<>(mode, a, b);
                    default:
                        throw error("Unsupported operator: " + operator);
                }
            }
        }

        boolean afterScope = false;

        private GenericExpression<N> parseSubexpression () {
            OperatorBank bank = new OperatorBank(parseAtom());

            while (!eof()) {
                String operator = takeOperator();
                if (operator.equals(")")) {
                    afterScope = true;
                    if (scopeBalance > 0) {
                        --scopeBalance;
                    } else {
                        throw error("Wrong scope sequence");
                    }
                    return bank.getExpr();
                } else {
                    afterScope = false;
                    bank.add(operator, parseAtom());
                }
            }

            return bank.getExpr();
        }

        private GenericExpression<N> parseAtom () {
            skipWhitespaces();
            if (take('(')) {
                ++scopeBalance;
                return parseSubexpression();
            } else if (take('-')) {
                if (between('1', '9')) {
                    return parseNumber(true);
                } else {
                    return new Negate<>(
                            mode,
                            parseAtom()
                    );
                }
            } else if (take('c')) {
                expect("ount");
                return new Count<>(
                        mode,
                        parseAtom()
                );
            } else if (between('0', '9')) {
                return parseNumber(false);
            } else if (between('x', 'z')) { // or "if (take('x') || take('y') || take('z'))"
                return new Variable<>(
                        // parse variable
                        String.valueOf(take())
                );
            }
            throw error("Number or unary operator expected");
        }

        private static final int COUNT_OF_PRIORITY_LEVELS = 3;
        private static final Map<String, Integer> PRIORITY = Map.ofEntries(
                Map.entry("min", 3),
                Map.entry("max", 3),
                Map.entry("+", 2),
                Map.entry("-", 2),
                Map.entry("*", 1),
                Map.entry("/", 1)
        );

        private String takeOperator () {
            boolean ws = skipWhitespaces();
            if (test('+') || test('-') || test('*') || test('/')) {
                return String.valueOf(take());
            } else if (take(')')) {
                return ")";
            } else if (take('m')) {
                if (afterScope || ws) {
                    if (take('i')) {
                        expect("n");
                        return "min";
                    } else if (take('a')) {
                        expect("x");
                        return "max";
                    } else {
                        throw error("'i' or 'a' expected");
                    }
                } else {
                    throw error("Missing whitespace before min or max");
                }
            } else {
                throw error("Not found binary operator: " + take());
            }
        }

        private GenericExpression<N> parseNumber ( boolean minus){
            StringBuilder sb = new StringBuilder();
            if (minus) {
                sb.append('-');
            }

            takeInteger(sb);

            return new Const<>(mode.valueOf(sb.toString()));
        }


        private void takeInteger(final StringBuilder sb) {
            if (take('0')) {
                sb.append('0');
            } else if (between('1', '9')) {
                takeDigits(sb);
            } else {
                throw error("Invalid number");
            }
        }

        private void takeDigits(final StringBuilder sb) {
            while (between('0', '9')) {
                sb.append(take());
            }
        }
    }
}
