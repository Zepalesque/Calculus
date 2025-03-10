package net.zepalesque.calc.function;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Division {
    
    public static Func divide(Func dividend, Func divisor) {
        if (dividend instanceof Const c1 && divisor instanceof Const c2) return c1.divideBy(c2);
        else if (dividend.equals(divisor)) return Constants.ONE;
        else if (dividend instanceof Multiplication.Product(Set<Func> factors))
            if (divisor instanceof Multiplication.Product(Set<Func> divisors)) {
                List<? extends Func> copy = factors.stream().toList();
                for (Func d : divisors) {
                    AtomicInteger count = new AtomicInteger(1);
                    Func[] funcs = copy.stream().filter(f -> {
                        if (f.equals(d)) count.decrementAndGet();
                        return !f.equals(d) || count.get() == 0;
                    }).toArray(Func[]::new);
                    
                    return Multiplication.multiply(funcs);
                }
            } else if (factors.contains(divisor)) {
                AtomicInteger count = new AtomicInteger(1);
                Func[] funcs = factors.stream().filter(f -> {
                    if (f.equals(divisor)) count.decrementAndGet();
                    return !f.equals(divisor) || count.get() == 0;
                }).toArray(Func[]::new);
                
                return Multiplication.multiply(funcs);
            }
        return new Quotient(dividend, divisor);
    }
    
    record Quotient(Func numerator, Func denominator) implements Func {
        
        @Override
        public double eval(double x) {
            return numerator.eval(x) / denominator.eval(x);
        }
        
        @Override
        public Variables.Variable termVariable() {
            if (numerator.termVariable().equals(denominator.termVariable())) return numerator.termVariable();
            else return Variables.X;
        }
        
        @Override
        public Func derivative() {
            Func da_b = Multiplication.multiply(numerator.derivative(), denominator);
            Func a_db = Multiplication.multiply(numerator, denominator.derivative());
            Func num = Addition.add(da_b, a_db.negate());
            Func den = Powers.pow(denominator, Constants.TWO);
            return divide(num, den);
        }
        
        @Override
        public String toString() {
            return String.format("(%s / %s)", numerator, denominator);
        }
    }
}
