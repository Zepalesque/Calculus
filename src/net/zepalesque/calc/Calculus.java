package net.zepalesque.calc;

import net.zepalesque.calc.function.Addition;
import net.zepalesque.calc.function.Const;
import net.zepalesque.calc.function.Constants;
import net.zepalesque.calc.function.Division;
import net.zepalesque.calc.function.Func;
import net.zepalesque.calc.function.Integration;
import net.zepalesque.calc.function.Logarithms;
import net.zepalesque.calc.function.Multiplication;
import net.zepalesque.calc.function.Polynomials;
import net.zepalesque.calc.function.Powers;
import net.zepalesque.calc.function.Trig;
import net.zepalesque.calc.function.Variables;

// TODO: trig functions, INTEGRATION (if i can figure it out lmao), more logarithms,
//  system for 'real constants' that can be represented by an integer over an integer,
//  storing the ints to allow simplification without computation of the function (avoids floating point precision stuff)

// TODO (continued...): redo most stuff perhaps, use SimpleIntegratableFunction earlier in development
//  also implement u-substitution, integration by parts, etc

public class Calculus {
    
    public static void main(String[] args) {
        Variables.Variable x = Variables.X;
        Func basicPolynomial = Addition.add(
            Polynomials.term(Constants.ONE, x, Constants.THREE),
            Polynomials.term(Constants.THREE.negate(), x, Constants.TWO),
            Polynomials.term(Constants.THREE.negate(), x, Constants.ONE),
            Constants.ONE
        );
        printAll(basicPolynomial, "f");
        
//        Series.TaylorSeries taylorSeries = new Series.TaylorSeries(basicPolynomial, 'f');
//        Func approx = taylorSeries.approximate(Constants.ZERO, 3);
//        printFuncAndDerivative(approx, 'g');
        
        Func xlnxMinusX = Addition.add(Multiplication.multiply(x, Logarithms.ln(x)), x.negate());
        printFuncAndDerivative(xlnxMinusX, "f_1");
        
        Const v = Constants.THREE;
        Const calc = xlnxMinusX.eval(v);
        System.out.printf("f_1(%s) = %s = %fd (double value)\n", v, calc, calc.value());
        
        Func lnxOverX = Division.divide(Logarithms.ln(x), x);
        printFuncAndAntiderivative(lnxOverX, "f_2");
        
        
        Func lnx = Logarithms.ln(x);
        printAll(lnx, "f_3");
        
        Func secant = Trig.secant(x);
        Func secSquared = Powers.pow(secant, Constants.TWO);
        printDifferentiateAndIntegrate(secSquared, "f_4");
//        System.out.println("Note that (sec(x) ^ 2) = (tan(x) ^ 2) + 1, so thanks to the arbitrary constant of integration, C, these are equivalent.");
    
        Func secxtanxlnsecx = Multiplication.multiply(Trig.secant(x), Trig.tangent(x), Logarithms.ln(Trig.secant(x)));
        printAll(secxtanxlnsecx, "f_5");
        
        
    }
    
    public static void printFuncAndDerivative(Func f, String id) {
        System.out.println();
        System.out.printf("%s(x) = %s\n", id, f);
        System.out.println("deriv:");
        System.out.printf("%s'(x) = %s\n", id, f.derivative());
        System.out.println();
    }
    public static void printFuncAndAntiderivative(Func f, String id) {
        System.out.println();
        System.out.printf("%s(x) = %s\n", id, f);
        System.out.println("antiderivative (indefinite integral):");
        Integration.IndefIntegral i = new Integration.IndefIntegral(f, f.termVariable());
        String ig = id.toUpperCase();
        System.out.printf("%s(x) = %s\n", ig, i);
        System.out.printf("%s(x) = %s + C\n", ig, i.integrate());
        System.out.println();
    }
    
    public static void printAll(Func f, String id) {
        System.out.println();
        System.out.printf("%s(x) = %s\n", id, f);
        System.out.println("deriv:");
        System.out.printf("%s'(x) = %s\n", id, f.derivative());
        System.out.println("antiderivative (indefinite integral):");
        Integration.IndefIntegral i = new Integration.IndefIntegral(f, f.termVariable());
        String ig = id.toUpperCase();
        System.out.printf("%s(x) = %s\n", ig, i);
        System.out.printf("%s(x) = %s + C\n", ig, i.integrate());
        System.out.println();
    }
    
    public static void printDifferentiateAndIntegrate(Func f, String id) {
        System.out.println();
        System.out.printf("%s(x) = %s\n", id, f);
        System.out.println("deriv:");
        Func deriv = f.derivative();
        System.out.printf("%s'(x) = %s\n", id, deriv);
        System.out.println("antiderivative (indefinite integral):");
        Integration.IndefIntegral i = new Integration.IndefIntegral(f, f.termVariable());
        String ig = id.toUpperCase();
        System.out.printf("%s(x) = %s\n", ig, i);
        System.out.printf("%s(x) = %s + C\n", ig, i.integrate());
        Integration.IndefIntegral i1 = new Integration.IndefIntegral(deriv, deriv.termVariable());
        System.out.println("antiderivative of deriv (indefinite integral):");
        System.out.printf("%s(x) = %s\n", id, i1);
        System.out.printf("%s(x) = %s + C\n", id, i1.integrate());
        System.out.println();
    }
}
