package net.zepalesque.calc.function;

public interface Term extends Powers.PowerFunc {
    
    @Override
    default Func f() {
       return Polynomials.X;
    }
    
    default Const g() {
        return power();
    }
    
    Const coefficient();
    
    Const power();
    
    Func create(Const coefficient, Const power);
}
