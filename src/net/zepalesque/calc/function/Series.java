package net.zepalesque.calc.function;

import java.util.ArrayList;
import java.util.List;


@Deprecated
public class Series {
    
    public record TaylorSeries(Func f, char originalID) {
        
        public Func approximate(Const center, int degree) {
            List<Func> terms = new ArrayList<>();
            Func base = Constants.constant(f.eval(center.value()));
            terms.add(base);
            Func currentDeriv = f.derivative();
            for (int i = 1; i <= degree; i++) {
                int factorial = 1;
                for (int i1 = 1; i1 <= i; i1++) factorial *= i1;
                TaylorTerm term = new TaylorTerm(Constants.constant(currentDeriv.eval(center.value())).divideBy(Constants.constant(factorial)), Constants.constant(i), center);
                currentDeriv = currentDeriv.derivative();
                terms.add(term);
            }
            return new TaylorSeriesHolder(new Addition.Sum(terms.reversed()), degree, 0, originalID);
        }
        
    }
    
    record TaylorTerm(Const coefficient, Const power, Const centerX) implements Func {
        
        @Override
        public boolean isZero() {
            return coefficient.isZero();
        }
        
        @Override
        public double eval(double x) {
            return coefficient.value() / Math.pow(x - centerX.value(), power.value());
        }
        
        @Override
        public Func derivative() {
            return new TaylorTerm(power.multiply(coefficient), power.add(Constants.ONE.negate()), centerX);
        }
        
        @Override
        public String toString() {
            if (power.value() == 0) return String.format("%s", coefficient);
            else {
                String variable = centerX.equals(Constants.ZERO) ? "x" : String.format("(%s - %s)", centerX, centerX);
                if (power.value() == 1) return String.format("%s%s", coefficient, variable);
                else return String.format("%s%s^%s", coefficient, variable, power);
            }
        }
    }
    
    
    public record TaylorSeriesHolder(Func f, int precision, int derivativeNum, char originalID) implements Func {
        @Override
        public String toString() {
            return f.toString();
        }
        
        public String header() {
            return String.format("Taylor series of %s(x):", originalID);
        }
        
        @Override
        public double eval(double x) {
            return f.eval(x);
        }
        
        @Override
        public Func derivative() {
            return new TaylorSeriesHolder(f.derivative(), precision, derivativeNum + 1, originalID);
        }
    }
}
