package net.zepalesque.calc.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Multiplication {
    
    public static Func multiply(Func... factors) {
        if (factors.length == 0) return Constants.ZERO;
        else if (factors.length == 1) return factors[0];
        List<Func> numerators = new ArrayList<>();
        List<Func> denominators = new ArrayList<>();
        // TODO: Multiply all existing constants together
        for (Func f : factors) {
            if (f.equals(Constants.ONE)) continue;
            if (f instanceof Product)
                numerators.addAll(((Product) f).factors());
            else if (f instanceof Division.Quotient(Func numerator, Func denominator)) {
                if (!numerator.equals(Constants.ONE))
                    numerators.add(numerator);
                denominators.add(denominator);
            }
            else numerators.add(f);
        }
        
        if (!denominators.isEmpty())
            return Division.divide(multiply(numerators.toArray(Func[]::new)), multiply(denominators.toArray(Func[]::new)));
        else {
            if (numerators.size() == 1) return numerators.getFirst();
            return new Product(numerators);
        }
    }
    
    record Product(List<? extends Func> factors) implements MultiTermFunction {
        
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
            StringBuilder sb = new StringBuilder();
            sb.append(factors.getFirst());
            for (int i = 1; i < factors.size(); i++) {
                String s = factors.get(i).toString();
                
                sb.append(" * ");
                sb.append(s);
                
            }
            return sb.toString();
        }
    }
}
