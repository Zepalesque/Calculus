package net.zepalesque.calc.function;

import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class Logarithms {
    
    public static Func ln(Func f) {
        if (f instanceof Const c) return c.ln();
        else return new NaturalLog(f);
    }
    
    interface Logarithmic extends Func {
        Func base();
        Func f();
    }
    
    record NaturalLog(Func f) implements Logarithmic, SimpleIntegratableFunction {
        
        @Override
        public Func base() {
            return Constants.E;
        }
        
        @Override
        public Const eval(Const x) {
            return f.eval(x).ln();
        }
        
        @Override
        public Variables.Variable termVariable() {
            return f.termVariable();
        }
        
        @Override
        public Func derivative() {
            return Division.divide(f().derivative(), f());
        }
        
        @Override
        public String toString() {
            return String.format("ln(%s)", f());
        }
        
        @Override
        public Func inner() {
            return f();
        }
        
        @Nullable
        @Override
        public Func integrateImpl() {
            return Addition.add(Multiplication.multiply(f(), this), f().negate());
        }
        
        @Override
        public Func createWithSubstitution(Func var) {
            return ln(var);
        }
        
        
    }
}
