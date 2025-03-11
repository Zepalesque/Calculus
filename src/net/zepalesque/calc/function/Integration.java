package net.zepalesque.calc.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Integration {
    
    
    public record IndefIntegral(Func func, Variables.Variable differential) {
        
        @Override
        public String toString() {
            return String.format("∫ %s %s", func, differential.differential());
        }
        
        public Func integrate() {
            return Integration.integrate(func, differential);
        }
    }
    
    // aaaaaa
    public static Func integrate(Func f, Variables.Variable differential) {
        if (f instanceof Addition.Sum(List<? extends Func> addends)) {
            Func[] integrals = addends.stream().map(func -> integrate(func, differential)).toArray(Func[]::new);
            return Addition.add(integrals);
        } else if (f instanceof Const c) return Multiplication.multiply(c, differential.function());
        else if (f instanceof Term term) {
            Const c = term.g().add(Constants.ONE);
            return term.create(term.coefficient().multiply(c.reciporical()), c);
        }
        if (f instanceof Division.Quotient(Func numerator, Func denominator))
            if (numerator.equals(denominator.derivative()))
                return Logarithms.ln(denominator);
            else if (Division.divide(Constants.ONE, denominator).equals(numerator.derivative()))
                return Multiplication.multiply(Constants.ONE_HALF, Powers.pow(numerator, Constants.TWO));
        if (differential != Variables.X) {
            // TODO
        } else if (f instanceof SimpleIntegratableFunction sif) {
            Func integ = sif.tryIntegrate(Constants.ONE);
            if (integ != null) return integ;
        } else if (f instanceof Multiplication.Product(Set<Func> factors, List<Func> asList)) {
            if (asList.size() == 1) {
                Func fac = asList.stream().findFirst().orElse(null);
                if (fac instanceof SimpleIntegratableFunction sif) {
                    Func integ = sif.tryIntegrate(Constants.ONE);
                    if (integ != null) return integ;
                }
            } else if (asList.size() == 2) for (int i = 0; i < 2; i++) {
                Func a = asList.get(i);
                Func b = asList.get(i == 0 ? 1 : 0);
                if (a instanceof SimpleIntegratableFunction sif) {
                    Func integ = sif.tryIntegrate(b);
                    if (integ != null) return integ;
                }
            }
            
            // TODO: check for certain predefined integral patterns multiplied an inner function's derivative
            //  then integrate again with a different differential (substituted) and simplify all replacements in the end
            Const c = Constants.ONE;
            List<Func> facsList = new ArrayList<>(factors.stream().toList());
            for (Func fac : facsList)
                if (fac instanceof Const c1)
                    c = c.multiply(c1);
            facsList = facsList.stream().filter(fac -> !(fac instanceof Const)).flatMap(
                func -> func instanceof Powers.Pow p ? p.factor() : Stream.of(func)
            ).toList();
            
            for (int test = 0; test < facsList.size(); test++) {
                Func[] possibleDerivs = Stream.concat(
                    facsList.subList(0, test).stream(),
                    facsList.subList(test + 1, facsList.size()).stream()).toArray(Func[]::new);
                Func possibleDeriv = Multiplication.multiply(possibleDerivs);
                Func f1 = facsList.get(test);
                if (f1 instanceof SimpleIntegratableFunction sif) {
                    Func integ = sif.tryIntegrate(possibleDeriv);
                    if (integ != null) return integ;
                }
            }
        }
        return new FailedIntegral(f, differential);
    }
    
    private record FailedIntegral(Func attempted, Variables.Variable differential) implements Func {
        
        @Override
        public Const eval(Const x) {
            return Constants.NAN;
        }
        
        @Override
        public Variables.Variable termVariable() {
            return Variables.X;
        }
        
        @Override
        public Func derivative() {
            return this;
        }
        
        @Override
        public String toString() {
            return "[INTEGRATION ERROR]";
        }
    }
}
