package net.zepalesque.calc.function;

public class Powers {
    
    public static Func pow(Func base, Func exponent) {
        if (base instanceof Const c1 && exponent instanceof Const c2)
            return c1.pow(c2);
        else if (base instanceof PowerFunc pow)
            return pow(pow.f(), Multiplication.multiply(pow.g(), exponent));
        else if (exponent instanceof Const c)
            if (c.equals(Constants.ZERO)) return base.isZero() ? Constants.NAN : Constants.ONE;
            else if (c.equals(Constants.TWO))
                return new Square(base);
            else if (c.equals(Constants.ONE_HALF))
                return new Sqrt(base);
            else return new Power(base, c);
        else if (exponent.isZero()) {
            if (base.isZero()) return Constants.NAN;
            return Constants.ONE;
        }
        else if (base instanceof Const c) {
            if (c.equals(Constants.ZERO)) return exponent.isZero() ? Constants.NAN : Constants.ZERO;
            if (base.equals(Constants.E))
                return new EBaseExponent(exponent);
            else return new Exponential(c, exponent);
        }
        else return new FPowG(base, exponent);
    }
    
    interface PowerFunc extends Func {
        Func f();
        Func g();
    }
    
    record Square(Func f) implements PowerFunc {
        
        @Override
        public double eval(double x) {
            return f.eval(x) * f.eval(x);
        }
        
        @Override
        public Func derivative() {
            return Multiplication.multiply(Constants.TWO, f, f.derivative());
        }
        
        @Override
        public Func g() {
            return Constants.TWO;
        }
        
        @Override
        public String toString() {
            return String.format("(%s ^ 2)", f);
        }
    }
    
    record Sqrt(Func f) implements PowerFunc {
        @Override
        public double eval(double x) {
            return Math.sqrt(f.eval(x));
        }
        
        @Override
        public Func derivative() {
            return Division.divide(f.derivative(), Multiplication.multiply(Constants.TWO, f));
        }
        
        @Override
        public Func g() {
            return Constants.ONE.divideBy(Constants.TWO);
        }
        
        @Override
        public String toString() {
            return String.format("(√%s)", f);
        }
    }
    
    
    record Power(Func f, Const g) implements PowerFunc {
        @Override
        public double eval(double x) {
            return Math.pow(f.eval(x), g.value());
        }
        
        @Override
        public Func derivative() {
            return Multiplication.multiply(g, power(f, g.add(Constants.NEG_ONE)));
        }
        
        static Func power(Func a, Const b) {
            if (b.equals(Constants.ONE)) return a;
            else if (b.equals(Constants.NEG_ONE)) return Division.divide(Constants.ONE, a);
            else return new Power(a, b);
        }
        
        @Override
        public String toString() {
            return String.format("(%s ^ %s)", f, g);
        }
    }
    
    record EBaseExponent(Func g) implements PowerFunc {
        @Override
        public double eval(double x) {
            return Math.exp(g.eval(x));
        }
        
        @Override
        public Func derivative() {
            return Multiplication.multiply(this, g.derivative());
        }
        
        @Override
        public Func f() {
            return Constants.E;
        }
    }
    
    record Exponential(Const f, Func g) implements PowerFunc {
        @Override
        public double eval(double x) {
            return Math.pow(f.value(), g.eval(x));
        }
        
        @Override
        public Func derivative() {
            return Multiplication.multiply(this, g.derivative(), f.ln());
        }
    }
    
    
    record FPowG(Func f, Func g) implements PowerFunc {
        
        @Override
        public double eval(double x) {
            return Math.pow(f.eval(x), g.eval(x));
        }
        
        @Override
        public Func derivative() {
            throw new IllegalStateException("TODO");
        }
        
        @Override
        public String toString() {
            return String.format("(%s ^ %s)", f, g);
        }
    }
}
