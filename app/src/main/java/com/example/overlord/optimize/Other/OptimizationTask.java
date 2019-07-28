package com.example.overlord.optimize.Other;

import android.util.Pair;

import com.example.overlord.optimize.Arithmetics.Equotion;
import com.example.overlord.optimize.Arithmetics.Matrix;
import com.example.overlord.optimize.Arithmetics.Number;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class OptimizationTask implements Serializable {
    private Number[] targetV;
    private Number targetC;
    private Limit limit;
    private Number[] basis;

    private ArrayList<Equotion> conditions;

    private int currentIteration;
    private ArrayList<ArrayList<ArrayList<Number>>> tables; //сначала выбирается номер таблицы, затем столбец, затем строка
    private ArrayList<ArrayList<Integer>> columnsIndexes = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> rowsIndexes = new ArrayList<>();


    public OptimizationTask(ArrayList<Equotion> conditions, Limit limit, Number targetC, Number[] targetV) {
        this.targetV = targetV;
        this.targetC = targetC;
        this.conditions = conditions;
        for (Equotion e : conditions) {
            if (e.getValue().getNumerator() < 0) {
                e.multiplyOnCoeff(-1);
            }
        }
        this.limit = limit;
        if (limit.equals(Limit.MAX)) {
            for (Number n : targetV) {
                n.multiply(new Number(-1));
            }
            targetC.multiply(new Number(-1));
        }
        currentIteration = 0;
        findBasis();
    }

    public Result solve() {
        if (tables == null) {
            buildTable();
        }

        while (true) {
            ArrayList<ArrayList<Number>> table = tables.get(currentIteration);
            if (isUnlimit(table)) {
                return new Result(limit, true, false);
            }
            if (isResultTable(table)) {
                if (isEmptySign()) {
                    return new Result(limit, false, true);
                } else {
                    ArrayList<Number> res = table.get(table.size() - 1);

                    Number[] point = new Number[res.size() - 1 + table.size() - 1];
                    for (int i = 0; i < res.size() - 1; i++) {
                        point[rowsIndexes.get(currentIteration).get(i)] = (Number) res.get(i).clone();
                    }
                    for (int i = 0; i < table.size() - 1; i++) {
                        point[columnsIndexes.get(currentIteration).get(i)] = new Number(0);
                    }

                    if (isZeroCoeffs(table)) {
                        ArrayList<Equotion> equotions = new ArrayList<>();
                        for (int j = 0; j < table.get(0).size() - 1; j++) {
                            Number[] coeffs = new Number[columnsIndexes.get(currentIteration).size() + rowsIndexes.get(currentIteration).size()];
                            Arrays.fill(coeffs, new Number(0));
                            int k;
                            for (k = 0; k < table.size() - 1; k++) {
                                coeffs[columnsIndexes.get(currentIteration).get(k)] = (Number) table.get(k).get(j).clone();
                            }
                            coeffs[rowsIndexes.get(currentIteration).get(j)] = new Number(1);
                            Number value = (Number) table.get(k).get(j).clone();
                            equotions.add(new Equotion(coeffs, Equotion.Sign.EQOALLY, value));
                        }
                        OptimizationTask tmp = new OptimizationTask(equotions, limit, getTargetC(), getTargetV());
                        tmp.buildTable(point);
                        return tmp.solve();
                    }

                    Number f = (Number) res.get(res.size() - 1).clone();
                    if (limit == Limit.MIN) {
                        f.multiply(new Number(-1));
                    }
                    return new Result(limit, f, point);
                }
            }

            doIteration(findOptimalKeyElement());
        }
    }

    public Result getResult() {
        ArrayList<ArrayList<Number>> table = tables.get(currentIteration);
        if (isUnlimit(table)) {
            return new Result(limit, true, false);
        }
        if (isResultTable(table)) {
            if (isEmptySign()) {
                return new Result(limit, false, true);
            } else {
                ArrayList<Number> res = table.get(table.size() - 1);

                Number[] point = new Number[res.size() - 1 + table.size() - 1];
                for (int i = 0; i < res.size() - 1; i++) {
                    point[rowsIndexes.get(currentIteration).get(i)] = (Number) res.get(i).clone();
                }
                for (int i = 0; i < table.size() - 1; i++) {
                    point[columnsIndexes.get(currentIteration).get(i)] = new Number(0);
                }

                if (isZeroCoeffs(table)) {
                    ArrayList<Equotion> equotions = new ArrayList<>();
                    for (int j = 0; j < table.get(0).size() - 1; j++) {
                        Number[] coeffs = new Number[columnsIndexes.get(currentIteration).size() + rowsIndexes.get(currentIteration).size()];
                        Arrays.fill(coeffs, new Number(0));
                        int k;
                        for (k = 0; k < table.size() - 1; k++) {
                            coeffs[columnsIndexes.get(currentIteration).get(k)] = (Number) table.get(k).get(j).clone();
                        }
                        coeffs[rowsIndexes.get(currentIteration).get(j)] = new Number(1);
                        Number value = (Number) table.get(k).get(j).clone();
                        equotions.add(new Equotion(coeffs, Equotion.Sign.EQOALLY, value));
                    }
                    OptimizationTask tmp = new OptimizationTask(equotions, limit, getTargetC(), getTargetV());
                    tmp.buildTable(point);
                    return new Result(tmp);
                }

                Number f = (Number) res.get(res.size() - 1).clone();
                if (limit == Limit.MIN) {
                    f.multiply(new Number(-1));
                }
                return new Result(limit, f, point);
            }
        }
        return null;
    }

    private boolean isUnlimit(ArrayList<ArrayList<Number>> table) {
        Number zero = new Number(0);
        for (int i = 0; i < table.size(); i++) {
            int j;
            for (j = 0; j < table.get(i).size(); j++) {
                if (j == table.get(i).size() - 1) {
                    if (table.get(i).get(j).lessThen(zero)) {
                        return true;
                    }
                    break;
                } else {
                    if (table.get(i).get(j).moreThen(zero)) {
                        break;
                    }
                }
            }
            if (j == table.get(i).size()) {
                return true;
            }
        }
        return false;
    }

    private boolean isEmptySign() {
        for (int index : rowsIndexes.get(currentIteration)) {
            if (index > targetV.length - 1) {
                return true;
            }
        }
        return false;
    }

    public boolean isResultTable(ArrayList<ArrayList<Number>> table) {
        Number zero = new Number(0);
        for (int i = 0; i < table.size() - 1; i++) {
            if (table.get(i).get(table.get(i).size() - 1).lessThen(zero)) {
                return false;
            }
        }
        return true;
    }

    private boolean isZeroCoeffs(ArrayList<ArrayList<Number>> table) {
        Number zero = new Number(0);
        for (int i = 0; i < table.size() - 1; i++) {
            if (!table.get(i).get(table.get(i).size() - 1).equals(zero)) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<ArrayList<Number>> doIteration(Pair<Integer, Integer> keyElement) {
        ArrayList<ArrayList<Number>> table = getTableCopy(tables.get(currentIteration));
        if (currentIteration < tables.size() - 1) {
            int tmp = tables.size();
            for (int i = currentIteration + 1; i < tmp; i++) {
                tables.remove(tables.size() - 1);
                columnsIndexes.remove(columnsIndexes.size() - 1);
                rowsIndexes.remove(rowsIndexes.size() - 1);
            }
        }
        int column = keyElement.second;
        int row = keyElement.first;
        Number keyEl = (Number) table.get(column).get(row).clone();

        for (int i = 0; i < table.size(); i++) {
            if (i != column) {
                table.get(i).get(row).divide(keyEl);
            }
        }

        for (int j = 0; j < table.size(); j++) {
            if (j != column) {
                for (int i = 0; i < table.get(j).size(); i++) {
                    if (i != row) {
                        table.get(j).get(i).sub(Number.multiply(table.get(j).get(row), table.get(column).get(i)));
                    }
                }
            }
        }

        for (int i = 0; i < table.get(column).size(); i++) {
            if (i == row) {
                table.get(column).get(i).upSideDown();
            } else {
                Number tmpKey = Number.multiply(keyEl, new Number(-1));
                table.get(column).get(i).divide(tmpKey);
            }
        }

        rowsIndexes.add(new ArrayList<Integer>());
        columnsIndexes.add(new ArrayList<Integer>());
        if (currentIteration == tables.size() - 1) {
            tables.add(table);
        } else {
            tables.remove(currentIteration);
            tables.add(currentIteration, table);
        }
        currentIteration++;

        for (int i = 0; i < rowsIndexes.get(currentIteration - 1).size(); i++) {
            rowsIndexes.get(currentIteration).add(rowsIndexes.get(currentIteration - 1).get(i));
        }

        for (int i = 0; i < columnsIndexes.get(currentIteration - 1).size(); i++) {
            columnsIndexes.get(currentIteration).add(columnsIndexes.get(currentIteration - 1).get(i));
        }

        int tmp = rowsIndexes.get(currentIteration).remove(row);
        rowsIndexes.get(currentIteration).add(row, columnsIndexes.get(currentIteration).get(column));
        columnsIndexes.get(currentIteration).remove(column);
        columnsIndexes.get(currentIteration).add(column, tmp);

        if (columnsIndexes.get(currentIteration).get(column) > targetV.length - 1) {
            table.remove(column);
            columnsIndexes.get(currentIteration).remove(column);
        }

        for (int i = 0; i < table.get(0).size(); i++) {
            for (int j = 0; j < table.size(); j++) {
                System.out.print(table.get(j).get(i) + " ");
            }
            System.out.println();
        }
        System.out.println();

        return table;
    }

    public Number[] findBasis() {
        int newLen = conditions.get(0).getCoeffs().length + conditions.size();
        Number[] basis = new Number[newLen];
        Arrays.fill(basis, new Number(0));

        for (int j = 0; j < conditions.size(); j++) {
            basis[newLen - conditions.size() + j] = conditions.get(j).getValue();
        }
        return basis;
    }

    public Pair<Integer, Integer> findOptimalKeyElement() {
        if (tables.size() == 0) {
            try {
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Pair<Integer, Integer> pair = new Pair<>(0, 0);
        ArrayList<ArrayList<Number>> table = tables.get(currentIteration);

        ArrayList<Number> beta = table.get(table.size() - 1);
        Number zero = new Number(0);
        Number tmp = new Number(-1);

        for (int i = 0; i < table.size() - 1; i++) {
            ArrayList<Number> column = table.get(i);
            if (column.get(column.size() - 1).lessThen(zero)) {
                for (int j = 0; j < column.size() - 1; j++) {
                    if (column.get(j).moreThen(zero) && (tmp.moreThen(Number.divide(beta.get(j), column.get(j))) || tmp.lessThen(zero))) {
                        tmp = Number.divide(beta.get(j), column.get(j));
                        pair = new Pair<>(j, i);
                    }
                }
            }
        }

        if(isAvalableEl(pair, tables.get(currentIteration))){
            return pair;
        }
        else {
            return new Pair<>(-1,-1);
        }
    }

    public boolean isAvalableEl(Pair<Integer, Integer> point, ArrayList<ArrayList<Number>> table) {
        ArrayList<Number> column = table.get(point.second);
        ArrayList<Number> beta = table.get(table.size() - 1);
        Number zero = new Number(0);

        if (column.get(column.size() - 1).lessThen(zero) && column.get(point.first).moreThen(zero)) {
            Number tmp = Number.divide(beta.get(point.first), column.get(point.first));
            for (int j = 0; j < column.size() - 1; j++) {
                if (column.get(j).moreThen(zero) && (tmp.moreThen(Number.divide(beta.get(j), column.get(j))) || tmp.lessThen(zero))) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    public ArrayList<ArrayList<Number>> buildTable(Number[] basis) {
        tables = new ArrayList<>();
        ArrayList<ArrayList<Number>> table = new ArrayList<>();
        columnsIndexes = new ArrayList<>();
        columnsIndexes.add(new ArrayList<Integer>());
        rowsIndexes = new ArrayList<>();
        rowsIndexes.add(new ArrayList<Integer>());
        this.basis = basis;
        Number zero = new Number(0);

        if (basis.length != targetV.length) {
            try {
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < basis.length; i++) {
            if (!basis[i].equals(zero)) {
                rowsIndexes.get(currentIteration).add(i);
            } else {
                columnsIndexes.get(currentIteration).add(i);
            }
        }

        Number[][] mat = new Number[conditions.size()][basis.length + 1];
        for (int i = 0; i < conditions.size(); i++) {
            Arrays.fill(mat[i], new Number(0));
            for (int j = 0; j < conditions.get(i).getCoeffs().length; j++) {
                mat[i][j] = (Number) conditions.get(i).getCoeffs()[j].clone();
            }
            mat[i][basis.length] = conditions.get(i).getValue();
        }
        Matrix matrix = new Matrix(mat);
        matrix.gaussMethod(rowsIndexes.get(currentIteration));

        Number[] tmpF = new Number[targetV.length];
        Number tmpC = (Number) targetC.clone();

        for (int i = 0; i < targetV.length; i++) {
            tmpF[i] = (Number) targetV[i].clone();
        }

        for (int i : rowsIndexes.get(currentIteration)) {
            for (int j = 0; j < mat.length; j++) {
                if (!mat[j][i].equals(new Number(0))) {
                    for (int k = 0; k < mat[j].length - 1; k++) {
                        if (k != i) {
                            tmpF[k].sub(Number.divide(Number.multiply(mat[j][k], tmpF[i]), mat[j][i]));

                        }
                    }
                    tmpC.add(Number.divide(Number.multiply(mat[j][mat[j].length - 1], tmpF[i]), mat[j][i]));
                    tmpF[i] = new Number(0);
                    break;
                }
            }
        }

        for (int ind : columnsIndexes.get(currentIteration)) {
            ArrayList<Number> column = new ArrayList<Number>();
            Number c = tmpF[ind];
            for (int j = 0; j < rowsIndexes.get(currentIteration).size(); j++) {
                column.add(mat[j][ind]);
            }
            column.add(c);
            table.add(column);
        }

        ArrayList<Number> consts = new ArrayList<Number>();
        for (int j = 0; j < rowsIndexes.get(currentIteration).size(); j++) {
            consts.add(mat[j][mat[j].length - 1]);
        }
        consts.add(Number.multiply(tmpC, new Number(-1)));
        table.add(consts);

        tables.add(table);

        for (int i = 0; i < table.get(0).size(); i++) {
            for (int j = 0; j < table.size(); j++) {
                System.out.print(table.get(j).get(i) + " ");
            }
            System.out.println();
        }
        System.out.println();

        return table;
    }

    public ArrayList<ArrayList<Number>> buildTable() {
        tables = new ArrayList<>();
        ArrayList<ArrayList<Number>> table = new ArrayList<>();
        columnsIndexes = new ArrayList<>();
        columnsIndexes.add(new ArrayList<Integer>());
        rowsIndexes = new ArrayList<>();
        rowsIndexes.add(new ArrayList<Integer>());
        Number zero = new Number(0);

        Number[] basis = findBasis();

        ArrayList<Equotion> conditions = new ArrayList<Equotion>();
        for (int j = 0; j < this.conditions.size(); j++) {
            Number[] coeffs = this.conditions.get(j).getCoeffs();

            Number[] newCoeffs = new Number[basis.length];
            Arrays.fill(newCoeffs, new Number(0));
            for (int i = 0; i < coeffs.length; i++) {
                newCoeffs[i] = (Number) coeffs[i].clone();
            }
            newCoeffs[basis.length - this.conditions.size() + j] = new Number(1);

            Equotion equotion = this.conditions.get(j).clone();
            equotion.setCoeffs(newCoeffs);
            conditions.add(equotion);
        }

        for (int i = 0; i < basis.length - conditions.size(); i++) {
            columnsIndexes.get(currentIteration).add(i);
        }

        for (int i = basis.length - conditions.size(); i < basis.length; i++) {
            rowsIndexes.get(currentIteration).add(i);
        }

        for (int i : columnsIndexes.get(currentIteration)) {
            ArrayList<Number> column = new ArrayList<Number>();
            Number c = new Number(0);
            for (int j = 0; j < rowsIndexes.get(currentIteration).size(); j++) {
                column.add(conditions.get(j).getCoeffs()[i]);
                c.sub(conditions.get(j).getCoeffs()[i]);
            }
            column.add(c);
            table.add(column);
        }

        ArrayList<Number> consts = new ArrayList<Number>();
        Number c = new Number(0);
        for (int j = 0; j < rowsIndexes.get(currentIteration).size(); j++) {
            consts.add(conditions.get(j).getValue());
            c.sub(conditions.get(j).getValue());
        }
        consts.add(c);
        table.add(consts);

        tables.add(table);

        for (int i = 0; i < table.get(0).size(); i++) {
            for (int j = 0; j < table.size(); j++) {
                System.out.print(table.get(j).get(i) + " ");
            }
            System.out.println();
        }
        System.out.println();

        return table;
    }

    public Number[] getTargetV() {
        Number[] ne = new Number[targetV.length];
        for (int i = 0; i < targetV.length; i++) {
            if (limit.equals(Limit.MAX)) {
                ne[i] = new Number(-1).multiply(targetV[i]);
            } else {
                ne[i] = (Number) targetV[i].clone();
            }
        }

        return ne;
    }

    public Number getTargetC() {
        Number c = (Number) targetC.clone();
        if (limit.equals(Limit.MAX)) {
            c.multiply(new Number(-1));
        }

        return c;
    }

    private ArrayList<ArrayList<Number>> getTableCopy(ArrayList<ArrayList<Number>> table) {
        ArrayList<ArrayList<Number>> newTable = new ArrayList<>();

        for (int i = 0; i < table.size(); i++) {
            newTable.add(new ArrayList<Number>());
            for (int j = 0; j < table.get(i).size(); j++) {
                newTable.get(i).add((Number) table.get(i).get(j).clone());
            }
        }

        return newTable;
    }

    public ArrayList<ArrayList<ArrayList<Number>>> getTables() {
        return tables;
    }

    public ArrayList<ArrayList<Integer>> getColumnsIndexes() {
        return columnsIndexes;
    }

    public ArrayList<ArrayList<Integer>> getRowsIndexes() {
        return rowsIndexes;
    }

    public int getCurrentIteration() {
        return currentIteration;
    }

    public void setCurrentIteration(int currentIteration) {
        this.currentIteration = currentIteration;
    }

    public class Result {
        private Limit limit;
        private Number f;
        private Number[] point;
        private boolean isInfinit = false;
        private boolean isEmpty = false;
        private OptimizationTask nextTask;

        public Result(Limit limit, Number f, Number[] point) {
            this.limit = limit;
            this.f = f;
            this.point = point;
        }

        public Result(Limit limit, boolean isInfinit, boolean isEmpty) {
            this.limit = limit;
            this.isInfinit = isInfinit;
            this.isEmpty = isEmpty;
        }

        public Result(OptimizationTask nextTask) {
            this.nextTask = nextTask;
        }

        public OptimizationTask getNextTask() {
            return nextTask;
        }

        public Limit getLimit() {
            return limit;
        }

        public void setLimit(Limit limit) {
            this.limit = limit;
        }

        public Number getF() {
            return f;
        }

        public void setF(Number f) {
            this.f = f;
        }

        public Number[] getPoint() {
            return point;
        }

        public void setPoint(Number[] point) {
            this.point = point;
        }

        public boolean isInfinit() {
            return isInfinit;
        }

        public void setInfinit(boolean infinit) {
            isInfinit = infinit;
        }

        public boolean isEmpty() {
            return isEmpty;
        }

        public void setEmpty(boolean empty) {
            isEmpty = empty;
        }

        private String arrayToString(Number[] ar) {
            String s = "(";
            int i;
            for (i = 0; i < ar.length - 1; i++) {
                s += ar[i] + ",";
            }
            s += ar[i] + ")";
            return s;
        }

        @Override
        public String toString() {
            if (isEmpty) {
                return "Пустое множество";
            }
            if (isInfinit) {
                String s = "Система не ограниченна ";
                switch (limit) {
                    case MIN:
                        return s + "снизу";
                    case MAX:
                        return s + "сверху";
                }
            }

            String res = "";
            switch (limit) {
                case MAX:
                    res = "F(max)";
                    break;
                case MIN:
                    res = "F(min)";
                    break;
            }
            return res + "= " + f + " в точке " + arrayToString(point);
        }
    }

    public enum Limit {
        MIN, MAX
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OptimizationTask)) return false;
        OptimizationTask that = (OptimizationTask) o;
        return currentIteration == that.currentIteration &&
                Arrays.equals(targetV, that.targetV) &&
                Objects.equals(targetC, that.targetC) &&
                limit == that.limit &&
                Objects.equals(conditions, that.conditions) &&
                Objects.equals(tables, that.tables) &&
                Objects.equals(columnsIndexes, that.columnsIndexes) &&
                Objects.equals(rowsIndexes, that.rowsIndexes);
    }

    public Number[] getBasis() {
        return basis;
    }

    public ArrayList<Equotion> getConditions() {
        return conditions;
    }

    public Limit getLimit() {
        return limit;
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(targetC, limit, conditions, currentIteration, tables, columnsIndexes, rowsIndexes);
        result = 31 * result + Arrays.hashCode(targetV);
        return result;
    }
}
