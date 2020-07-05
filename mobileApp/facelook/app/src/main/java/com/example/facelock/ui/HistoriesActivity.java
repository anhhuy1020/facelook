package com.example.facelock.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facelock.Event.EventCallback;
import com.example.facelock.Event.EventManager;
import com.example.facelock.Event.EventType;
import com.example.facelock.Network.CmdConstant;
import com.example.facelock.Network.ErrorConstant;
import com.example.facelock.Network.NetworkController;
import com.example.facelock.Network.Package.Receive.ResHistory;
import com.example.facelock.Network.Package.Receive.ResponsePackage;
import com.example.facelock.R;
import com.example.facelock.data.History;

import java.util.List;
import java.util.Map;

public class HistoriesActivity extends AppCompatActivity {
    RecyclerView rcvHistories;
    TextView msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histories);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        msg = findViewById(R.id.msg);
        rcvHistories = findViewById(R.id.rcv_histories);

        showWaitingCircle();
        NetworkController.getInstance().sendCommon(CmdConstant.HISTORY);

        EventManager.removeEventListener(EventType.SERVER_RESPONSE);
        EventManager.addEventListener(EventType.SERVER_RESPONSE, new EventCallback() {
            @Override
            public void execute(Map e) {
                final ResponsePackage pkg = (ResponsePackage) e.get(ResponsePackage.class);
                switch (pkg.getCmdId()) {
                    case CmdConstant.HISTORY:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideWaitingCircle();
                                handleReceiveHistory(pkg);
                            }
                        });
                }
            }
        });

    }

    private void handleReceiveHistory(ResponsePackage pkg) {
        if (pkg.getError() == ErrorConstant.SUCCESS){
            ResHistory res = new ResHistory(pkg);
            List<History> histories = res.getHistories();
            if (histories.size() <= 0){
                msg.setVisibility(View.VISIBLE);
                rcvHistories.setVisibility(View.GONE);
            } else {

                msg.setVisibility(View.GONE);
                rcvHistories.setVisibility(View.VISIBLE);
                HistoryAdaptor adaptor = new HistoryAdaptor(HistoriesActivity.this, histories);
                rcvHistories.setAdapter(adaptor);
                rcvHistories.setLayoutManager(new LinearLayoutManager(HistoriesActivity.this));
            }
        } else{
            Toast.makeText(HistoriesActivity.this, "Can't get histories!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(HistoriesActivity.this, MainActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }



    public void showWaitingCircle(){
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
    }

    public void hideWaitingCircle(){
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

}
