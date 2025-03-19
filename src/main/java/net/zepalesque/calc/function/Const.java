package net.zepalesque.calc.function;

import java.util.function.Predicate;

public sealed interface Const extends Func permits Constants.Constant, Constants.NamedConst {
    boolean isNamed();
    
    double value();
    
    @Override
    default Const eval(Const x) {
        return this;
    }
    
    @Override
    default Const derivative() {
        return Constants.ZERO;
    }
    
    @Override
    default Variables.Variable termVariable() {
        return null;
    }
    
    default Const reciporical() {
        return Constants.ONE.divideBy(this);
    }
    
    @Override
    
    default Const substituteImpl(Func var, Predicate<Func> predicate) {
        return this;
    }
    
    Const negate();
    
    Const multiply(Const other);
    
    Const divideBy(Const other);
    
    Const add(Const other);
    
    Const subtract(Const other);
    
    Const pow(Const other);
    
    Const sin();
    
    Const cos();
    
    Const tan();
    
    Const asin();
    
    Const acos();
    
    Const atan();
    
    Const csc();
    
    Const sec();
    
    Const cot();
    
    Const acsc();
    
    Const asec();
    
    Const acot();
    
    Const ln();
    
    Const sqrt();
    
    Const exp();
}
