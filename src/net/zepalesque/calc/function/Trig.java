package net.zepalesque.calc.function;

import org.jetbrains.annotations.Nullable;

public class Trig {
    
    interface TrigFunc extends SimpleIntegratableFunction {
        Func f();
        
        Func reciporical();
        
        @Override
        default Variables.Variable termVariable() {
            return f().termVariable();
        }
        
        @Override
        default Func inner() {
            return f();
        }
    }
    
    public static Func sine(Func f) {
        if (f instanceof Const c)
            return c.sin();
        else return new Sin(f);
    }
    
    public static Func cosine(Func f) {
        if (f instanceof Const c)
            return c.cos();
        else return new Cos(f);
    }
    
    public static Func tangent(Func f) {
        if (f instanceof Const c)
            return c.tan();
        else return new Tan(f);
    }
    
    public static Func cosecant(Func f) {
        if (f instanceof Const c)
            return c.csc();
        else return new Csc(f);
    }
    
    public static Func secant(Func f) {
        if (f instanceof Const c)
            return c.sec();
        else return new Sec(f);
    }
    
    public static Func cotangent(Func f) {
        if (f instanceof Const c)
            return c.cot();
        else return new Cot(f);
    }
    
    
    record Sin(Func f) implements TrigFunc {
        
        @Override
        public Const eval(Const x) {
            return f.eval(x).sin();
        }
        
        @Override
        public Func derivative() {
            return Multiplication.multiply(cosine(this.f).negate(), this.f.derivative());
        }
        
        
        @Override
        public Func reciporical() {
            return cosecant(f);
        }
        
        @Override
        public String toString() {
            return String.format("sin(%s)", f);
        }
       
        
        @Override
        public Func integrateImpl() {
            return cosine(f).negate();
        }
    }
    
    record Cos(Func f) implements TrigFunc {
        @Override
        public Const eval(Const x) {
            return f.eval(x).cos();
        }
        
        @Override
        public Func derivative() {
            return Multiplication.multiply(sine(this.f).negate(), this.f.derivative());
        }
        
        @Override
        public Func reciporical() {
            return secant(f);
        }
        
        @Override
        public String toString() {
            return String.format("cos(%s)", f);
        }
       
        
        @Override
        public Func integrateImpl() {
            return sine(f);
        }
    }
    
    record Tan(Func f) implements TrigFunc {
        @Override
        public Const eval(Const x) {
            return f.eval(x).tan();
        }
        
        @Override
        public Func derivative() {
            // sec^2(exp)*exp'
            Func secSquared = Powers.Power.power(secant(f), Constants.TWO);
            return Multiplication.multiply(secSquared, f.derivative());
        }
        
        @Override
        public Func reciporical() {
            return cotangent(f);
        }
        
        @Override
        public String toString() {
            return String.format("tan(%s)", f);
        }
       
        
        @Override
        public Func integrateImpl() {
            return Logarithms.ln(cosine(f)).negate();
        }
    }
    
    record Csc(Func f) implements TrigFunc {
        
        @Override
        public Const eval(Const x) {
            return f.eval(x).csc();
        }
        
        @Override
        public Func derivative() {
            // -cot(exp)csc(exp)*exp'
            Func negativeCotCsc = Multiplication.multiply(cotangent(f).negate(), cosecant(f));
            return Multiplication.multiply(negativeCotCsc, f.derivative());
        }
        
        
        @Override
        public Func reciporical() {
            return sine(f);
        }
        
        @Override
        public String toString() {
            return String.format("csc(%s)", f);
        }
       
        
        @Nullable
        @Override
        public Func integrateImpl() {
            return Logarithms.ln(Addition.add(cosecant(f), cotangent(f))).negate();
        }
    }
    
    record Sec(Func f) implements TrigFunc {
        
        @Override
        public Const eval(Const x) {
            return f.eval(x).sec();
        }
        
        @Override
        public Func derivative() {
            // sec(exp)tan(exp)*exp'
            Func secTan = Multiplication.multiply(secant(f), tangent(f));
            return Multiplication.multiply(secTan, f.derivative());
        }
        
        
        @Override
        public Func reciporical() {
            return cosine(f);
        }
        
        @Override
        public String toString() {
            return String.format("sec(%s)", f);
        }
        
        @Nullable
        @Override
        public Func integrateImpl() {
            return Logarithms.ln(Addition.add(secant(f), tangent(f)));
        }
    }
    
    record Cot(Func f) implements TrigFunc {
        
        @Override
        public Const eval(Const x) {
            return f.eval(x).cot();
        }
        
        @Override
        public Func derivative() {
            // -csc^2(exp)*exp'
            Func negativeCscSquared = Powers.Power.power(cosecant(f), Constants.TWO).negate();
            return Multiplication.multiply(negativeCscSquared, f.derivative());
        }
        
        @Override
        public Func reciporical() {
            return tangent(f);
        }
        
        @Override
        public String toString() {
            return String.format("cot(%s)", f);
        }
        
        @Nullable
        @Override
        public Func integrateImpl() {
            return Logarithms.ln(sine(f));
        }
    }
}
