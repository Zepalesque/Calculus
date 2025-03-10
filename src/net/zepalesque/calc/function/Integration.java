package net.zepalesque.calc.function;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
    
    
    public static Func integrate(Func f, Variables.Variable differential) {
        if (f instanceof Addition.Sum(List<? extends Func> addends)) {
            Func[] integrals = addends.stream().map(func -> integrate(func, differential)).toArray(Func[]::new);
            return Addition.add(integrals);
        } else if (f instanceof Const c) {
            return Multiplication.multiply(c, differential.function());
        }
        else if (f instanceof Term term) {
            Const c = term.g().add(Constants.ONE);
            return term.create(term.coefficient().multiply(c.reciporical()), c);
        }
        if (f instanceof Division.Quotient(Func numerator, Func denominator)) {
            if (numerator.equals(denominator.derivative()))
                return Logarithms.ln(denominator);
            else if (Division.divide(Constants.ONE, denominator).equals(numerator.derivative())) {
                return Multiplication.multiply(Constants.ONE_HALF, Powers.pow(numerator, Constants.TWO));
            }
        }
        if (differential != Variables.X) {
            // TODO
        } else if (f instanceof Multiplication.Product(Set<Func> factors)) {
            // TODO: check for certain predefined integral patterns multiplied an inner function's derivative
            //  then integrate again with a different differential (substituted) and simplify all replacements in the end
            List<Func> facsList = factors.stream().toList();
            for (int test = 0; test < facsList.size(); test++) {
                Func[] possibleDerivs = Stream.concat(
                    facsList.subList(0, test).stream(),
                    facsList.subList(test + 1, facsList.size()).stream()).toArray(Func[]::new);
                Func possibleDeriv = Multiplication.multiply(possibleDerivs);
                
                if (f.derivative().equals(possibleDeriv)) {
                    return Multiplication.multiply(Constants.ONE_HALF, Powers.pow(f, Constants.TWO));
                }
            }
        }
        return null;
    }
}
