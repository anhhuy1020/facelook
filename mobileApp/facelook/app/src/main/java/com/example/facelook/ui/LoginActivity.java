package com.example.facelook.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facelook.Event.EventCallback;
import com.example.facelook.Event.EventManager;
import com.example.facelook.Event.EventType;
import com.example.facelook.Network.CmdConstant;
import com.example.facelook.Network.ErrorConstant;
import com.example.facelook.Network.NetworkController;
import com.example.facelook.Network.Package.Receive.ResponsePackage;
import com.example.facelook.Network.ReceiveModule;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import com.example.facelook.R;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Map;

import static com.example.facelook.Network.NetworkController.SERVER_PORT;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText edtUsername;
    TextInputEditText edtPassword;
    AsyncTask<?, ?, ?> runningTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.username);
        edtPassword = findViewById(R.id.password);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeBaseUrl();
            }
        });


        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();
                //validate form
                if (validateLogin(username, password)) {
                    //do login
                    doLogin();
                }
            }
        });

        EventManager.addEventListener(EventType.SERVER_RESPONSE, new EventCallback() {
            @Override
            public void execute(Map e) {
                ResponsePackage pkg = (ResponsePackage) e.get(ResponsePackage.class);
                if(pkg.getCmdId() == CmdConstant.LOGIN){
                    if (pkg.getError() == ErrorConstant.SUCCESS){
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else{
                        Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void changeBaseUrl() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change server address");
        LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
        View view = inflater.inflate(R.layout.change_address_view, null);

        final TextInputEditText serverIP = view.findViewById(R.id.ip);
        final TextInputEditText serverPort = view.findViewById(R.id.port);
        serverIP.setInputType(InputType.TYPE_CLASS_TEXT);
        serverIP.setText(NetworkController.SERVER_IP);
        serverPort.setInputType(InputType.TYPE_CLASS_TEXT);
        serverPort.setText(String.valueOf(SERVER_PORT));

        builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NetworkController.SERVER_IP = serverIP.getText().toString();
                SERVER_PORT = Integer.parseInt(serverPort.getText().toString());
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

    private boolean validateLogin(String username, String password) {
        if (username == null || username.trim().length() == 0) {
            Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password == null || password.trim().length() == 0) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void doLogin() {
        showWaitingCircle();
        if (runningTask != null)
            runningTask.cancel(true);
        runningTask = new LongOperation();
        runningTask.execute();

    }

    public void showWaitingCircle() {
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
    }

    public void hideWaitingCircle() {
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

    private final class LongOperation extends AsyncTask<Object, Object, Boolean> {
        @Override
        protected Boolean doInBackground(Object... params) {
            if (NetworkController.getInstance().connectServer()) {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();
                String msg = username + "|~|" + password;
                try {
                    NetworkController.getInstance().getSendModule().getDataOutputStream().write(msg.getBytes(Charset.forName("UTF-8")));
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            hideWaitingCircle();
        }
    }
}
