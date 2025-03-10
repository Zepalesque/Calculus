package net.zepalesque.calc.function;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Multiplication {
    
    public static Func multiply(Func... factors) {
        if (factors.length == 0) return Constants.ZERO;
        else if (factors.length == 1) return factors[0];
        Set<Func> numerators = new HashSet<>();
        Set<Func> denominators = new HashSet<>();
        // TODO: Multiply all existing constants together
        for (Func f : factors) {
            if (f.equals(Constants.ONE)) continue;
            if (f instanceof Product)
                for (Func factor : factors)
                    add(numerators, factor);
            else if (f instanceof Division.Quotient(Func numerator, Func denominator)) {
                if (!numerator.equals(Constants.ONE))
                    add(numerators, numerator);
                add(denominators, denominator);
            }
            else add(numerators, f);
        }
        
        if (!denominators.isEmpty())
            return Division.divide(multiply(numerators.toArray(Func[]::new)), multiply(denominators.toArray(Func[]::new)));
        else {
            if (numerators.size() == 1) return numerators.stream().findFirst().get();
            return new Product(numerators);
        }
    }
    
    private static void add(Set<Func> funcs, Func toAdd, Const degree) {
        if (toAdd instanceof Powers.Power(Func f, Const g)) {
            add(funcs, f, g);
        }
        if (funcs.contains(toAdd)) {
            funcs.remove(toAdd);
            funcs.add(Powers.pow(toAdd, Constants.ONE.add(degree)));
        } else {
            List<Func> powers = funcs.stream().filter(func -> func instanceof Powers.Power(Func f, Const g) && f == toAdd).toList();
            if (powers.isEmpty())
                funcs.add(toAdd);
            else {
                Const deg = degree;
                for (Func func : powers)
                    deg = deg.add(((Powers.Power) func).g());
                powers.forEach(funcs::remove);
                funcs.add(Powers.pow(toAdd, deg));
            }
        }
    }
    
    private static void add(Set<Func> funcs, Func toAdd) {
        add(funcs, toAdd, Constants.ONE);
    }
    
    record Product(Set<Func> factors) implements MultiTermFunction {
        
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
