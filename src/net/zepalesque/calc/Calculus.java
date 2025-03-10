package net.zepalesque.calc;

import net.zepalesque.calc.function.Addition;
import net.zepalesque.calc.function.Constants;
import net.zepalesque.calc.function.Division;
import net.zepalesque.calc.function.Func;
import net.zepalesque.calc.function.Integration;
import net.zepalesque.calc.function.Logarithms;
import net.zepalesque.calc.function.Multiplication;
import net.zepalesque.calc.function.Polynomials;
import net.zepalesque.calc.function.Powers;
import net.zepalesque.calc.function.Trig;

// TODO: trig functions, INTEGRATION (if i can figure it out lmao), more logarithms,
//  system for 'real constants' that can be represented by an integer over an integer,
//  storing the ints to allow simplification without computation of the function (avoids floating point precision stuff)
public class Calculus {
    
    public static void main(String[] args) {
        Func basicPolynomial = Addition.add(
            Polynomials.term(Constants.ONE, Constants.THREE),
            Polynomials.term(Constants.THREE.negate(), Constants.TWO),
            Polynomials.term(Constants.THREE.negate(), Constants.ONE),
            Constants.ONE
        );
        printAll(basicPolynomial, 'f');
        
//        Series.TaylorSeries taylorSeries = new Series.TaylorSeries(basicPolynomial, 'f');
//        Func approx = taylorSeries.approximate(Constants.ZERO, 3);
//        printFuncAndDerivative(approx, 'g');
        
        Func x = Polynomials.X;
        Func xlnxMinusX = Addition.add(Multiplication.multiply(x, Logarithms.ln(x)), x.negate());
        printFuncAndDerivative(xlnxMinusX, 'h');
        
        Func lnxOverX = Division.divide(Logarithms.ln(x), x);
        printFuncAndAntiderivative(lnxOverX, 'j');
        
        Func secant = Trig.secant(x);
        Func secSquared = Powers.pow(secant, Constants.TWO);
        printDifferentiateAndIntegrate(secSquared, 'k');
        System.out.println("Note that (sec(x) ^ 2) = (tan(x) ^ 2) + 1, so thanks to the arbitrary constant of integration, C, these are equivalent.\n");
    }
    
    public static void printFuncAndDerivative(Func f, char id) {
        System.out.println();
        System.out.printf("%c(x) = %s\n", id, f);
        System.out.println("derivative:");
        System.out.printf("%c'(x) = %s\n", id, f.derivative());
        System.out.println();
    }
    public static void printFuncAndAntiderivative(Func f, char id) {
        System.out.println();
        System.out.printf("%c(x) = %s\n", id, f);
        System.out.println("antiderivative (indefinite integral):");
        Integration.IndefIntegral i = new Integration.IndefIntegral(f, f.termVariable());
        char ig = Character.toUpperCase(id);
        System.out.printf("%c(x) = %s\n", ig, i);
        System.out.printf("%c(x) = %s + C\n", ig, i.integrate());
        System.out.println();
    }
    
    public static void printAll(Func f, char id) {
        System.out.println();
        System.out.printf("%c(x) = %s\n", id, f);
        System.out.println("derivative:");
        System.out.printf("%c'(x) = %s\n", id, f.derivative());
        System.out.println("antiderivative (indefinite integral):");
        Integration.IndefIntegral i = new Integration.IndefIntegral(f, f.termVariable());
        char ig = Character.toUpperCase(id);
        System.out.printf("%c(x) = %s\n", ig, i);
        System.out.printf("%c(x) = %s + C\n", ig, i.integrate());
        System.out.println();
    }
    
    public static void printDifferentiateAndIntegrate(Func f, char id) {
        System.out.println();
        System.out.printf("%c(x) = %s\n", id, f);
        System.out.println("derivative:");
        Func deriv = f.derivative();
        System.out.printf("%c'(x) = %s\n", id, deriv);
        System.out.println("antiderivative (indefinite integral):");
        Integration.IndefIntegral i = new Integration.IndefIntegral(f, f.termVariable());
        char ig = Character.toUpperCase(id);
        System.out.printf("%c(x) = %s\n", ig, i);
        System.out.printf("%c(x) = %s + C\n", ig, i.integrate());
        Integration.IndefIntegral i1 = new Integration.IndefIntegral(deriv, deriv.termVariable());
        System.out.println("antiderivative of derivative (indefinite integral):");
        System.out.printf("%c(x) = %s\n", id, i1);
        System.out.printf("%c(x) = %s + C\n", id, i1.integrate());
        System.out.println();
    }
}
