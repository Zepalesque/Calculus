package net.zepalesque.calc.function;

public class Logarithms {
    
    public static Func ln(Func f) {
        if (f instanceof Const c) return c.ln();
        else if (f.isZero()) return Constants.NEG_INF;
        else return new NaturalLog(f);
    }
    
    interface Logarithmic extends Func {
        Func base();
        Func f();
    }
    
    record NaturalLog(Func f) implements Logarithmic {
        
        @Override
        public Func base() {
            return Constants.E;
        }
        
        @Override
        public double eval(double x) {
            return Math.log(f().eval(x));
        }
        
        @Override
        public Func derivative() {
            return Division.divide(f().derivative(), f());
        }
        
        @Override
        public String toString() {
            return String.format("ln(%s)", f());
        }
    }
}
