package net.zepalesque.calc.function;

import java.util.function.Predicate;

public class Variables {
    
    private static final String charsForVars = "xutjklab";
    
    public static char getNextForDifferential(char current) {
        int i = charsForVars.indexOf(current);
        if (i != -1 && i < charsForVars.length() - 1) {
            return charsForVars.charAt(i + 1);
        } else {
            return (char) (current + 1);
        }
    }
    
    public static Variable of(Func func, char id) {
        if (func.equals(X)) return X;
        else return VarHolder.create(id, func);
    }
    
    public static final Variable X = new XVar();
    
    public interface Variable extends Func {
        char identifier();
        
        String differential();
        
        Func function();
        
        String expand();
        String expandDifferential();
        
        @Override
        default Variable termVariable() {
            return this;
        }
        
        @Override
        default Func substituteImpl(Func var, Predicate<Func> predicate) {
            return predicate.test(this) ? var : null;
        }
        
        @Override
        default Func derivative() {
            return Constants.ONE;
        }
    }
    
    public record VarHolder(char identifier, Func function, Func deriv) implements Variable {
        public static VarHolder create(char id, Func val) {
            return new VarHolder(id, val, val.derivative());
        }
        
        @Override
        public String toString() {
            return String.valueOf(identifier);
        }
        
        public String differential() {
            return String.format("d%c", identifier);
        }
        
        @Override
        public String expand() {
            return "";
        }
        
        @Override
        public String expandDifferential() {
            return "";
        }
        
        @Override
        public Const eval(Const x) {
            return this.function().eval(x);
        }
    }
    
    record XVar() implements Variable {
        
        @Override
        public char identifier() {
            return 'x';
        }
        
        @Override
        public String differential() {
            return "dx";
        }
        
        @Override
        public Func function() {
            return this;
        }
        
        @Override
        public String expand() {
            return "x";
        }
        
        @Override
        public String expandDifferential() {
            return "dx";
        }
        
        @Override
        public Const eval(Const x) {
            return x;
        }
        
        @Override
        public String toString() {
            return "x";
        }
    }
}
