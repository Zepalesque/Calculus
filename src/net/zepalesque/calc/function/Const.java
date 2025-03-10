package net.zepalesque.calc.function;

public interface Const extends Func {
    boolean isNamed();
    
    double value();
    
    default double eval(double x) {
        return value();
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
