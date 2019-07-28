package com.example.overlord.optimize.Arithmetics;

import java.io.Serializable;
import java.util.regex.Pattern;

public class Number implements Cloneable, Comparable<Number>, Serializable {
    private int numerator;
    private int denominator;

    public Number(int numerator, int denominator) {
        if (denominator == 0) {
            throw new ArithmeticException();
        }
        this.numerator = numerator;
        this.denominator = denominator;
        reduct();
    }

    public Number(double value) {
        if (Double.isInfinite(value)) {
            throw new ArithmeticException();
        }
        String[] d = String.valueOf(value).split("\\.");
        if (d.length == 2) {
            denominator = (int) Math.pow(10, d[1].length());
            numerator = (int) (value * denominator);
        } else {
            numerator = (int) value;
            denominator = 1;
        }
        reduct();
    }

    private void reduct() {
        if (denominator < 0) {
            numerator *= -1;
            denominator *= -1;
        }

        if (numerator==0){
            denominator = 1;
            return;
        }

        int nod = findNOD(numerator, denominator);
        numerator /= nod;
        denominator /= nod;
    }

    @Override
    public Object clone() {
        return new Number(numerator, denominator);
    }

    private static int findNOD(int a, int b) {
        a = Math.abs(a);
        b = Math.abs(b);

        if (a < b) {
            int tmp = a;
            a = b;
            b = tmp;
        }

        int r = a % b;

        if (r == 0) {
            return b;
        } else {
            return findNOD(b, r);
        }
    }

    private static int findNOK(int a, int b) {
        a = Math.abs(a);
        b = Math.abs(b);

        return a * b / findNOD(a, b);
    }

    public static Number add(Number n1, Number n2) {
        int nok = findNOK(n1.denominator, n2.denominator);
        int num = n1.numerator * (nok / n1.denominator) + n2.numerator * (nok / n2.denominator);
        return new Number(num, nok);
    }

    public static Number sub(Number n1, Number n2) {
        int nok = findNOK(n1.denominator, n2.denominator);
        int num = n1.numerator * (nok / n1.denominator) - n2.numerator * (nok / n2.denominator);
        return new Number(num, nok);
    }

    public static Number multiply(Number n1, Number n2) {
        int num = n1.numerator * n2.numerator;
        int den = n1.denominator * n2.denominator;
        return new Number(num, den);
    }

    public static Number divide(Number n1, Number n2) {
        int num = n1.numerator * n2.denominator;
        int den = n1.denominator * n2.numerator;
        return new Number(num, den);
    }

    public Number add(Number n) {
        int nok = findNOK(denominator, n.denominator);
        int num = numerator * (nok / denominator) + n.numerator * (nok / n.denominator);
        numerator = num;
        denominator = nok;
        reduct();
        return this;
    }

    public Number sub(Number n) {
        int nok = findNOK(denominator, n.denominator);
        int num = numerator * (nok / denominator) - n.numerator * (nok / n.denominator);
        numerator = num;
        denominator = nok;
        reduct();
        return this;
    }

    public Number multiply(Number n) {
        int num = numerator * n.numerator;
        int den = denominator * n.denominator;
        numerator = num;
        denominator = den;
        reduct();
        return this;
    }

    public Number divide(Number n) {
        int num = numerator * n.denominator;
        int den = denominator * n.numerator;
        numerator = num;
        denominator = den;
        reduct();
        return this;
    }

    public Number upSideDown(){
        int tmp = numerator;
        numerator = denominator;
        denominator = tmp;
        return this;
    }

    public static Number upSideDown(Number n){
        Number number = new Number(n.denominator, n.numerator);
        return number;
    }

    public boolean equals(Number n) {
        reduct();
        n.reduct();

        if (numerator == n.numerator && denominator == n.denominator) {
            return true;
        }
        return false;
    }

    public int getNumerator() {
        return numerator;
    }

    public int getDenominator() {
        return denominator;
    }

    @Override
    public String toString() {
        if (denominator == 1){
            return String.valueOf(numerator);
        }
        else {
            return String.valueOf(numerator)+"/"+String.valueOf(denominator);
        }
    }

    @Override
    public int compareTo(Number o) {
        int nok = findNOK(denominator,o.denominator);
        return numerator*nok/denominator - o.numerator*nok/o.denominator;
    }

    public boolean lessThen(Number n){
        if (this.compareTo(n)<0){
            return true;
        }
        else {
            return false;
        }
    }

    public boolean moreThen(Number n){
        if (this.compareTo(n)>0){
            return true;
        }
        else {
            return false;
        }
    }

    public boolean lessOrEqThen(Number n){
        if (this.compareTo(n)<=0){
            return true;
        }
        else {
            return false;
        }
    }

    public boolean moreOrEqThen(Number n){
        if (this.compareTo(n)>=0){
            return true;
        }
        else {
            return false;
        }
    }

    public static Pattern getPattern(){
        return Pattern.compile("(-\\d+/\\d+)|(\\d+/\\d+)|(\\d+\\.\\d+)|(-\\d+\\.\\d+)|(\\d+)|(-\\d+)");
    }

    public static Number readNumber(String str) {
        String string = str;
        String[] ar1 = string.split("/");

        if (ar1.length == 2) {
            return new Number(Integer.parseInt(ar1[0]), Integer.parseInt(ar1[1]));
        } else {
            return new Number(Double.parseDouble(string));
        }
    }
}
