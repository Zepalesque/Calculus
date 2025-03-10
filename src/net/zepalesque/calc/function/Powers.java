package net.zepalesque.calc.function;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class Powers {
    
    public static Func pow(Func base, Func exponent) {
        if (base instanceof Const c1 && exponent instanceof Const c2)
            return c1.pow(c2);
        else if (base instanceof Polynomials.PTerm(Const coefficient, Const power)
            && exponent instanceof Const c)
            return Polynomials.term(coefficient.pow(c), power.multiply(c));
        else if (base instanceof PowerFunc pow)
            return pow(pow.f(), Multiplication.multiply(pow.g(), exponent));
        else if (exponent instanceof Const c)
            if (c.equals(Constants.ZERO)) return base.equals(Constants.ZERO) ? Constants.NAN : Constants.ONE;
            else if (c.equals(Constants.ONE)) return base;
            else if (base instanceof Multiplication.Product(Set<Func> factors, List<Func> ignored) && factors.stream().anyMatch(func -> func instanceof Const)) {
                Const multiplier = Constants.ONE;
                List<Func> facsList = new ArrayList<>(factors.stream().toList());
                for (Func fac : facsList)
                    if (fac instanceof Const c1)
                        multiplier = multiplier.multiply(c1);
                multiplier = multiplier.pow(c);
                return Multiplication.multiply(multiplier, pow(Multiplication.multiply(facsList.stream().filter(fac -> !(fac instanceof Const)).toArray(Func[]::new)), exponent));
            }
            else if (c.equals(Constants.TWO))
                return new Square(base);
            else if (c.equals(Constants.ONE_HALF))
                return new Sqrt(base);
            else return new Power(base, c);
        else if (base instanceof Const c) {
            if (c.equals(Constants.ZERO)) return exponent.equals(Constants.ZERO) ? Constants.NAN : Constants.ZERO;
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
    
    interface Pow extends PowerFunc, SimpleIntegratableFunction {
        @Override
        Const g();
        
        @Override
        default Func inner() {
            return f();
        }
        
        default Stream<Func> factor() {
            if (g().value() < 1 && g().value() > 0) return Stream.of(this);
            int full = (int) Math.round(g().value() % 1);
            boolean negative = full < 0;
            Const remainder = g().subtract(Constants.constant(full));
            if (negative) full = -full;
            Func[] facs = new Func[full + 1];
            Func func = !negative ? f() : Division.divide(Constants.ONE, f());
            Arrays.fill(facs, 0, full, func);
            if (!remainder.equals(Constants.ZERO)) facs[full + 1] = remainder;
            return Arrays.stream(facs).filter(Objects::nonNull);
        }
    }
    
    record Square(Func f) implements Pow {
        
        @Override
        public double eval(double x) {
            return f.eval(x) * f.eval(x);
        }
        
        @Override
        public Variables.Variable termVariable() {
            return f.termVariable();
        }
        
        @Override
        public Func derivative() {
            return Multiplication.multiply(Constants.TWO, f, f.derivative());
        }
        
        @Override
        public Const g() {
            return Constants.TWO;
        }
        
        @Override
        public String toString() {
            return String.format("(%s ^ 2)", f);
        }
        
        @Nullable
        @Override
        public Func integrateImpl() {
            return Multiplication.multiply(Constants.ONE_HALF, f());
        }
        
        @Nullable
        @Override
        public Func tryIntegrate(Func differential) {
            if (f instanceof Trig.Sec(Func in)) {
                if (in.derivative().equals(differential)) return Trig.tangent(in);
            }
            return Pow.super.tryIntegrate(differential);
        }
        
        @Override
        public Stream<Func> factor() {
            return Stream.of(f(), f());
        }
    }
    
    record Sqrt(Func f) implements Pow {
        @Override
        public double eval(double x) {
            return Math.sqrt(f.eval(x));
        }
        
        @Override
        public Variables.Variable termVariable() {
            return f.termVariable();
        }
        
        @Override
        public Func derivative() {
            return Division.divide(f.derivative(), Multiplication.multiply(Constants.TWO, f));
        }
        
        @Override
        public Const g() {
            return Constants.ONE.divideBy(Constants.TWO);
        }
        
        @Nullable
        @Override
        public Func integrateImpl() {
            return Division.divide(Constants.TWO, this);
        }
        
        @Override
        public String toString() {
            return String.format("(√%s)", f);
        }
        
        @Override
        public Stream<Func> factor() {
            return Stream.of(this);
        }
    }
    
    
    record Power(Func f, Const g) implements Pow {
        @Override
        public double eval(double x) {
            return Math.pow(f.eval(x), g.value());
        }
        
        @Override
        public Variables.Variable termVariable() {
            return f.termVariable();
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
        
        @Nullable
        @Override
        public Func integrateImpl() {
            return Multiplication.multiply(g().reciporical(), power(f, g.subtract(Constants.ONE)));
        }
    }
    
    record EBaseExponent(Func g) implements PowerFunc {
        @Override
        public double eval(double x) {
            return Math.exp(g.eval(x));
        }
        
        @Override
        public Variables.Variable termVariable() {
            return g.termVariable();
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
        public Variables.Variable termVariable() {
            return g.termVariable();
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
        public Variables.Variable termVariable() {
            if (f.termVariable().equals(g.termVariable())) return f.termVariable();
            else return Variables.X;
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
