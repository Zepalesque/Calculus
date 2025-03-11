package net.zepalesque.calc.function;

public class Polynomials {
    
    public static Func term(Const coefficient, Const power) {
        if (coefficient.equals(Constants.ZERO)) {
            return Constants.ZERO;
        } else if (power.equals(Constants.ZERO)) {
            return coefficient;
        } else return new PTerm(coefficient, power);
    }
    
    public static final Func X = new PTerm(Constants.ONE, Constants.ONE);
    
    public record PTerm(Const coefficient, Const power) implements Term {
        
        public Const eval(Const x) {
            return x.pow(power).multiply(coefficient);
        }
        
        @Override
        public Variables.Variable termVariable() {
            return Variables.X;
        }
        
        public Func derivative() {
            if (power.equals(Constants.ZERO)) return Constants.ZERO;
            else if (power.equals(Constants.ONE)) return coefficient();
            return new PTerm(power.multiply(coefficient), power.add(Constants.ONE.negate()));
        }
        
        public String toString() {
            if (power.equals(Constants.ZERO) || coefficient.equals(Constants.ZERO)) {
                return String.format("%s", coefficient);
            } else if (power.equals(Constants.ONE)) {
                if (coefficient().equals(Constants.ONE)) return "x";
                return String.format("%sx", coefficient);
            } return String.format("%sx^%s", coefficient, power);
        }
        
        @Override
        public Func create(Const coefficient, Const power) {
            return Polynomials.term(coefficient, power);
        }
    }
}
