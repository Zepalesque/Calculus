package net.zepalesque.calc.function;

import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public interface Func {
    
    Const eval(Const x);
    
    Variables.Variable termVariable();
    
    Func derivative();
    
    default Func negate() {
        return new Negation(this);
    }
    
    @Nullable
    Func substituteImpl(Func var, Predicate<Func> predicate);
    
    @Nullable
    default Func substitute(Func var, Predicate<Func> predicate) {
        if (predicate.test(this)) return var;
        else return substituteImpl(var, predicate);
    }
    
    record Negation(Func func) implements Func {
        @Override
        public Const eval(Const x) {
            return func.eval(x).negate();
        }
        
        @Override
        public Variables.Variable termVariable() {
            return this.func().termVariable();
        }
        
        @Override
        public Func derivative() {
            return func.derivative().negate();
        }
        
        @Override
        public Func negate() {
            return func;
        }
        
        @Override
        public Func substituteImpl(Func var, Predicate<Func> predicate) {
            Func f = this.func().substitute(var, predicate);
            return f == null ? null : f.negate();
        }
        
        @Override
        public String toString() {
            return String.format("-%s", func);
        }
    }
    
}
