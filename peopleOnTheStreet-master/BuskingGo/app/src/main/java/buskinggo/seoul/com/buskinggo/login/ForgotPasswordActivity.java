package buskinggo.seoul.com.buskinggo.login;

import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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
import buskinggo.seoul.com.buskinggo.configure.ChangePasswordActivity;
import buskinggo.seoul.com.buskinggo.configure.ChangePasswordRequest;
import buskinggo.seoul.com.buskinggo.register.EmailValidateRequest;
import buskinggo.seoul.com.buskinggo.register.RegisterActivity;
import buskinggo.seoul.com.buskinggo.utils.GMailSender;
import buskinggo.seoul.com.buskinggo.utils.RandomPassword;

public class ForgotPasswordActivity extends AppCompatActivity {
    private AlertDialog dialog;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        final EditText forgotEmailText = findViewById(R.id.forgotEmailText);
        Button forgotConfirmButton = findViewById(R.id.forgotConfirmButton);
        forgotConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEmail = forgotEmailText.getText().toString().trim();
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                                dialog = builder.setMessage("아이디를 잘못 입력하셨습니다.")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            } else {
                                String randomVal = RandomPassword.randomPassword(10);
                                new backgroundTask().execute(randomVal);  // 메일 발송
                                Response.Listener<String> passwordChangeListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonResponse = new JSONObject(response);
                                            boolean success = jsonResponse.getBoolean("success");
                                            if (success) {
                                                Toast.makeText(getApplicationContext(), "사용자 메일로 임시 비밀번호를 보냈습니다.", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "임시 비밀번호 전송에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                ChangePasswordRequest passwordChangeRequest = new ChangePasswordRequest(userEmail, randomVal, passwordChangeListener);
                                RequestQueue queue = Volley.newRequestQueue(ForgotPasswordActivity.this);
                                queue.add(passwordChangeRequest);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                EmailValidateRequest emailValidateRequest = new EmailValidateRequest(userEmail, responseListener);
                RequestQueue queue = Volley.newRequestQueue(ForgotPasswordActivity.this);
                queue.add(emailValidateRequest);
            }
        });
    }

    class backgroundTask extends AsyncTask<String, String, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                GMailSender sender = new GMailSender("buskinggo@gmail.com", "opbcihidfjsleoll");
                sender.sendMail("BuskingGo 새 비밀번호 발송",
                        userEmail + "님 안녕하세요. 사용자 요청에 따라 아래 새 비밀번호로 변경하였습니다. 아래 비밀번호로 다시 로그인해주세요. \r password : " + params[0],
                        userEmail, userEmail);
            } catch (Exception e) {
                Log.e("SendMail", e.getMessage(), e);
            }
            return null;
        }

    }
}
