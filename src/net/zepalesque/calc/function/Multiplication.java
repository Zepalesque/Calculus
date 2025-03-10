package net.zepalesque.calc.function;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Multiplication {
    
    public static Func multiply(Func... factors) {
        if (factors.length == 0) return Constants.ZERO;
        else if (factors.length == 1) return factors[0];
        Set<Func> numerators = new HashSet<>();
        Set<Func> denominators = new HashSet<>();
        Const c = Constants.ONE;
        Const d = Constants.ONE;
        for (Func f : factors) {
            if (f.equals(Constants.ONE)) continue;
            if (f instanceof Product p) {
                Set<Func> others = p.factors();
                AtomicInteger i = new AtomicInteger(1);
                return multiply(Stream.concat(others.stream(), Arrays.stream(factors).filter(func -> {
                    if (i.get() == 1 && func.equals(p)) {
                        i.decrementAndGet();
                        return false;
                    }
                    else return true;
                })).toArray(Func[]::new));
/*                for (Func factor : factors) {
                    if (factor instanceof Const c1) c = c.multiply(c1);
                    else c.multiply(add(numerators, factor));
                }*/
            } else if (f instanceof Division.Quotient(Func numerator, Func denominator)) {
                if (!numerator.equals(Constants.ONE))
                    c.multiply(add(numerators, numerator));
                d.multiply(add(denominators, denominator));
            }
            else if (f instanceof Const c1) c = c.multiply(c1);
            else c.multiply(add(numerators, f));
            
        }
        if (!c.equals(Constants.ONE)) {
            Set<Func> cset = new HashSet<>();
            cset.add(c);
            cset.addAll(numerators);
            numerators = cset;
        }
        if (c.equals(Constants.ZERO)) return Constants.ZERO;
        if (!denominators.isEmpty())
            return Division.divide(multiply(numerators.toArray(Func[]::new)), multiply(denominators.toArray(Func[]::new)));
        else {
            if (numerators.size() == 1) return numerators.stream().findFirst().get();
            return new Product(numerators);
        }
    }
    
    // Returns any/all constant multipliers
    private static Const add(Set<Func> funcs, Func toAdd, Const degree) {
        if (toAdd instanceof Const c) return c.pow(degree);
        if (toAdd instanceof Powers.Power(Func f, Const g)) {
            return add(funcs, f, g);
        }
        if (funcs.contains(toAdd)) {
            funcs.remove(toAdd);
            funcs.add(Powers.pow(toAdd, Constants.ONE.add(degree)));
        } else {
            List<Func> powers = funcs.stream().filter(func -> func instanceof Powers.Power(Func f, Const g) && f == toAdd).toList();
            if (powers.isEmpty())
                funcs.add(Powers.pow(toAdd, degree));
            else {
                Const deg = degree;
                for (Func func : powers)
                    deg = deg.add(((Powers.Power) func).g());
                powers.forEach(funcs::remove);
                funcs.add(Powers.pow(toAdd, deg));
            }
        }
        return Constants.ONE;
    }
    
    private static Const add(Set<Func> funcs, Func toAdd) {
        return add(funcs, toAdd, Constants.ONE);
    }
    
    record Product(Set<Func> factors, List<Func> asList) implements MultiTermFunction {
        
        public Product(Set<Func> factors) {
            this(factors, factors.stream().toList());
        }
        
        public Product {
            if (factors.isEmpty()) throw new IllegalArgumentException("At least one argument is required");
        }
        
        @Override
        public double eval(double x) {
            double val = 1;
            for (Func f : factors) val *= f.eval(x);
            
            return val;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Product)) return false;
            else return factors.equals(((Product) obj).factors);
        }
        
        @Override
        public Variables.Variable termVariable() {
            Variables.Variable var = null;
            boolean success = true;
            for (Func f : factors) {
                if (f instanceof Const) continue;
                Variables.Variable var2 = f.termVariable();
                if (var == null) var = var2;
                else if (!var2.equals(var)) success = false;
            }
            return success ? var : Variables.X;
        }
        
        @Override
        public Func derivative() {
            Func[][] derivTerms = new Func[factors.size()][];
            for (int i = 0; i < derivTerms.length; i++)
                derivTerms[i] = new Func[factors.size()];
            for (int i = 0; i < derivTerms.length; i++) {
                Func[] term = factors.toArray(derivTerms[i]);
                term[i] = term[i].derivative();
                derivTerms[i] = term;
            }
            
            Func[] asProducts = Arrays.stream(derivTerms).map(Multiplication::multiply)
                .toArray(Func[]::new);
            
            return Addition.add(asProducts);
        }
        
        @Override
        public String toString() {
            return String.format("(%s)", internalString());
        }
        
        @Override
        public String internalString() {
            List<Func> funcs = factors.stream().map(Multiplication::multiply).toList();
            StringBuilder sb = new StringBuilder();
            sb.append(funcs.getFirst());
            for (int i = 1; i < funcs.size(); i++) {
                String s = funcs.get(i).toString();
                
                sb.append(" * ");
                sb.append(s);
                
            }
            return sb.toString();
        }
    }
}
