package com.example.overlord.optimize.Arithmetics;

import java.util.ArrayList;

public class Matrix {
    private Number[][] mat;
    private int n;

    public Matrix(Number[][] mat) {
        this.mat = mat;
        this.n = mat[0].length;
    }

    public void swapLines(int a, int b) {
        if (a != b && a < mat.length & b < mat.length) {
            Number tmp;
            for (int i = 0; i < mat[0].length; i++) {
                tmp = mat[a][i];
                mat[a][i] = mat[b][i];
                mat[b][i] = tmp;
            }
        }
    }

    public void swapColumns(int a, int b) {
        if (a != b && a < mat[0].length & b < mat[0].length) {
            Number tmp;
            for (int i = 0; i < mat.length; i++) {
                tmp = mat[i][a];
                mat[i][a] = mat[i][b];
                mat[i][b] = tmp;
            }
        }
    }

    public void addLines(int line, int add, Number coeff) {
        if (line != add && line < mat[0].length & add < mat[0].length && !coeff.equals(new Number(0))) {
            for (int i = 0; i < mat[0].length; i++) {
                mat[line][i] = Number.add(mat[line][i], Number.multiply(mat[add][i], coeff));
            }
        }
    }

    public void multLine(int line, Number coeff) {
        if (line < mat[0].length && !coeff.equals(new Number(0)) && !coeff.equals(new Number(1))) {
            for (int i = 0; i < mat[0].length; i++) {
                mat[line][i] = Number.multiply(mat[line][i], coeff);
            }
        }
    }

    private void reduceColumnByLineDown(int line) {
        if (line < n) {
            Number zero = new Number(0);
            if (mat[line][line].equals(zero)){
                for (int i = line+1; i < n; i++) {
                    if (!mat[i][line].equals(zero)) {
                        swapLines(line, i);
                        break;
                    }
                }
            }
            if (mat[line][line].equals(zero)){
                return;
            }

            multLine(line, Number.divide(new Number(1),mat[line][line]));
            for (int i = line + 1; i < n; i++) {
                addLines(i, line, Number.divide(Number.multiply(mat[i][line], new Number(-1)), mat[line][line]));
            }
        }
    }

    private void reduceColumnByLineUp(int line) {
        if (line < n) {
            if (mat[line][line].equals(new Number(0))){
                return;
            }

            multLine(line, Number.divide(new Number(1),mat[line][line]));
            for (int i = line - 1; i >= 0; i--) {
                addLines(i, line, Number.divide(Number.multiply(mat[i][line], new Number(-1)), mat[line][line]));
            }
        }
    }

    public Number[][] gaussMethod(ArrayList<Integer> vars){
        this.n = vars.size();

        for (int i = 0; i < n; i++) {
            if (vars.get(i) != i) {
                swapColumns(vars.get(i), i);
            }
        }

        for (int i = 0;i<vars.size();i++){
            reduceColumnByLineDown(i);
        }

        for (int i = vars.size()-1;i>=0;i--){
            reduceColumnByLineUp(i);
        }

        for (int i = vars.size()-1; i >=0; i--) {
            if (vars.get(i) != i) {
                swapColumns(vars.get(i), i);
            }
        }

        this.n = mat[0].length;

        return mat;
    }

    public Number[][] getMat() {
        return mat;
    }
}