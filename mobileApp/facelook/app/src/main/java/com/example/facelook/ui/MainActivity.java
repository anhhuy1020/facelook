package com.example.facelook.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.facelook.Event.EventCallback;
import com.example.facelook.Event.EventDataType;
import com.example.facelook.Event.EventManager;
import com.example.facelook.Event.EventType;
import com.example.facelook.Network.CmdConstant;
import com.example.facelook.Network.ErrorConstant;
import com.example.facelook.Network.NetworkController;
import com.example.facelook.Network.Package.Receive.ResponsePackage;
import com.example.facelook.Network.Package.Send.ReqTrain;
import com.example.facelook.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Map;

import static com.example.facelook.Network.NetworkController.SERVER_PORT;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLock = findViewById(R.id.btn_lock);
        btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkController.getInstance().sendCommon(CmdConstant.LOCK);
            }
        });

        Button btnUnlock = findViewById(R.id.btn_unlock);
        btnUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkController.getInstance().sendCommon(CmdConstant.UNLOCK);
            }
        });

        Button btnTrain = findViewById(R.id.btn_add_face);
        btnTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Add new face");
                // Set up the input
                final EditText input = new EditText(MainActivity.this);
                input.setHint("Name");
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ReqTrain req = new ReqTrain();
                        req.setName(input.getText().toString());
                        NetworkController.getInstance().send(req);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        Button btnHistory = findViewById(R.id.btn_history);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoriesActivity.class);
                startActivity(intent);
            }
        });
        EventManager.removeEventListener(EventType.SERVER_RESPONSE);
        EventManager.addEventListener(EventType.SERVER_RESPONSE, new EventCallback() {
            @Override
            public void execute(Map e) {
                final ResponsePackage pkg = (ResponsePackage) e.get(ResponsePackage.class);
                switch (pkg.getCmdId()) {
                    case CmdConstant.TRAIN:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                handleReceiveTrain(pkg);
                            }
                        });
                }
            }
        });
    }

    private void handleReceiveTrain(ResponsePackage pkg) {
        String title = "Train";
        String msg = "";
        if (pkg.getError() == ErrorConstant.SUCCESS) {
            title += " success";
            msg = "Your face has been added!";
        } else {
            title += " failed";
        }
        if (pkg.getError() == ErrorConstant.INVALID_NAME) {
            msg = "This name has already been added!";
        }
        if (pkg.getError() == ErrorConstant.FAIL) {
            msg = "Over time!";
        }
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
