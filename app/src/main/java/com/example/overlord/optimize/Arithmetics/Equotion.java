package com.example.overlord.optimize.Arithmetics;


import java.io.Serializable;
import java.util.Arrays;

public class Equotion implements Serializable {
    private Number[] coeffs;
    private Sign sign;
    private Number value;

    public Equotion(Number[] coeffs, Sign sign, Number value) {
        this.coeffs = coeffs;
        this.sign = sign;
        this.value = value;
    }

    public void add(double coeff, Equotion e2) {
        if (coeffs.length != e2.coeffs.length || sign != e2.sign) {
            try {
                throw new EquotionException();
            } catch (EquotionException e) {
                e.printStackTrace();
            }
        } else {
            Number c = new Number(coeff);
            for (int i = 0; i < coeffs.length; i++) {
                coeffs[i] = Number.add(coeffs[i], Number.multiply(e2.coeffs[i], c));
            }
            value = Number.add(value, Number.multiply(e2.value, c));
        }
    }

    public void sub(double coeff, Equotion e2) {
        if (coeffs.length != e2.coeffs.length || sign != e2.sign) {
            try {
                throw new EquotionException();
            } catch (EquotionException e) {
                e.printStackTrace();
            }
        } else {
            Number c = new Number(coeff);
            for (int i = 0; i < coeffs.length; i++) {
                coeffs[i] = Number.sub(coeffs[i], Number.multiply(e2.coeffs[i], c));
            }
            value = Number.sub(value, Number.multiply(e2.value, c));
        }
    }

    public void multiplyOnCoeff(double coeff) {
        if (coeff == 0) {
            try {
                throw new EquotionException();
            } catch (EquotionException e) {
                e.printStackTrace();
            }
        } else {
            Number c = new Number(coeff);
            for (int i = 0; i < coeffs.length; i++) {
                coeffs[i] = Number.multiply(coeffs[i], c);
            }

            value = Number.multiply(value, c);

            if (coeff < 0 && sign != Sign.EQOALLY) {
                switch (sign) {
                    case LESS:
                        sign = Sign.MORE;
                        break;
                    case MORE:
                        sign = Sign.LESS;
                        break;
                    case LESSOREQ:
                        sign = Sign.MOREOREQ;
                        break;
                    case MOREOREQ:
                        sign = Sign.LESSOREQ;
                        break;
                }
            }
        }
    }

    public void divideByCoeff(double coeff) {
        if (coeff == 0) {
            try {
                throw new EquotionException();
            } catch (EquotionException e) {
                e.printStackTrace();
            }
        } else {
            Number c = new Number(coeff);
            for (int i = 0; i < coeffs.length; i++) {
                coeffs[i] = Number.divide(coeffs[i], c);
            }

            value = Number.divide(value, c);

            if (coeff < 0 && sign != Sign.EQOALLY) {
                switch (sign) {
                    case LESS:
                        sign = Sign.MORE;
                        break;
                    case MORE:
                        sign = Sign.LESS;
                        break;
                    case LESSOREQ:
                        sign = Sign.MOREOREQ;
                        break;
                    case MOREOREQ:
                        sign = Sign.LESSOREQ;
                        break;
                }
            }
        }
    }

    public Equotion clone() {
        Number[] coef = Arrays.copyOf(coeffs, coeffs.length);
        Sign s = Sign.valueOf(sign.name());
        Number v = (Number) value.clone();
        return new Equotion(coef, s, v);
    }

    public Number[] getCoeffs() {
        return coeffs;
    }

    public void setCoeffs(Number[] coeffs) {
        this.coeffs = coeffs;
    }

    public Sign getSign() {
        return sign;
    }

    public void setSign(Sign sign) {
        this.sign = sign;
    }

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    private class EquotionException extends Exception {
    }

    @Override
    public String toString() {
        String tF = "";
        tF += coeffs[0] + "X" + 1;
        for (int i = 1; i < coeffs.length; i++) {
            if (coeffs[i].lessThen(new Number(0))) {
                tF += coeffs[i] + "X" + (i + 1);
            } else {
                tF += "+" + coeffs[i] + "X" + (i + 1);
            }
        }
        tF += " = " + value;
        return tF;
    }

    public enum Sign {
        MORE, LESS, EQOALLY, MOREOREQ, LESSOREQ
    }
}
