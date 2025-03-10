package net.zepalesque.calc.function;

import org.jetbrains.annotations.Nullable;

public interface SimpleIntegratableFunction extends Func {
    Func inner();
    
    @Nullable
    default Func tryIntegrate(Func differential) {
        if (differential.equals(Multiplication.multiply(this.derivative(), inner().derivative()))) {
            return Powers.pow(this, Constants.TWO);
        }
        if (inner().derivative().equals(differential)) return integrateImpl();
        return null;
    }
    
    @Nullable Func integrateImpl();
    
    
}
