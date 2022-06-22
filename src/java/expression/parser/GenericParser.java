package expression.parser;

import expression.GenericExpression;
import expression.exceptions.ParserException;

@FunctionalInterface
public interface GenericParser <N extends Number> {
    GenericExpression<N> parse(String expression) throws ParserException;
}
