package net.zepalesque.calc;

import net.zepalesque.calc.function.Addition;
import net.zepalesque.calc.function.Constants;
import net.zepalesque.calc.function.Func;
import net.zepalesque.calc.function.Logarithms;
import net.zepalesque.calc.function.Multiplication;
import net.zepalesque.calc.function.PolyTerm;
import net.zepalesque.calc.function.Series;

// TODO: trig functions
public class Calculus {
    
    public static void main(String[] args) {
        Func basicPolynomial = Addition.add(
            new PolyTerm(Constants.ONE, Constants.THREE),
            new PolyTerm(Constants.THREE.negate(), Constants.TWO),
            new PolyTerm(Constants.THREE.negate(), Constants.ONE),
            Constants.ONE
        );
        printFuncAndDerivative(basicPolynomial, 'f');
        
//        Series.TaylorSeries taylorSeries = new Series.TaylorSeries(basicPolynomial, 'f');
//        Func approx = taylorSeries.approximate(Constants.ZERO, 3);
//        printFuncAndDerivative(approx, 'g');
        
        Func x = PolyTerm.X;
        Func xlnxMinusX = Addition.add(Multiplication.multiply(x, Logarithms.ln(x)), x.negate());
        printFuncAndDerivative(xlnxMinusX, 'h');
    }
    
    public static void printFuncAndDerivative(Func f, char id) {
        System.out.println();
//        if (f instanceof Series.TaylorSeriesHolder holder) System.out.println(holder.header());
        System.out.printf("%c(x) = %s\n", id, f);
        System.out.println("derivative:");
        System.out.printf("%c'(x) = %s\n", id, f.derivative());
        System.out.println();
    }
}
