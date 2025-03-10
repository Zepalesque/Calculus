package net.zepalesque.calc.function;

public interface Term<T extends Term<T>> extends Powers.PowerFunc {
    
    @Override
    default Func f() {
       return create(coefficient(), Constants.ONE);
    }
    
    default Func g() {
        return power();
    }
    
    Const coefficient();
    
    Const power();
    
    T create(Const coefficient, Const power);
}
