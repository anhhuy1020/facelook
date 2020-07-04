package com.example.facelook.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.example.facelook.R;
import com.example.facelook.network.History;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class HistoriesActivity extends AppCompatActivity {
    RecyclerView rcvHistories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histories);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        Calendar now = Calendar.getInstance();
        Random random = new Random();

        List<History> histories = new ArrayList<>();
        histories.add(new History((Calendar) now.clone(), "Ho Quoc Huy"));
        now.roll(Calendar.SECOND, true);
        histories.add(new History((Calendar) now.clone(), "Duong Chi Viet Hoang"));
        now.roll(Calendar.MINUTE, true);
        histories.add(new History((Calendar) now.clone(), "Bui Xuan Danh"));
        now.roll(Calendar.SECOND, true);
        histories.add(new History((Calendar) now.clone(), "Nguyen Van Danh"));
        now.roll(Calendar.SECOND, true);
        histories.add(new History((Calendar) now.clone(), "Ho Quoc Huy"));
        now.roll(Calendar.HOUR, true);
        histories.add(new History((Calendar) now.clone(), "Nguyen Van Danh"));
        now.roll(Calendar.SECOND, true);
        histories.add(new History((Calendar) now.clone(), "Ho Quoc Huy"));
        now.roll(Calendar.MINUTE, true);
        histories.add(new History((Calendar) now.clone(), "Duong Chi Viet Hoang"));
        now.roll(Calendar.SECOND, true);
        histories.add(new History((Calendar) now.clone(), "Nguyen Van Danh"));
        now.roll(Calendar.HOUR, true);
        histories.add(new History((Calendar) now.clone(), "Ho Quoc Huy"));
        now.roll(Calendar.SECOND, true);

        histories.add(new History(now, "Duong Chi Viet Hoang"));

        rcvHistories = findViewById(R.id.rcv_histories);
        HistoryAdaptor adaptor = new HistoryAdaptor(HistoriesActivity.this, histories);
        rcvHistories.setAdapter(adaptor);
        rcvHistories.setLayoutManager(new LinearLayoutManager(HistoriesActivity.this));
    }
}
