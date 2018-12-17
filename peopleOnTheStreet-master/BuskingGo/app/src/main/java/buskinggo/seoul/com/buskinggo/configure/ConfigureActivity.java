package buskinggo.seoul.com.buskinggo.configure;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import buskinggo.seoul.com.buskinggo.login.LoginActivity;
import buskinggo.seoul.com.buskinggo.MyApplication;
import buskinggo.seoul.com.buskinggo.R;

public class ConfigureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);

        Button changeNicknameButton = (Button) findViewById(R.id.changeNicknameButton);
        changeNicknameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent NicknameIntent = new Intent(ConfigureActivity.this, ChangeNicknameActivity.class);
                startActivity(NicknameIntent);
            }
        });

        Button changePasswordButton = (Button) findViewById(R.id.changePasswordButton);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent PasswordIntent = new Intent(ConfigureActivity.this, ChangePasswordActivity.class);
                startActivity(PasswordIntent);
            }
        });

        Button logoutButton = (Button) findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogout();
            }
        });

        Button registerBuskerButton = (Button) findViewById(R.id.registerBuskerButton);
        registerBuskerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regisBuskerIntent = new Intent(ConfigureActivity.this, RegisterBuskerActivity.class);
                startActivity(regisBuskerIntent);
            }
        });
    }

    void showLogout() {
        new AlertDialog.Builder(this)
                .setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        MyApplication.userDTO = null;
                        MyApplication.userEmail = "";
                        MyApplication.password = "";
                        finishAffinity();
                        Intent logoutIntent = new Intent(ConfigureActivity.this, LoginActivity.class);
                        startActivity(logoutIntent);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

}
