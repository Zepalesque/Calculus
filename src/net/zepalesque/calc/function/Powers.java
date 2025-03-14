package net.zepalesque.calc.function;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class Powers {
    
    public static Func pow(Func base, Func exponent) {
        if (base instanceof Const c1 && exponent instanceof Const c2)
            return c1.pow(c2);
        else if (base instanceof Variables.Variable v
            && exponent instanceof Const c)
            return Polynomials.term(Constants.ONE, v, c);
        else if (base instanceof Polynomials.PTerm(Const coefficient, Variables.Variable v, Const power)
            && exponent instanceof Const c)
            return Polynomials.term(coefficient.pow(c), v, power.multiply(c));
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
    
    interface PowerFunc extends Func, ParenthesisHeldFunction {
        Func f();
        Func g();
    }
    
    interface Pow extends PowerFunc, SimpleIntegratableFunction, Factorable {
        @Override
        Const g();
        
        @Override
        default Func inner() {
            return f();
        }
        
        default Collection<Func> factorsImpl() {
            return null;
        }
        
        default List<Func> factor() {
            if (g().value() < 1 && g().value() > 0) return List.of(this);
            int full = (int) Math.round(g().value() % 1);
            boolean negative = full < 0;
            Const remainder = g().subtract(Constants.constant(full));
            if (negative) full = -full;
            Func[] facs = new Func[full + 1];
            Func func = !negative ? f() : Division.divide(Constants.ONE, f());
            Arrays.fill(facs, 0, full, func);
            // TODO: check for errors due to modulo not working as expected with negative #s
            if (!remainder.equals(Constants.ZERO)) facs[full + 1] = pow(func, remainder);
            return Arrays.stream(facs).filter(Objects::nonNull).toList();
        }
        
        @Override
        default Func createWithSubstitution(Func var) {
            return pow(var, g());
        }
    }
    
    record Square(Func f) implements Pow {
        
        @Override
        public Const eval(Const x) {
            Const inner = f.eval(x);
            return inner.multiply(inner);
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
        public String internalString() {
            return String.format("%s^2", f);
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
        public List<Func> factor() {
            return List.of(f(), f());
        }
        
        @Override
        public String toString() {
            return String.format("(%s)", internalString());
        }
    }
    
    record Sqrt(Func f) implements Pow {
        @Override
        public Const eval(Const x) {
            return f.eval(x).sqrt();
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
        public String internalString() {
            return String.format("√%s", f);
        }
        
        @Override
        public String toString() {
            return String.format("(%s)", internalString());
        }
        
        @Override
        public List<Func> factor() {
            return List.of(this);
        }
    }
    
    
    record Power(Func f, Const g) implements Pow {
        @Override
        public Const eval(Const x) {
            return f.eval(x).pow(g);
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
        public String internalString() {
            return String.format("%s^%s", f, g);
        }
        
        @Override
        public String toString() {
            return String.format("(%s)", internalString());
        }
        
        @Nullable
        @Override
        public Func integrateImpl() {
            return Multiplication.multiply(g().reciporical(), power(f, g.subtract(Constants.ONE)));
        }
    }
    
    record EBaseExponent(Func g) implements PowerFunc {
        @Override
        public Const eval(Const x) {
            return g.eval(x).exp();
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
        public Func substituteImpl(Func var, Predicate<Func> predicate) {
            Func gs = this.g().substitute(var, predicate);
            if (gs != null) return pow(f(), gs);
            else return null;
        }
        
        @Override
        public Func f() {
            return Constants.E;
        }
        
        @Override
        public String toString() {
            return String.format("(%s)", internalString());
        }
        
        @Override
        public String internalString() {
            return String.format("e^%s", g());
        }
    }
    
    record Exponential(Const f, Func g) implements PowerFunc {
        @Override
        public Const eval(Const x) {
            return f.pow(g.eval(x));
        }
        
        @Override
        public Variables.Variable termVariable() {
            return g.termVariable();
        }
        
        @Override
        public Func derivative() {
            return Multiplication.multiply(this, g.derivative(), f.ln());
        }
        
        @Override
        public Func substituteImpl(Func var, Predicate<Func> predicate) {
            Func gs = this.g().substitute(var, predicate);
            if (gs != null) return pow(f(), gs);
            else return null;
        }
        
        @Override
        public String internalString() {
            return String.format("%s^%s", f(), g());
        }
        
        @Override
        public String toString() {
            return String.format("(%s)", internalString());
        }
    }
    
    
    record FPowG(Func f, Func g) implements PowerFunc {
        
        @Override
        public Const eval(Const x) {
            return f.eval(x).pow(g.eval(x));
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
        public String internalString() {
            return String.format("%s^%s", f(), g());
        }
        
        @Override
        public String toString() {
            return String.format("(%s)", internalString());
        }
        
        @Override
        public Func substituteImpl(Func var, Predicate<Func> predicate) {
            Func gs = this.g().substitute(var, predicate);
            Func fs = this.f().substitute(var, predicate);
            if (gs != null && fs != null) return pow(fs, gs);
            else return null;
        }
    }
}
