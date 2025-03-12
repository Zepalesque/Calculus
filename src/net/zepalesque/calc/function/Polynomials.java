package net.zepalesque.calc.function;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class Polynomials {
    
    public static Func term(Const coefficient, Variables.Variable var, Const power) {
        if (coefficient.equals(Constants.ZERO)) {
            return Constants.ZERO;
        } else if (power.equals(Constants.ZERO)) {
            return coefficient;
        } else return new PTerm(coefficient, var, power);
    }
    
    public record PTerm(Const coefficient, Variables.Variable var, Const power) implements Term {
        
        public Const eval(Const x) {
            return var.eval(x).pow(power).multiply(coefficient);
        }
        
        @Override
        public Variables.Variable termVariable() {
            return var;
        }
        
        public Func derivative() {
            if (power.equals(Constants.ZERO)) return Constants.ZERO;
            else if (power.equals(Constants.ONE)) return coefficient();
            return new PTerm(power.multiply(coefficient), var, power.add(Constants.ONE.negate()));
        }
        
        public String toString() {
            if (power.equals(Constants.ZERO) || coefficient.equals(Constants.ZERO)) {
                return String.format("%s", coefficient);
            } else if (power.equals(Constants.ONE)) {
                if (coefficient().equals(Constants.ONE)) return String.format("%s", var);
                return String.format("%s%s", coefficient, var);
            } else if (coefficient().equals(Constants.ONE) && !power.equals(Constants.ONE)) {
                return String.format("%s^%s", var, power);
            } else {
            return String.format("%s%s^%s", coefficient, var, power);
            }
        }
        
        @Override
        public Func inner() {
            return var;
        }
        
        @Nullable
        @Override
        public Func integrateImpl() {
            Const powerPlus = power.add(Constants.ONE);
            return create(coefficient.divideBy(powerPlus), powerPlus);
        }
        
        @Override
        public Func create(Const coefficient, Const power) {
            return term(coefficient, var, power);
        }
        
        @Override
        public List<Func> factor() {
            if (power().value() < 1 && power().value() > 0) return List.of(this);
            int full = (int) Math.round(power().value() % 1);
            boolean negative = full < 0;
            Const remainder = power().subtract(Constants.constant(full));
            if (negative) full = -full;
            Func[] facs = new Func[full + 2];
            Func func = !negative ? f() : Division.divide(Constants.ONE, var());
            Arrays.fill(facs, 0, full, func);
            // TODO: check for errors due to modulo not working as expected with negative #s
            if (!remainder.equals(Constants.ZERO)) facs[full + 1] = create(Constants.ONE, remainder);
            if (!coefficient.equals(Constants.ONE)) facs[full + 2] = coefficient;
            return Arrays.stream(facs).filter(Objects::nonNull).toList();
        }
        
        @Override
        public Func createWithSubstitution(Func var) {
            if (var instanceof Variables.Variable v) {
                return term(coefficient, v, power);
            } else {
                return Multiplication.multiply(coefficient, Powers.pow(var, power));
            }
        }
    }
}
