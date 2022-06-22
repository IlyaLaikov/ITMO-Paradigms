package expression.mode;

public interface Mode<N extends Number> {
    N negate(N a);

    N count(N a);

    N add(N a, N b);

    N subtract(N a, N b);

    N multiply(N a, N b);

    N divide(N a, N b);

    N min(N a, N b);

    N max(N a, N b);

    N valueOf(int value);

    N valueOf(String s);
}
