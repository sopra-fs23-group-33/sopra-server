package ch.uzh.ifi.hase.soprafs23.constant;


public enum InstructionType {

    A0(0.0, Symbol.ADD),
    A1(1.0, Symbol.MUL),
    A2(1.0, Symbol.MUL),
    A3(0.0, Symbol.ADD),
    A4(0.0, Symbol.ADD),
    A5(0.0, Symbol.ADD),
    A6(1.0, Symbol.MUL),
    A7(0.0, Symbol.ADD),
    A8(1.0, Symbol.MUL),
    A9(0.0, Symbol.ADD),
    A10(1.0, Symbol.MUL),
    A11(0.0, Symbol.ADD),
    A12(1.0, Symbol.MUL),
    A13(0.0, Symbol.ADD),
    A14(0.0, Symbol.ADD),
    A15(0.0, Symbol.ADD),

    A16(0.0, Symbol.ADD),
    A17(0.0, Symbol.ADD),
    A18(0.0, Symbol.ADD),
    A19(0.0, Symbol.ADD);



    private enum Symbol {
        ADD, MUL;
    }

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
