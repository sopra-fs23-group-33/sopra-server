package ch.uzh.ifi.hase.soprafs23.constant;


public enum InstructionType {

    a0(0.0, Symbol.ADD),
    a1(1.0, Symbol.MUL),
    a2(1.0, Symbol.MUL),
    a3(0.0, Symbol.ADD);

    private enum Symbol {
        ADD, MUL;
    }

    ;
    private final double defaultValue;

    private final Symbol symbol;

    InstructionType(Double defaultValue, Symbol symbol) {
        this.defaultValue = defaultValue;
        this.symbol = symbol;
    }

    public double getDefaultValue() {
        return defaultValue;
    }

    public double compute(double a, double b) {
        if (this.symbol == Symbol.ADD)
            return a + b;
        else if (this.symbol == Symbol.MUL)
            return a * b;
        else
            return a;
    }
}
