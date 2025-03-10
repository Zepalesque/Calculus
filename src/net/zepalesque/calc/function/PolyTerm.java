package net.zepalesque.calc.function;

public record PolyTerm(Const coefficient, Const power) implements Term<PolyTerm> {
    
    public static final PolyTerm X = new PolyTerm(Constants.ONE, Constants.ONE);
    
    @Override
    public boolean isZero() {
        return coefficient.isZero();
    }
    
    public double eval(double x) {
        return Math.pow(x, power.value()) * coefficient.value();
    }
    
    public Func derivative() {
        if (power.equals(Constants.ONE)) return coefficient;
        return new PolyTerm(power.multiply(coefficient), power.add(Constants.ONE.negate()));
    }
    
    public String toString() {
        if (power.equals(Constants.ZERO) || coefficient.equals(Constants.ZERO))
            return String.format("%s", coefficient);
        else if (power.equals(Constants.ONE)) {
            if (coefficient().equals(Constants.ONE)) return "x";
            return String.format("%sx", coefficient);
        } return String.format("%sx^%s", coefficient, power);
    }
    
    @Override
    public PolyTerm create(Const coefficient, Const power) {
        return new PolyTerm(coefficient, power);
    }
}
