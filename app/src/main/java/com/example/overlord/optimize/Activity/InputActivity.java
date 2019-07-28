package com.example.overlord.optimize.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.overlord.optimize.Arithmetics.Equotion;
import com.example.overlord.optimize.Arithmetics.Number;
import com.example.overlord.optimize.Fragments.VariableInput;
import com.example.overlord.optimize.Other.OptimizationTask;
import com.example.overlord.optimize.Other.Reader;
import com.example.overlord.optimize.R;

import java.util.ArrayList;
import java.util.Arrays;

public class InputActivity extends AppCompatActivity implements VariableInput.OnFragmentInteractionListener {

    private static final int TABLETEXTSIZE = 30;

    private OptimizationTask optimizationTask;
    private ArrayList<Equotion> conditions = new ArrayList<>();
    private Number[] targetFunction = new Number[0];
    private Number[] basis = new Number[0];
    private OptimizationTask.Limit limit = OptimizationTask.Limit.MAX;

    private ArrayList<ArrayList<VariableInput>> coefficientMatrixVI = new ArrayList<>();
    private ArrayList<VariableInput> eqColomnVI = new ArrayList<>();
    private ArrayList<VariableInput> basisVI = new ArrayList<>();

    private LinearLayout targetFunctionLayout;
    private LinearLayout conditionsLayout;
    private LinearLayout rightPieceLayout;
    private LinearLayout basisLayout;
    private FloatingActionButton addVariableB;
    private FloatingActionButton addConditionB;
    private FloatingActionButton removeVariableB;
    private FloatingActionButton removeConditionB;
    private Switch basisSwitch;
    private ToggleButton limitB;
    private LinearLayout solveLayout;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.newTaskB :
                Intent intent = new Intent(InputActivity.this, InputActivity.class);
                InputActivity.this.startActivity(intent);
                break;
            case R.id.openTaskB:
                Intent intent1 = new Intent(InputActivity.this, LoadActivity.class);
                InputActivity.this.startActivity(intent1);
                break;
            case R.id.saveTaskB:
                dataRefresh();
                Reader.writeTask(conditions, OptimizationTask.Limit.valueOf(limitB.getText().toString().toUpperCase()), targetFunction, basisSwitch.isChecked(), basis, this);
                Toast toast = Toast.makeText(getApplicationContext(),getResources().getText(R.string.writeSuccess), Toast.LENGTH_SHORT);
                toast.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void dataRefresh(){
        targetFunction = new Number[coefficientMatrixVI.get(0).size()];
        for(int i=0;i<coefficientMatrixVI.get(0).size();i++){
            targetFunction[i] = (Number) coefficientMatrixVI.get(0).get(i).getCoefficient().clone();
        }

        conditions = new ArrayList<>();
        for(int i=1;i<coefficientMatrixVI.size();i++){
            Number[] coefs = new Number[coefficientMatrixVI.get(i).size()];
            for(int j=0;j<coefs.length;j++){
                coefs[j] = (Number) coefficientMatrixVI.get(i).get(j).getCoefficient().clone();
            }
            Equotion equotion = new Equotion(coefs, Equotion.Sign.EQOALLY, (Number) eqColomnVI.get(i-1).getCoefficient().clone());
            conditions.add(equotion);
        }

        if(basisSwitch.isChecked()){
            basis = new Number[basisVI.size()];
            for(int i=0;i<basis.length;i++){
                basis[i] = (Number) basisVI.get(i).getCoefficient().clone();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        targetFunctionLayout = findViewById(R.id.targetFInput);
        conditionsLayout = findViewById(R.id.conds);
        basisLayout = findViewById(R.id.basisInput);
        rightPieceLayout = findViewById(R.id.rightPiece);
        addVariableB = findViewById(R.id.addVariable);
        addConditionB = findViewById(R.id.addCondition);
        removeVariableB = findViewById(R.id.removeVariable);
        removeConditionB = findViewById(R.id.removeCondition);
        basisSwitch = findViewById(R.id.basisSwitch);
        limitB = findViewById(R.id.limit);
        solveLayout = findViewById(R.id.solvationOutPut);

        limitB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                limit = OptimizationTask.Limit.valueOf(limitB.getText().toString().toUpperCase());
            }
        });

        basisSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    findViewById(R.id.basisContainer).setBackgroundResource(R.drawable.border);
                } else {
                    findViewById(R.id.basisContainer).setBackgroundResource(R.drawable.ic_remove_24dp);
                }
            }
        });

        findViewById(R.id.solve).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (targetFunction.length == 0 || basis.length == 0 || conditions.size() == 0) {
                    return;
                }

                for (int i = 0; i < targetFunction.length; i++) {
                    if (targetFunction[i] == null) {
                        return;
                    }
                }

                for (int i = 0; i < conditions.size(); i++) {
                    Equotion eq = conditions.get(i);
                    for (int j = 0; j < eq.getCoeffs().length; j++) {
                        if (eq.getCoeffs()[j] == null) {
                            return;
                        }
                    }
                }

                if (basisSwitch.isChecked()) {
                    for (int i = 0; i < basis.length; i++) {
                        if (basis[i] == null) {
                            return;
                        }
                    }
                }

                dataRefresh();
                solveLayout.removeAllViews();
                initTask();
            }
        });


        coefficientMatrixVI.add(new ArrayList<VariableInput>());
        coefficientMatrixVI.add(new ArrayList<VariableInput>());
        VariableInput vI = VariableInput.newInstance(0, 1, new Number(0), false);
        getFragmentManager().beginTransaction().add(targetFunctionLayout.getId(), vI).commit();
        coefficientMatrixVI.get(0).add(vI);

        VariableInput vI3 = VariableInput.newInstance(-1, 1, new Number(0), false);
        getFragmentManager().beginTransaction().add(basisLayout.getId(), vI3).commit();
        basisVI.add(vI3);

        VariableInput vI1 = VariableInput.newInstance(1, 1, new Number(0), false);
        getFragmentManager().beginTransaction().add(conditionsLayout.getChildAt(0).getId(), vI1).commit();
        coefficientMatrixVI.get(1).add(vI1);

        VariableInput vI2 = VariableInput.newInstance(1, -1, new Number(0), true);
        eqColomnVI.add(vI2);
        getFragmentManager().beginTransaction().add(rightPieceLayout.getChildAt(0).getId(), vI2).commit();

        Intent intent = getIntent();
        OptimizationTask task = (OptimizationTask) intent.getSerializableExtra("task");
        if(task!=null) {
            insertTask(task);
        }

        addVariableB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addVariable();
            }
        });

        addConditionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCondition();
            }
        });

        removeVariableB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeVariable();
            }
        });

        removeConditionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCondition();
            }
        });
    }

    private void insertTask(OptimizationTask task){
        coefficientMatrixVI.get(0).get(0).setCoefficient(task.getTargetV()[0]);
        for(int i=1;i<task.getTargetV().length;i++){
            addVariable();
            coefficientMatrixVI.get(0).get(coefficientMatrixVI.get(0).size()-1).setCoefficient(task.getTargetV()[i]);
        }

        if(task.getBasis()!=null){
            basisSwitch.setChecked(true);
            for(int i=0;i<task.getBasis().length;i++){
                basisVI.get(i).setCoefficient(task.getBasis()[i]);
            }
        }

        Equotion eq = task.getConditions().get(0);
        ArrayList<VariableInput> cond = coefficientMatrixVI.get(1);
        for(int j = 0;j<eq.getCoeffs().length;j++){
            cond.get(j).setCoefficient(eq.getCoeffs()[j]);
        }
        eqColomnVI.get(eqColomnVI.size()-1).setCoefficient(eq.getValue());
        for(int i=1;i<task.getConditions().size();i++){
            addCondition();
            Equotion eq1 = task.getConditions().get(i);
            ArrayList<VariableInput> cond1 = coefficientMatrixVI.get(coefficientMatrixVI.size()-1);
            for(int j = 0;j<eq1.getCoeffs().length;j++){
                cond1.get(j).setCoefficient(eq1.getCoeffs()[j]);
            }
            eqColomnVI.get(eqColomnVI.size()-1).setCoefficient(eq1.getValue());
        }

        if(task.getLimit().equals(OptimizationTask.Limit.MIN)){
            limitB.setChecked(false);
        }
    }

    private void addVariable(){
        synchronized (InputActivity.this) {
            int index = coefficientMatrixVI.get(0).size() + 1;
            VariableInput vI1 = VariableInput.newInstance(0, index, new Number(0), false);
            getFragmentManager().beginTransaction().add(targetFunctionLayout.getId(), vI1).commit();
            coefficientMatrixVI.get(0).add(vI1);

            VariableInput vI2 = VariableInput.newInstance(-1, index, new Number(0), false);
            getFragmentManager().beginTransaction().add(basisLayout.getId(), vI2).commit();
            basisVI.add(vI2);

            for (int i = 0; i < conditionsLayout.getChildCount(); i++) {
                VariableInput vI = VariableInput.newInstance(i + 1, index, new Number(0), false);
                coefficientMatrixVI.get(i + 1).add(vI);
                getFragmentManager().beginTransaction().add(conditionsLayout.getChildAt(i).getId(), vI).commit();
            }
        }
    }

    private void removeVariable(){
        synchronized (InputActivity.this) {
            if (targetFunctionLayout.getChildCount() > 1) {
                android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                int index = coefficientMatrixVI.get(0).size() - 1;
                for (int i = 0; i < coefficientMatrixVI.size(); i++) {
                    fragmentTransaction.remove(coefficientMatrixVI.get(i).remove(index));
                }
                fragmentTransaction.remove(basisVI.get(index));
                fragmentTransaction.commit();
            }
        }
    }

    private void addCondition(){
        synchronized (InputActivity.this) {
            LinearLayout newCond = new LinearLayout(InputActivity.this);
            newCond.setOrientation(LinearLayout.HORIZONTAL);
            newCond.setId(10000 * conditionsLayout.getChildCount());
            conditionsLayout.addView(newCond);
            coefficientMatrixVI.add(new ArrayList<VariableInput>());

            for (int i = 0; i < coefficientMatrixVI.get(0).size(); i++) {
                VariableInput vI = VariableInput.newInstance(conditionsLayout.getChildCount(), i + 1, new Number(0), false);
                coefficientMatrixVI.get(coefficientMatrixVI.size() - 1).add(vI);
                getFragmentManager().beginTransaction().add(newCond.getId(), vI).commit();
            }

            LinearLayout newEq = new LinearLayout(InputActivity.this);
            newEq.setOrientation(LinearLayout.HORIZONTAL);
            newEq.setId(1001 * rightPieceLayout.getChildCount());
            rightPieceLayout.addView(newEq);

            VariableInput vI = VariableInput.newInstance(conditionsLayout.getChildCount(), -1, new Number(0), true);
            eqColomnVI.add(vI);

            getFragmentManager().beginTransaction().add(newEq.getId(), vI).commit();
        }
    }

    private void removeCondition(){
        synchronized (InputActivity.this) {
            if (conditionsLayout.getChildCount() > 1) {
                android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                for (int i = 0; i < coefficientMatrixVI.get(coefficientMatrixVI.size() - 1).size(); i++) {
                    fragmentTransaction.remove(coefficientMatrixVI.get(coefficientMatrixVI.size() - 1).remove(0));
                }
                coefficientMatrixVI.remove(coefficientMatrixVI.size() - 1);
                fragmentTransaction.remove(eqColomnVI.remove(eqColomnVI.size() - 1));

                rightPieceLayout.removeViewAt(rightPieceLayout.getChildCount() - 1);
                conditionsLayout.removeViewAt(conditionsLayout.getChildCount() - 1);
                fragmentTransaction.commit();
            }
        }
    }

    @Override
    public void onFragmentIteration(Bundle data) {
        int row = data.getInt(VariableInput.ARG_PARAM1);
        int index = data.getInt(VariableInput.ARG_PARAM2);
        Number number = (Number) data.getSerializable(VariableInput.ARG_PARAM3);

        while (row > conditions.size()) {
            conditions.add(new Equotion(new Number[targetFunction.length], Equotion.Sign.EQOALLY, new Number(0)));
        }

        while (index > targetFunction.length) {
            targetFunction = Arrays.copyOf(targetFunction, targetFunction.length + 1);
            targetFunction[targetFunction.length - 1] = new Number(0);

            basis = Arrays.copyOf(basis, basis.length + 1);
            basis[basis.length - 1] = new Number(0);

            for (int i = 0; i < conditions.size(); i++) {
                Number[] newCoefs = Arrays.copyOf(conditions.get(i).getCoeffs(), conditions.get(i).getCoeffs().length + 1);
                newCoefs[newCoefs.length - 1] = new Number(0);
                conditions.get(i).setCoeffs(newCoefs);
            }
        }

        if (index == -1) {
            conditions.get(row - 1).setValue(number);
        } else {
            if (row == -1) {
                basis[index - 1] = number;
            } else {
                if (row == 0) {
                    targetFunction[index - 1] = number;
                } else {
                    conditions.get(row - 1).getCoeffs()[index - 1] = number;
                }
            }
        }
    }

    public void initTask() {
        optimizationTask = new OptimizationTask(conditions, OptimizationTask.Limit.valueOf(limitB.getText().toString().toUpperCase()), new Number(0), targetFunction);
        if (basisSwitch.isChecked()) {
            optimizationTask.buildTable(basis);
        } else {
            optimizationTask.buildTable();
        }

        solveAllFrom(0, optimizationTask.findOptimalKeyElement(), optimizationTask);
    }

    private int findTableIndex(int iteration, OptimizationTask optimizationTask){
        for(int i=0;i<solveLayout.getChildCount();i++){
            if(solveLayout.getChildAt(i) instanceof SelectTableLayout){
                if(((SelectTableLayout)solveLayout.getChildAt(i)).getTask().equals(optimizationTask) && ((SelectTableLayout)solveLayout.getChildAt(i)).getIteration() == iteration){
                    return i;
                }
            }
        }
        return -1;
    }

    public void solveAllFrom(int iteration, Pair<Integer, Integer> key, OptimizationTask optimizationTask) {
        int index = findTableIndex(iteration, optimizationTask);
        SelectTableLayout tableLayout;
        if(index != -1) {
            solveLayout.removeViews(index + 1, solveLayout.getChildCount() - 1 - index);
            tableLayout = (SelectTableLayout) solveLayout.getChildAt(index);
        }
        else {
            tableLayout = createTableView(optimizationTask.getCurrentIteration(), key, optimizationTask);
        }
        optimizationTask.setCurrentIteration(iteration);
        OptimizationTask.Result res;

        if((res = optimizationTask.getResult()) == null) {
            optimizationTask.doIteration(tableLayout.getSelectedEl());

            while ((res = optimizationTask.getResult()) == null) {
                tableLayout = createTableView(optimizationTask.getCurrentIteration(), optimizationTask.findOptimalKeyElement(), optimizationTask);
                optimizationTask.doIteration(tableLayout.getSelectedEl());
            }

            createTableView(optimizationTask.getCurrentIteration(), optimizationTask.findOptimalKeyElement(), optimizationTask);
        }

        if(res.getNextTask()!=null){
            solveAllFrom(0, res.getNextTask().findOptimalKeyElement(), res.getNextTask());
        }
        else {
            TextView t1 = new TextView(this);
            t1.setText(String.format("\n%s:", getResources().getString(R.string.answer)));
            TextView answer = new TextView(this);
            answer.setText(res.toString());
            solveLayout.addView(t1);
            solveLayout.addView(answer);
        }
    }

    public SelectTableLayout createTableView(int iteration, Pair<Integer, Integer> key, OptimizationTask optimizationTask) {
        ArrayList<ArrayList<Number>> table = optimizationTask.getTables().get(iteration);
        ArrayList<Integer> columnsIndexes = optimizationTask.getColumnsIndexes().get(iteration);
        ArrayList<Integer> rowsIndexes = optimizationTask.getRowsIndexes().get(iteration);

        SelectTableLayout tableL = new SelectTableLayout(this, iteration);
        TableRow row0 = new TableRow(this);
        row0.addView(new TextView(this));

        for (int i = 0; i < columnsIndexes.size(); i++) {
            TextView el = new TextView(this);
            el.setGravity(Gravity.CENTER_HORIZONTAL);
            el.setBackgroundResource(R.drawable.selection);
            el.setTextColor(Color.WHITE);
            el.setTextSize(TABLETEXTSIZE);
            el.setText(String.format("X%d", columnsIndexes.get(i) + 1));
            row0.addView(el);

        }
        TextView b = new TextView(this);
        b.setGravity(Gravity.CENTER_HORIZONTAL);
        b.setText("B");
        b.setBackgroundResource(R.drawable.selection);
        b.setTextColor(Color.WHITE);
        b.setTextSize(TABLETEXTSIZE);
        row0.addView(b);
        tableL.addView(row0);


        Pair<Integer, Integer> optimal = optimizationTask.findOptimalKeyElement();
        for (int i = 0; i < rowsIndexes.size(); i++) {
            TableRow row = new TableRow(this);

            TextView rIndex = new TextView(this);
            rIndex.setGravity(Gravity.CENTER_HORIZONTAL);
            rIndex.setTextSize(TABLETEXTSIZE);
            rIndex.setBackgroundResource(R.drawable.selection);
            rIndex.setTextColor(Color.WHITE);
            rIndex.setText(String.format("X%d", rowsIndexes.get(i) + 1));
            row.addView(rIndex);

            for (int j = 0; j < columnsIndexes.size(); j++) {
                SelectTableTextView el = new SelectTableTextView(this, new Pair<Integer, Integer>(i, j), tableL);
                el.setGravity(Gravity.CENTER_HORIZONTAL);
                el.setTextSize(TABLETEXTSIZE);
                el.setText(table.get(j).get(i).toString());
                if (optimal.first == i && optimal.second == j) {
                    el.setOptimal();
                }
                if (!optimizationTask.isAvalableEl(new Pair<Integer, Integer>(i, j), table)) {
                    el.setNotEvalable();
                }
                row.addView(el);
            }

            TextView beta = new TextView(this);
            beta.setGravity(Gravity.CENTER_HORIZONTAL);
            beta.setTextSize(TABLETEXTSIZE);
            beta.setText(table.get(table.size() - 1).get(i).toString());
            row.addView(beta);

            tableL.addView(row);
        }

        TableRow rowLast = new TableRow(this);
        TextView C = new TextView(this);
        C.setGravity(Gravity.CENTER_HORIZONTAL);
        C.setTextSize(TABLETEXTSIZE);
        C.setBackgroundResource(R.drawable.selection);
        C.setTextColor(Color.WHITE);
        C.setText("C");
        rowLast.addView(C);
        for (int j = 0; j < table.size(); j++) {
            TextView el = new TextView(this);
            el.setGravity(Gravity.CENTER_HORIZONTAL);
            el.setTextSize(TABLETEXTSIZE);
            el.setText(table.get(j).get(table.get(j).size() - 1).toString());
            rowLast.addView(el);
        }
        tableL.addView(rowLast);

        tableL.setSelectedEl(key);
        tableL.setTask(optimizationTask);

        solveLayout.addView(tableL);

        return tableL;
    }

    public class SelectTableTextView extends android.support.v7.widget.AppCompatTextView {
        private Pair<Integer, Integer> pos;
        private SelectTableLayout table;
        private boolean isOptimal = false;
        private boolean isAvalable = true;

        public SelectTableTextView(Context context, final Pair<Integer, Integer> pos, SelectTableLayout table1) {
            super(context);
            this.pos = pos;
            this.table = table1;
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isAvalable && (table.getSelectedEl().first!= pos.first || table.getSelectedEl().second!= pos.second)) {
                        table.setSelectedEl(pos);
                        solveAllFrom(table.getIteration(), table.getSelectedEl(),table.getTask());
                    }
                }
            });
        }

        public SelectTableTextView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public SelectTableTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public void setOptimal() {
            isOptimal = true;
            setBackgroundResource(R.drawable.optimal_24dp);
        }

        public void setNotEvalable() {
            isAvalable = false;
            setBackgroundResource(R.drawable.not_evalable_24dp);
        }

        public Pair<Integer, Integer> getPos() {
            return pos;
        }

        public void setPos(Pair<Integer, Integer> pos) {
            this.pos = pos;
        }
    }

    public class SelectTableLayout extends TableLayout {

        private int iteration;
        private Pair<Integer, Integer> selectedEl;
        private OptimizationTask task;

        public SelectTableLayout(Context context, int iteration) {
            super(context);
            this.iteration = iteration;
        }

        public SelectTableLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public Pair<Integer, Integer> getSelectedEl() {
            return selectedEl;
        }

        public void setSelectedEl(Pair<Integer, Integer> selectedEl) {
            if (selectedEl.first!=-1) {
                ((TextView) ((TableRow) getChildAt(selectedEl.first + 1)).getChildAt(selectedEl.second + 1)).setTextColor(Color.rgb(231, 16, 69));
                if (getSelectedEl() != null) {
                    ((TextView) ((TableRow) getChildAt(getSelectedEl().first + 1)).getChildAt(getSelectedEl().second + 1)).setTextColor(new TextView(this.getContext()).getTextColors().getDefaultColor());
                }
            }
            this.selectedEl = selectedEl;
        }

        public OptimizationTask getTask() {
            return task;
        }

        public void setTask(OptimizationTask task) {
            this.task = task;
        }

        public int getIteration() {
            return iteration;

        }
    }

}
