package net.zepalesque.calc.function;

public interface Term extends Powers.Pow {
    
    @Override
    default Func f() {
       return this.inner();
    }
    
    default Const g() {
        return power();
    }
    
    Const coefficient();
    
    Func inner();
    
    Const power();
    
    Func create(Const coefficient, Const power);
}
