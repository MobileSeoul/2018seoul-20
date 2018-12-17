package buskinggo.seoul.com.buskinggo.configure;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import buskinggo.seoul.com.buskinggo.MyApplication;
import buskinggo.seoul.com.buskinggo.R;

public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        final EditText currentPasswordText = findViewById(R.id.currentPasswordText);
        final EditText changePasswordText = findViewById(R.id.changePasswordText);
        final EditText checkPasswordText = findViewById(R.id.checkPasswordText);

        Button confirmPasswordButton = findViewById(R.id.confirmPasswordButton);
        confirmPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentPasswordText.getText().toString().equals(MyApplication.password)) {
                    Toast.makeText(getApplicationContext(), "현재 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!changePasswordText.getText().toString().equals(checkPasswordText.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "변경할 비밀번호와 확인 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (currentPasswordText.getText().toString().equals(checkPasswordText.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "현재 비밀번호로 변경할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String changedPassword = checkPasswordText.getText().toString();
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                MyApplication.password = changedPassword;
                                Toast.makeText(getApplicationContext(), "비밀번호를 성공적으로 변경하였습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "비밀번호 변경에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                ChangePasswordRequest passwordChangeRequest = new ChangePasswordRequest(MyApplication.userEmail, changedPassword, responseListener);
                RequestQueue queue = Volley.newRequestQueue(ChangePasswordActivity.this);
                queue.add(passwordChangeRequest);
            }
        });
    }
}
