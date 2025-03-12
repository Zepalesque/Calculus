package net.zepalesque.calc;

import net.zepalesque.calc.function.Func;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public interface Factorable extends Func {
    
    default List<Func> factor() {
        return this.factorsImpl().stream().flatMap(f -> f instanceof Factorable fac ? fac.factor().stream() : Stream.of(f)
        ).toList();
    }
    
    // Return null if and only if factor() is overriden
    Collection<Func> factorsImpl();
}
