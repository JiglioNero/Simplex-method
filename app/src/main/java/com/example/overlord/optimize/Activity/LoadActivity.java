package com.example.overlord.optimize.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.overlord.optimize.Other.Reader;
import com.example.overlord.optimize.Other.TaskAdapter;
import com.example.overlord.optimize.R;

public class LoadActivity extends AppCompatActivity {
    private RecyclerView listOfTasks;
    private TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        getSupportActionBar().setTitle(R.string.loadTask);
        listOfTasks = findViewById(R.id.list);
        listOfTasks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(this);
        listOfTasks.setAdapter(adapter);
        adapter.setItems(Reader.readAllTasks(this));
    }
}
