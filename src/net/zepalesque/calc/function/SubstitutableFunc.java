package net.zepalesque.calc.function;

import org.jetbrains.annotations.Nullable;

public interface SubstitutableFunc {
    
    /**
     * Returns a copy of this with the function substituted, or {@code null} if the substitution fails
     * @param f the function (ex: integral u-substitution)
     * @return
     */
    @Nullable
    Func substitute(Func f);
}
