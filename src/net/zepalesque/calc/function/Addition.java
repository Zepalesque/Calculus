package net.zepalesque.calc.function;

import java.util.ArrayList;
import java.util.List;

public class Addition {
    
    public static Func add(Func... funcs) {
        if (funcs.length == 1) return funcs[0];
        List<Func> addends = new ArrayList<>();
        Const c = Constants.ZERO;
        for (Func f : funcs)
            if (f instanceof Const c1) c = c.add(c1);
            else if (f instanceof Sum(List<? extends Func> addends1)) for (Func f1 : addends1)
                if (f1 instanceof Const c2) c = c.add(c2);
                else if (!f1.isZero()) addends.add(f1);
                else if (!f.isZero()) addends.add(f);
        if (!c.equals(Constants.ZERO)) addends.add(c);
        if (addends.isEmpty()) return Constants.ZERO;
        if (addends.size() == 1) return addends.getFirst();
        return new Sum(addends);
    }
    
    // Polynomial, or otherwise
    record Sum(List<? extends Func> addends) implements MultiTermFunction {
        
        public Sum {
            if (addends.isEmpty()) throw new IllegalArgumentException("At least one argument is required");
        }
        
        @Override
        public double eval(double x) {
            double val = 0;
            for (Func f : addends) val += f.eval(x);
            
            return val;
        }
        
        @Override
        public Func derivative() {
            Func[] derivs = new Func[addends.size()];
            for (int i = 0; i < addends.size(); i++)
                derivs[i] = addends.get(i).derivative();
            List<Func> derivList = new ArrayList<>(List.of(derivs));
            derivList.removeIf(Func::isZero);
            if (derivList.isEmpty()) return Constants.ZERO;
            return add(derivList.toArray(Func[]::new));
        }
        
        @Override
        public String toString() {
            return String.format("(%s)", internalString());
        }
        
        @Override
        public String internalString() {
            StringBuilder sb = new StringBuilder();
            Func first = addends.getFirst();
            if (first instanceof Multiplication.Product m) sb.append(m.internalString());
            else sb.append(addends.getFirst());
            
            for (int i = 1; i < addends.size(); i++) {
                Func addend = addends.get(i);
                String s;
                if (addend instanceof Multiplication.Product m) s = m.internalString();
                else s = addends.get(i).toString();
                if (s.charAt(0) == '-') {
                    sb.append(" - ");
                    sb.append(s.substring(1));
                } else {
                    sb.append(" + ");
                    sb.append(s);
                }
            }
            return sb.toString();
        }
    }
}
