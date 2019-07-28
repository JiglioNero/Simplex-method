package com.example.overlord.optimize.Other;

import android.content.Context;
import android.graphics.Path;
import android.os.Environment;

import com.example.overlord.optimize.Arithmetics.Equotion;
import com.example.overlord.optimize.Arithmetics.Number;
import com.example.overlord.optimize.R;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Reader {

    private static String PATH = Environment.getExternalStorageDirectory()+ "/Android/data/";
    private static final Pattern numberPattern = Number.getPattern();
    private static int index = 1;

    public static ArrayList<OptimizationTask> initTasks(Context context){
        ArrayList<OptimizationTask> res = new ArrayList<>();
        try {
            String[] list = context.getAssets().list("data");
            for(String s : list){
                res.add(readAssetTask(context.getAssets().open(s)));
                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static ArrayList<OptimizationTask> readAllTasks(Context context){
        ArrayList<OptimizationTask> res = new ArrayList<>();

        index = 1;
        while(new File(context.getFilesDir(), "ex"+index+".txt").exists()){
            res.add(readTask(context));
            index++;
        }

        if(index == 1){
            res = initTasks(context);
            index = 1;
            for(OptimizationTask task : res){
                writeTask(task,context);
            }
        }

        return res;
    }

    public static void writeTask(ArrayList<Equotion> conditions, OptimizationTask.Limit limit, Number[] targetFunction, boolean isBasis, Number[] basis, Context context){
        try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(context.openFileOutput("ex"+index+".txt", Context.MODE_PRIVATE)))){
            String s = "";
            for(Number n : targetFunction){
                s+=n+" ";
            }

            s+=limit.name().toUpperCase();
            s+="\n";

            if(isBasis){
                for(Number n : basis){
                    s+=n+" ";
                }
                s+="\n";
            }

            for(Equotion eq : conditions){
                for(Number n : eq.getCoeffs()){
                    s+=n+" ";
                }
                s+= "= ";
                s+=eq.getValue();
                s+="\n";
            }
            bw.write(s);
            index++;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeTask(OptimizationTask task, Context context){
        try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(context.openFileOutput("ex"+index+".txt", Context.MODE_PRIVATE)))){
            String s = "";
            for(Number n : task.getTargetV()){
                s+=n+" ";
            }

            s+=task.getLimit().name().toUpperCase();
            s+="\n";

            if(task.getBasis()!=null){
                for(Number n : task.getBasis()){
                    s+=n+" ";
                }
                s+="\n";
            }

            for(Equotion eq : task.getConditions()){
                for(Number n : eq.getCoeffs()){
                    s+=n+" ";
                }
                s+= "= ";
                s+=eq.getValue();
                s+="\n";
            }
            bw.write(s);
            index++;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static OptimizationTask readTask(Context context) {
        OptimizationTask task = null;
        try (Scanner scanner = new Scanner(context.openFileInput("ex"+index+".txt"))) {

            ArrayList<Number> targetF = new ArrayList<>();
            OptimizationTask.Limit limit = OptimizationTask.Limit.MIN;
            Number targetC = new Number(0);
            ArrayList<Equotion> conditions = new ArrayList<Equotion>();

            int n = 0; //размерность
            while (scanner.hasNext(numberPattern)) {
                targetF.add(Number.readNumber(scanner.next()));
                n++;
            }

            Number[] basis = new Number[n];
            Number[] targetV = new Number[n];

            for (int i=0;i<n;i++){
                targetV[i] = targetF.get(i);
            }

            if (scanner.hasNext()) {
                limit = OptimizationTask.Limit.valueOf(scanner.next());
            }

            for (int i = 0; i < n; i++) {
                basis[i] = Number.readNumber(scanner.next());
            }

            if (!scanner.hasNext(numberPattern)){
                Equotion.Sign sign = Equotion.Sign.EQOALLY;
                String s = scanner.next();
                switch (s){
                    case "=": sign = Equotion.Sign.EQOALLY; break;
                    case ">": sign = Equotion.Sign.MORE; break;
                    case "<": sign = Equotion.Sign.LESS; break;
                    case "<=": sign = Equotion.Sign.LESSOREQ; break;
                    case ">=": sign = Equotion.Sign.MOREOREQ; break;
                }

                Number value = Number.readNumber(scanner.next());
                Equotion equotion = new Equotion(basis,sign,value);
                basis = null;

                conditions.add(equotion);
            }

            while (scanner.hasNext(numberPattern)){
                Number[] coeffs = new Number[n];
                for (int i = 0; i < n; i++) {
                    coeffs[i] = Number.readNumber(scanner.next());
                }

                Equotion.Sign sign = Equotion.Sign.EQOALLY;
                String s = scanner.next();
                switch (s){
                    case "=": sign = Equotion.Sign.EQOALLY; break;
                    case ">": sign = Equotion.Sign.MORE; break;
                    case "<": sign = Equotion.Sign.LESS; break;
                    case "<=": sign = Equotion.Sign.LESSOREQ; break;
                    case ">=": sign = Equotion.Sign.MOREOREQ; break;
                }

                Number value = Number.readNumber(scanner.next());
                Equotion equotion = new Equotion(coeffs,sign,value);
                conditions.add(equotion);
            }

            task = new OptimizationTask(conditions, limit, targetC, targetV);
            if (basis!=null){
                task.buildTable(basis);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return task;
    }

    public static OptimizationTask readAssetTask(InputStream stream) {
        OptimizationTask task = null;
        try (Scanner scanner = new Scanner(stream)) {

            ArrayList<Number> targetF = new ArrayList<>();
            OptimizationTask.Limit limit = OptimizationTask.Limit.MIN;
            Number targetC = new Number(0);
            ArrayList<Equotion> conditions = new ArrayList<Equotion>();

            int n = 0; //размерность
            while (scanner.hasNext(numberPattern)) {
                targetF.add(Number.readNumber(scanner.next()));
                n++;
            }

            Number[] basis = new Number[n];
            Number[] targetV = new Number[n];

            for (int i=0;i<n;i++){
                targetV[i] = targetF.get(i);
            }

            if (scanner.hasNext()) {
                limit = OptimizationTask.Limit.valueOf(scanner.next());
            }

            for (int i = 0; i < n; i++) {
                basis[i] = Number.readNumber(scanner.next());
            }

            if (!scanner.hasNext(numberPattern)){
                Equotion.Sign sign = Equotion.Sign.EQOALLY;
                String s = scanner.next();
                switch (s){
                    case "=": sign = Equotion.Sign.EQOALLY; break;
                    case ">": sign = Equotion.Sign.MORE; break;
                    case "<": sign = Equotion.Sign.LESS; break;
                    case "<=": sign = Equotion.Sign.LESSOREQ; break;
                    case ">=": sign = Equotion.Sign.MOREOREQ; break;
                }

                Number value = Number.readNumber(scanner.next());
                Equotion equotion = new Equotion(basis,sign,value);
                basis = null;

                conditions.add(equotion);
            }

            while (scanner.hasNext(numberPattern)){
                Number[] coeffs = new Number[n];
                for (int i = 0; i < n; i++) {
                    coeffs[i] = Number.readNumber(scanner.next());
                }

                Equotion.Sign sign = Equotion.Sign.EQOALLY;
                String s = scanner.next();
                switch (s){
                    case "=": sign = Equotion.Sign.EQOALLY; break;
                    case ">": sign = Equotion.Sign.MORE; break;
                    case "<": sign = Equotion.Sign.LESS; break;
                    case "<=": sign = Equotion.Sign.LESSOREQ; break;
                    case ">=": sign = Equotion.Sign.MOREOREQ; break;
                }

                Number value = Number.readNumber(scanner.next());
                Equotion equotion = new Equotion(coeffs,sign,value);
                conditions.add(equotion);
            }

            task = new OptimizationTask(conditions, limit, targetC, targetV);
            if (basis!=null){
                task.buildTable(basis);
            }

        }
        return task;
    }
}
