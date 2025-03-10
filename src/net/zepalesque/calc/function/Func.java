package net.zepalesque.calc.function;

public interface Func {
    
    default boolean isZero() {
        return false;
    }
    
    double eval(double x);
    
    Func derivative();
    
    default Func negate() {
        return new Negation(this);
    }
    
    record Negation(Func func) implements Func {
        @Override
        public double eval(double x) {
            return -func.eval(x);
        }
        
        @Override
        public Func derivative() {
            return func.derivative().negate();
        }
        
        @Override
        public Func negate() {
            return func;
        }
        
        @Override
        public String toString() {
            return String.format("-%s", func);
        }
    }
    
}
