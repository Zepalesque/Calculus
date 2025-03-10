package net.zepalesque.calc.function;

public interface Func {
    
    double eval(double x);
    
    Variables.Variable termVariable();
    
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
        public Variables.Variable termVariable() {
            return this.func().termVariable();
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
