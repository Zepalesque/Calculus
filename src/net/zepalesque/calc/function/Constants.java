package net.zepalesque.calc.function;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final DecimalFormat FORMAT = new DecimalFormat("#.####");
    
    private static final Map<Double, Const> CONSTANTS = new HashMap<>();
    private static final Map<String, Const> NAMED_CONSTANTS = new HashMap<>();
    
    public static final Const NEG_ONE = constant(-1);
    public static final Const ZERO = constant(0);
    public static final Const ONE = constant(1);
    public static final Const TWO = constant(2);
    public static final Const THREE = constant(3);
    public static final Const ONE_HALF = ONE.divideBy(TWO) ;
    public static final Const PI = namedConstant("π", Math.PI);
    public static final Const NEG_PI = namedConstant("-π", -Math.PI);
    public static final Const PI_OVER_TWO = PI.divideBy(TWO);
    
    public static final Const E = namedConstant("e", Math.E);
    
    public static final Const INF = namedConstant("∞", Double.POSITIVE_INFINITY);
    public static final Const NEG_INF = new NamedConstant("-∞", Double.NEGATIVE_INFINITY);
    public static final Const NAN = namedConstant("NaN", Double.NaN);
  
    public static Const constant(double value) {
        if (Double.isNaN(value)) return NAN;
        else if (value == Double.POSITIVE_INFINITY) return INF;
        else if (value == Double.NEGATIVE_INFINITY) return NEG_INF;
        else if (value == Math.PI) return PI;
        else if (value == Math.E) return E;
        else if (CONSTANTS.containsKey(value)) return CONSTANTS.get(value);
        else {
            Constant c = new Constant(value);
            CONSTANTS.put(value, c);
            return c;
        }
    }
    
    private static Const namedConstant(String name, double value) {
        if (NAMED_CONSTANTS.containsKey(name)) return NAMED_CONSTANTS.get(name);
        else {
            NamedConstant c = new NamedConstant(name, value);
            NAMED_CONSTANTS.put(name, c);
            return c;
        }
    }
    
    record Constant(double value) implements Const {
        @Override
        public boolean isNamed() {
            return false;
        }
        
        @Override
        public Const negate() {
            if (this.equals(NAN)) return NAN;
            else if (this.equals(INF)) return NEG_INF;
            else if (this.equals(NEG_INF)) return INF;
            else if (this.equals(ZERO)) return ZERO;
            return constant(-this.value);
        }
        
        @Override
        public Const multiply(Const other) {
            if (other.equals(NAN) || this.equals(NAN)) return NAN;
            if (Double.isInfinite(this.value())) if (other.equals(ZERO)) return NAN;
            else if (other.value() < 0) return this.value() < 0 ? NEG_INF : INF;
            else return this.value() > 0 ? INF : NEG_INF;
            else if (Double.isInfinite(other.value())) if (this.equals(ZERO)) return NAN;
            else if (this.value() < 0) return other.value() < 0 ? NEG_INF : INF;
            else return other.value() > 0 ? INF : NEG_INF;
            else if (this.equals(ZERO) || other.equals(ZERO)) return ZERO;
            else {
                double val = this.value() * other.value();
                if (other.isNamed()) return namedConstant(String.format("(%s * %s)", this, other), val);
                else return constant(val);
            }
        }
        
        @Override
        public Const divideBy(Const other) {
            if (other.equals(NAN) || this.equals(NAN)) return NAN;
            if (Double.isInfinite(this.value()) && Double.isInfinite(other.value())) return NAN;
            else if (Double.isInfinite(other.value())) return ZERO;
            else if (other.equals(ONE)) return this;
            else if (other.equals(NEG_ONE)) return this.negate();
            else if (this.equals(other)) return ONE;
            else if (this.equals(other.negate())) return NEG_ONE;
            else if (other.equals(ZERO)) return this.equals(ZERO) ? NAN : this.value() > 0 ? INF : NEG_INF;
            else if (this.equals(ZERO)) return ZERO;
            else {
                double val = this.value() / other.value();
                if (other.isNamed()) return namedConstant(String.format("(%s / %s)", this, other), val);
                else return constant(val);
            }
        }
        
        @Override
        public Const add(Const other) {
            if (other.equals(NAN) || this.equals(NAN)) return NAN;
            else if (other.equals(INF))
                if (this.equals(NEG_INF)) return NAN;
                else return INF;
            else if (other.equals(NEG_INF))
                if (this.equals(INF)) return NAN;
                else return NEG_INF;
            else if (other.equals(ZERO)) return this;
            else if (this.equals(ZERO)) return other;
            else {
                double val = this.value() + other.value();
                if (other.isNamed()) return namedConstant(String.format("(%s + %s)", this, other), val);
                else return constant(val);
            }
        }
        
        @Override
        public Const subtract(Const other) {
            if (other.equals(NAN) || this.equals(NAN)) return NAN;
            else if (other.equals(INF))
                if (this.equals(INF)) return NAN;
                else return NEG_INF;
            else if (this.equals(INF))
                if (other.equals(INF)) return NAN;
                else return INF;
           
            else if (this.equals(other)) return ZERO;
            else if (other.equals(ZERO)) return this;
            else if (this.equals(ZERO)) return other.negate();
            else {
                double val = this.value() - other.value();
                if (other.isNamed()) return namedConstant(String.format("(%s - %s)", this, other), val);
                else return constant(val);
            }
        }
        
        @Override
        public Const pow(Const other) {
            if (other.equals(NAN) || this.equals(NAN)) return NAN;
            else if (other.equals(ZERO))
                if (Double.isInfinite(this.value()) || this.equals(ZERO))
                    return NAN;
                else return ONE;
            else if (this.equals(ONE))
                if (other.equals(INF))
                    return NAN;
                else return this;
            else if (this.equals(ZERO)) return ZERO;
            else {
                double val = Math.pow(this.value(), other.value());
                if (other.isNamed()) return namedConstant(String.format("(%s ^ %s)", this, other), val);
                else return constant(val);
            }
        }
        
        @Override
        public Const sin() {
            if (this.value() == 0) return ZERO;
            else {
                double val = Math.sin(this.value());
                if (val % 1 == 0) return constant(val);
                else return namedConstant(String.format("(sin(%s))", this), val);
            }
        }
        
        @Override
        public Const cos() {
            if (this.value() == 0) return ONE;
            else {
                double val = Math.cos(this.value());
                if (val % 1 == 0) return constant(val);
                else return namedConstant(String.format("(cos(%s))", this), val);
            }
        }
        
        @Override
        public Const tan() {
            if (this.value() == 0) return ZERO;
            else {
                double val = Math.tan(this.value());
                if (val % 1 == 0) return constant(val);
                else if (val % Math.PI == Math.PI / 2) return NAN;
                else return namedConstant(String.format("(tan(%s))", this), val);
            }
        }
        
        @Override
        public Const asin() {
            if (this.value() > 1 || this.value() < -1) return NAN;
            else if (this.value() == 0) return ZERO;
            else if (this.value() == 1) return PI_OVER_TWO;
            else if (this.value() == -1) return PI_OVER_TWO.negate();
            else {
                double val = Math.asin(this.value());
                if (val % 1 == 0) return constant(val);
                else return namedConstant(String.format("(arcsin(%s))", this), val);
            }
        }
        
        @Override
        public Const acos() {
            if (this.value() > 1 || this.value() < -1) return NAN;
            else if (this.value() == 0) return PI_OVER_TWO;
            else if (this.value() == 1) return ZERO;
            else if (this.value() == -1) return PI;
            else {
                double val = Math.acos(this.value());
                if (val % 1 == 0) return constant(val);
                else return namedConstant(String.format("(arccos(%s))", this), val);
            }
        }
        
        @Override
        public Const atan() {
            if (this.value() == 0) return ZERO;
            else {
                double val = Math.atan(this.value());
                if (val % 1 == 0) return constant(val);
                else return namedConstant(String.format("(arctan(%s))", this), val);
            }
        }
        
        @Override
        public Const csc() {
            if (this.value() % Math.PI == 0) return NAN;
            else {
                double val = 1 / Math.sin(this.value());
                if (val % 1 == 0) return constant(val);
                else return namedConstant(String.format("(csc(%s))", this), val);
            }
        }
        
        @Override
        public Const sec() {
            if (this.value() % Math.PI == Math.PI / 2) return NAN;
            else {
                double val = 1 / Math.cos(this.value());
                if (val % 1 == 0) return constant(val);
                else return namedConstant(String.format("(sec(%s))", this), val);
            }
        }
        
        @Override
        public Const cot() {
            if (this.value() % Math.PI == Math.PI / 2) return ZERO;
            else {
                double val = 1 / Math.tan(this.value());
                if (val % 1 == 0) return constant(val);
                else return namedConstant(String.format("(cot(%s))", this), val);
            }
        }
        
        @Override
        public Const acsc() {
            if (this == ONE) return PI_OVER_TWO;
            else if (this == NEG_ONE) return PI_OVER_TWO.negate();
            else if (this.value() > -1 && this.value() < 1) return NAN;
            else {
                Const reciporical = this.reciporical();
                if (reciporical.value() > 1 || reciporical.value() < -1) return NAN;
                else if (reciporical.value() == 0) return ZERO;
                else if (reciporical.value() == 1) return PI_OVER_TWO;
                else if (reciporical.value() == -1) return PI_OVER_TWO.negate();
                else {
                    double val = Math.asin(reciporical.value());
                    if (val % 1 == 0) return constant(val);
                    else return namedConstant(String.format("(arccsc(%s))", this), val);
                }
            }
        }
        
        @Override
        public Const asec() {
            if (this == ONE) return ZERO;
            else if (this == NEG_ONE) return PI;
            else if (this.value() > -1 && this.value() < 1) return NAN;
            else {
                Const reciporical = this.reciporical();
                if (reciporical.value() > 1 || reciporical.value() < -1) return NAN;
                else if (reciporical.value() == 0) return PI_OVER_TWO;
                else if (reciporical.value() == 1) return ZERO;
                else if (reciporical.value() == -1) return PI;
                else {
                    double val = Math.acos(reciporical.value());
                    if (val % 1 == 0) return constant(val);
                    else return namedConstant(String.format("(arcsec(%s))", this), val);
                }
            }
        }
        
        @Override
        public Const acot() {
            if (this.value() == 0) return PI_OVER_TWO;
            else {
                double val = Math.atan(-this.value()) + Math.PI / 2;
                if (val % 1 == 0) return constant(val);
                else return namedConstant(String.format("(arccot(%s))", this), val);
            }
        }
        
        @Override
        public Const ln() {
            if (this.value() < 0) return NAN;
            if (this.value() == 0) return NEG_INF;
            return constant(Math.log(this.value()));
        }
        
        @Override
        public Const sqrt() {
            if (this.value() < 0) return NAN;
            return constant(Math.sqrt(this.value()));
        }
        
        @Override
        public Const exp() {
            return constant(Math.exp(this.value()));
        }
        
        @Override
        public String toString() {
            if (this.value() % 1 != 0 && this.reciporical().value() % 1 == 0) {
                return String.format("(1/%s)", this.reciporical());
            }
            return FORMAT.format(this.value);
        }
    }
    
    sealed interface NamedConst extends Const permits NamedConstant {
        @Override
        Const add(Const other);
    }
    
    record NamedConstant(String name, double value) implements NamedConst {
        @Override
        public String toString() {
            return name;
        }
        
        @Override
        public boolean isNamed() {
            return true;
        }
        
        @Override
        public Const add(Const other) {
            return namedConstant(String.format("(%s + %s)", this, other), this.value() + other.value());
        }
        
        @Override
        public Const subtract(Const other) {
            return namedConstant(String.format("(%s - %s)", this, other), this.value() - other.value());
        }
        
        @Override
        public Const multiply(Const other) {
            return namedConstant(String.format("(%s * %s)", this, other), this.value() * other.value());
        }
        
        @Override
        public Const divideBy(Const other) {
            if (other.equals(NAN) || this.equals(NAN)) return NAN;
            else if (Double.isInfinite(this.value()) && Double.isInfinite(other.value())) return NAN;
            else if (Double.isInfinite(other.value())) return ZERO;
            else if (other.equals(ONE)) return this;
            else if (other.equals(NEG_ONE)) return this.negate();
            else if (this.equals(other)) return ONE;
            else if (this.equals(other.negate())) return NEG_ONE;
            else if (other.equals(ZERO)) return this.equals(ZERO) ? NAN : this.value() > 0 ? INF : NEG_INF;
            else if (this.equals(ZERO)) return ZERO;
            return namedConstant(String.format("(%s/%s)", this, other), this.value() / other.value());
        }
        
        @Override
        public Const pow(Const other) {
            if (other.equals(NAN) || this.equals(NAN)) return NAN;
            else if (other.equals(ZERO))
                if (Double.isInfinite(this.value()) || this.equals(ZERO))
                    return NAN;
                else return ONE;
            else if (this.equals(ONE))
                if (other.equals(INF))
                    return NAN;
                else return this;
            else if (this.equals(ZERO)) return ZERO;
            return namedConstant(String.format("(%s^%s)", this, other), Math.pow(this.value(), other.value()));
        }
        
        @Override
        public Const ln() {
            if (this.value() < 0) return NAN;
            else if (this == E) return ONE;
            else return namedConstant(String.format("ln(%s)", this), Math.log(this.value()));
        }
        
        @Override
        public Const sqrt() {
            if (this.value() < 0) return NAN;
            double val = Math.sqrt(this.value());
            return namedConstant(String.format("(√%s)", this), val);
        }
        
        @Override
        public Const exp() {
            return E.pow(this);
        }
        
        @Override
        public Const sin() {
            if (Double.isNaN(this.value()) || Double.isInfinite(this.value())) return NAN;
            double modPi = this.value() % Math.PI;
            double mod2Pi = this.value() % (2 * Math.PI);
            if (modPi == 0) return ZERO;
            else if (mod2Pi == Math.PI / 2) return ONE;
            else if (mod2Pi - Math.PI == - Math.PI / 2) return NEG_ONE;
            else {
                double val = Math.sin(this.value());
                return namedConstant(String.format("(sin(%s))", this), val);
            }
        }
        
        @Override
        public Const cos() {
            if (Double.isNaN(this.value()) || Double.isInfinite(this.value())) return NAN;
            double mod2Pi = this.value() % (2 * Math.PI);
            if (mod2Pi == Math.PI / 2 || mod2Pi - Math.PI == - Math.PI / 2) return ZERO;
            else if (mod2Pi == 0) return ONE;
            else if (mod2Pi == Math.PI) return NEG_ONE;
            else {
                double val = Math.sin(this.value());
                return namedConstant(String.format("(cos(%s))", this), val);
            }
        }
        
        @Override
        public Const tan() {
            if (Double.isNaN(this.value()) || Double.isInfinite(this.value())) return NAN;
            double modPi = this.value() % Math.PI;
            if (modPi == 0) return ZERO;
            else if (modPi == Math.PI / 2) return NAN;
            else {
                double val = Math.tan(this.value());
                return Double.isNaN(val)
                    ? NAN
                    : namedConstant(String.format("(tan(%s))", this), val);
            }
        }
        
        @Override
        public Const asin() {
            if (this.value() < -1 || this.value() > 1) return NAN;
            double val = Math.asin(this.value());
            return namedConstant(String.format("(arcsin(%s))", this), val);
        }
        
        @Override
        public Const acos() {
            if (this.value() < -1 || this.value() > 1) return NAN;
            double val = Math.acos(this.value());
            return namedConstant(String.format("(arccos(%s))", this), val);
        }
        
        @Override
        public Const atan() {
            if (this == INF) return PI_OVER_TWO;
            else if (this == NEG_INF) return PI_OVER_TWO.negate();
            double val = Math.atan(this.value());
            return namedConstant(String.format("(arctan(%s))", this), val);
        }
        
        @Override
        public Const csc() {
            if (Double.isNaN(this.value()) || Double.isInfinite(this.value())) return NAN;
            double modPi = this.value() % Math.PI;
            double mod2Pi = this.value() % (2 * Math.PI);
            if (modPi == 0) return NAN;
            else if (mod2Pi == Math.PI / 2) return ONE;
            else if (mod2Pi - Math.PI == - Math.PI / 2) return NEG_ONE;
            double val = 1 / Math.sin(this.value());
            return namedConstant(String.format("(csc(%s))", this), val);
        }
        
        @Override
        public Const sec() {
            if (Double.isNaN(this.value()) || Double.isInfinite(this.value())) return NAN;
            double modPi = this.value() % Math.PI;
            double mod2Pi = this.value() % (2 * Math.PI);
            if (modPi == Math.PI / 2) return NAN;
            else if (mod2Pi == 0) return ONE;
            else if (mod2Pi == Math.PI) return NEG_ONE;
            double val = 1 / Math.cos(this.value());
            return namedConstant(String.format("(sec(%s))", this), val);
            
        }
        
        @Override
        public Const cot() {
            if (Double.isNaN(this.value()) || Double.isInfinite(this.value())) return NAN;
            double modPi = this.value() % Math.PI;
            if (modPi == 0) return NAN;
            else if (modPi == Math.PI / 2) return ZERO;
            else {
                double val = 1 / Math.tan(this.value());
                return namedConstant(String.format("(cot(%s))", this), val);
            }
        }
        
        @Override
        public Const acsc() {
            if (this == INF || this == NEG_INF) return ZERO;
            if (this.value() > -1 && this.value() < 1) return NAN;
            double val = this.reciporical().asin().value();
            return namedConstant(String.format("(arccsc(%s))", this), val);
        }
        
        @Override
        public Const asec() {
            if (this == INF || this == NEG_INF) return PI_OVER_TWO;
            if (this.value() > -1 && this.value() < 1) return NAN;
            double val = this.reciporical().acos().value();
            return namedConstant(String.format("(arcsec(%s))", this), val);
        }
        
        @Override
        public Const acot() {
            if (this == INF) return ZERO;
            else if (this == NEG_INF) return PI;
            double val = PI_OVER_TWO.add(this.atan().negate()).value();
            return namedConstant(String.format("(arcsec(%s))", this), val);
        }
        
        @Override
        public Const negate() {
            if (this.equals(INF)) return NEG_INF;
            else if (this.equals(NEG_INF)) return INF;
            else if (this.equals(NAN)) return NAN;
            else if (this.equals(PI)) return NEG_PI;
            return namedConstant(String.format("(-%s)", this), -this.value());
        }
    }
}
