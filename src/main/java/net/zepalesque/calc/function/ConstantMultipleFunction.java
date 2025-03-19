package net.zepalesque.calc.function;

// TODO
public interface ConstantMultipleFunction extends Func {
    
    Const constantMultiplier();
    
    // Also serves as a 'withoutMultiplier' function
    Func toMultiply();
    
    
}
