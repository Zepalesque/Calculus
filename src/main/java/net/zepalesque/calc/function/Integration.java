package net.zepalesque.calc.function;

import net.zepalesque.calc.Calculus;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class Integration {
    
    public record DefIntegral(IndefIntegral integral, Const a, Const b) {
        
        @Nullable
        Const calculate() {
            Func func = this.integral().integrate();
            Const upper = func.eval(b);
            Const lower = func.eval(a);
            return upper.subtract(lower);
        }
    }



    public record IndefIntegral(Func func, Variables.Variable differential) {
        
        @Override
        public String toString() {
            // TODO: get console display of ∫ to work
            return String.format("⌠ %s %s", Calculus.noParenthesisString(func), differential.differential());
        }
        
        public Func integrate() {
            Func f = Integration.integrate(func, differential, null);
            if (success(f)) {
                if (f.termVariable().equals(differential)) return f;
                else {
                    Variables.Variable v = f.termVariable();
                    while (v != differential) {
                        Variables.Variable finalV = v;
                        f = f.substitute(v.function(), f1 -> f1.equals(finalV));
                        if (f == null) break;
                        v = f.termVariable();
                    }
                }
            }
            return f;
        }
    }
    
    // aaaaaa
    public static Func integrate(Func integrand, Variables.Variable differential, @Nullable Func outermostIn) {
        if (outermostIn == null) outermostIn = integrand;
        final Func outermostIntegrand = outermostIn;
        if (integrand.equals(differential)) return Polynomials.term(Constants.ONE_HALF, differential, Constants.TWO);
        if (integrand instanceof Addition.Sum(List<? extends Func> addends)) {
            // for addition, parts should use the individual term integrand, so input outermostIn should be input as null
            Func[] integrals = addends.stream().map(func -> integrate(func, differential, null)).toArray(Func[]::new);
            return Addition.add(integrals);
        } else if (integrand instanceof Const c) return Multiplication.multiply(c, differential.function());
        else if (integrand instanceof Term term) {
            Const c = term.g().add(Constants.ONE);
            return term.create(term.coefficient().multiply(c.reciporical()), c);
        }
        if (integrand instanceof Division.Quotient(Func numerator, Func denominator))
            if (numerator.equals(denominator.derivative()))
                return Logarithms.ln(denominator);
            else if (Division.divide(Constants.ONE, denominator).equals(numerator.derivative()))
                return Multiplication.multiply(Constants.ONE_HALF, Powers.pow(numerator, Constants.TWO));
        if (integrand instanceof SimpleIntegratableFunction sif) {
            Func integ = sif.tryIntegrate(Constants.ONE);
            if (integ != null) return integ;
        } else if (integrand instanceof Factorable factorable) {
            List<Func> factors = factorable.factor();
            if (factors.size() == 1) {
                Func fac = factors.stream().findFirst().orElse(null);
                if (fac instanceof SimpleIntegratableFunction sif) {
                    Func integ = sif.tryIntegrate(Constants.ONE);
                    if (integ != null) return integ;
                }
            } else if (factors.size() == 2) for (int i = 0; i < 2; i++) {
                Func a = factors.get(i);
                Func b = factors.get(i == 0 ? 1 : 0);
                if (a instanceof SimpleIntegratableFunction sif) {
                    Func integ = sif.tryIntegrate(b);
                    if (integ != null) return integ;
                }
            } else {
                Const c = factors.stream().filter(func -> func instanceof Const).map(Const.class::cast).reduce(Constants.ONE, Const::multiply);
                List<Func> nonConst = factors.stream().filter(func -> !(func instanceof Const)).toList();
                Func tryInteg = tryToIntegrate(nonConst, differential, outermostIntegrand, Multiplication.multiply(nonConst.toArray(Func[]::new)));
                if (tryInteg != null) return Multiplication.multiply(tryInteg, c);
            }
            
        }/* else if (f instanceof Multiplication.Product(Set<Func> factors, List<Func> asList)) {
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
            
            // TODO: check for certain predefined integral patterns multiplied an inner function's deriv
            //  then integrate again with a different differential (substituted) and simplify all replacements in the end
            Const c = Constants.ONE;
            List<Func> facsList = new ArrayList<>(factors.stream().toList());
            for (Func fac : facsList)
                if (fac instanceof Const c1)
                    c = c.multiply(c1);
            facsList = facsList.stream().filter(fac -> !(fac instanceof Const)).flatMap(
                func -> func instanceof Factorable p ? p.factor().stream() : Stream.of(func)
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
        }*/
        return new FailedIntegral(integrand, differential);
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
        public Func substituteImpl(Func var, Predicate<Func> predicate) {
            return null;
        }
        
        @Override
        public String toString() {
            return "[INTEGRATION ERROR]";
        }
    }
    
    private static boolean longAsBoolArray(long s, int indexToCheck) {
        return (s & 0x8000000000000000L >>> indexToCheck) != 0;
    }
    
    // checks all subsets of a list of functions, and see if they're all integrable and their inner functions' derivatives all equal the other funcs
    @Nullable
    private static Func tryToIntegrate(List<Func> list, Variables.Variable differential, Func outermostIntegrand, Func partsSoFar) {
        if (list.size() > Long.SIZE) {
            // the long way /:
            return tryIntegLongList(list, differential, outermostIntegrand, partsSoFar);
        } else {
            long adder = 1;
            do {
                Func[] sub1 = list.toArray(Func[]::new), sub2 = list.toArray(Func[]::new);
                for (int i = 0; i < list.size(); i++) {
                    if (longAsBoolArray(adder, i + Long.SIZE - list.size())) sub2[i] = null;
                    else sub1[i] = null;
                }
                @Nullable Func possibleResult = setupIntegrateList(differential, outermostIntegrand, partsSoFar, sub1, sub2);
                if (possibleResult != null) return possibleResult;
                adder++;
            } while (adder < Math.pow(2, list.size()));
        }
        return null;
    }
    
    @Nullable
    private static Func tryIntegLongList(List<Func> list, Variables.Variable differential, Func outermostIntegrand, Func partsSoFar) {
        boolean[] current = new boolean[list.size()];
        current[list.size() - 1] = true;
        do {
            Func[] sub1 = list.toArray(Func[]::new),
                sub2 = list.toArray(Func[]::new);
            for (int i = 0; i < list.size(); i++) {
                if (current[i]) sub2[i] = null;
                else sub1[i] = null;
            }
            @Nullable Func possibleResult = setupIntegrateList(differential, outermostIntegrand, partsSoFar, sub1, sub2);
            if (possibleResult != null) return possibleResult;
        } while (addOne(current));
        return null;
    }
    
    private static @Nullable Func setupIntegrateList(Variables.Variable differential, Func outermostIntegrand, Func partsSoFar, Func[] sub1, Func[] sub2) {
        List<Func> tested = Arrays.stream(sub1).filter(Objects::nonNull).toList();
        List<Func> possibleDerivs = Arrays.stream(sub2).filter(Objects::nonNull).toList();
        List<Func> nonConstDerivs = possibleDerivs.stream().filter(func -> !(func instanceof Const) && func != null).toList();
        Const derivMult = possibleDerivs.stream().filter(func -> func instanceof Const).map(Const.class::cast).reduce(Constants.ONE, Const::multiply);
        @Nullable Func possibleResult = integListImpl(tested, nonConstDerivs, derivMult, differential, outermostIntegrand, partsSoFar);
        if (possibleResult != null) return possibleResult;
        return null;
    }
    
    @Nullable
    private static Func integListImpl(List<Func> tested, List<Func> nonConstDerivs, Const derivMult, Variables.Variable differential, Func outermostIntegrand, Func partsSoFar) {
        if (tested.stream().allMatch(SimpleIntegratableFunction.class::isInstance)) {
            List<SimpleIntegratableFunction> integratables = tested.stream()
                .map(SimpleIntegratableFunction.class::cast).toList();
            if (integratables.size() == 1) {
                SimpleIntegratableFunction f = integratables.getFirst();
                Func attempt = f.tryIntegrate(Multiplication.multiply(nonConstDerivs.toArray(Func[]::new)));
                if (attempt != null) return attempt;
            }
            Optional<SimpleIntegratableFunction> extract = integratables.stream().findFirst();
            if (extract.isPresent()) {
                Func inner = extract.get().inner();
                Func deriv = inner.derivative();
                if (deriv instanceof Factorable factorable) {
                    List<Func> derivativeFactors = factorable.factor();
                    List<Func> nonConst = derivativeFactors.stream().filter(f -> !(f instanceof Const)).toList();
                    if (nonConst.equals(nonConstDerivs)) {
                        Variables.Variable var = Variables.of(Multiplication.multiply(inner, derivMult.reciporical()), Variables.getNextForDifferential(differential.identifier()));
                        List<@Nullable Func> substituted = integratables.stream().map(sif -> sif.substitute(var, func -> func.equals(inner))).toList();
                        Func result = substituted.stream().anyMatch(Objects::isNull)
                            ? null : integrate(Multiplication.multiply(substituted.toArray(Func[]::new)), var, outermostIntegrand);
                        if (success(result)) return result;
                    }
                } else if (integratables.size() == 1) {
                    SimpleIntegratableFunction f = integratables.getFirst();
                    Func attempt = f.tryIntegrate(Multiplication.multiply(nonConstDerivs.toArray(Func[]::new)));
                    if (success(attempt)) return attempt;
                }
                
            }
            // else try parts
            /*Func u = Multiplication.multiply(tested.stream().filter(Objects::nonNull).toArray(Func[]::new));
            Func dv = Multiplication.multiply(Stream.concat(Stream.of(u), nonConstDerivs.stream()).toArray(Func[]::new));
            Func uv = Multiplication.multiply(u.substituteImpl(differential.function(), differential::equals), differential.function());
            Func vdu = Multiplication.multiply(differential, u.derivative());
            
            while (uv.termVariable() != Variables.X)
                uv = Multiplication.multiply(uv.substituteImpl(uv.termVariable().function(), uv.termVariable()::equals), uv.termVariable().derivative());
            while (vdu.termVariable() != Variables.X)
                vdu = Multiplication.multiply(vdu.substituteImpl(vdu.termVariable().function(), vdu.termVariable()::equals), vdu.termVariable().derivative());
            
            if (vdu.equals(outermostIntegrand)) {
                return Multiplication.multiply(partsSoFar, Constants.ONE_HALF);
            } else {
                Func integrated = integrate(vdu, differential, outermostIntegrand).negate();
                if (integrated != null) Addition.add(uv, integrated);
            }*/
        }
        return null;
    }
    
    private static boolean addOne(boolean[] subsetChecker) {
        boolean[] added = new boolean[subsetChecker.length],
            zero = new boolean[subsetChecker.length],
            max = new boolean[subsetChecker.length],
            carry = new boolean[subsetChecker.length];
        Arrays.fill(max, true);
        added[subsetChecker.length - 1] = true;
        added[added.length - 1] = true;
        while (!Arrays.equals(added, zero)) for (int i = 0; i < subsetChecker.length; i++) {
            carry[i] = subsetChecker[i] && added[i];
            subsetChecker[i] = subsetChecker[i] ^ added[i];
            added[i] = i != subsetChecker.length - 1 && carry[i + 1];
        }
        return !Arrays.equals(subsetChecker, max);
    }
    
    public static boolean success(@Nullable Func f) {
        return f != null && !(f instanceof FailedIntegral);
    }
}
