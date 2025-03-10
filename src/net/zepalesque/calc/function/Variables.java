package net.zepalesque.calc.function;

public class Variables {
    
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
        
    }
    
    public record VarHolder(char identifier, Func function, Func derivative) implements Variable {
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
        public double eval(double x) {
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
            return Polynomials.X;
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
        public double eval(double x) {
            return x;
        }
        
        @Override
        public Func derivative() {
            return Constants.ONE;
        }
    }
}
