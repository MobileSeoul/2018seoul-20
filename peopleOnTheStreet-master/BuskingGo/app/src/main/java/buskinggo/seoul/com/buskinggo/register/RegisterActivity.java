package buskinggo.seoul.com.buskinggo.register;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import buskinggo.seoul.com.buskinggo.R;

public class RegisterActivity extends AppCompatActivity {

    private ArrayAdapter guAdapter;
    private ArrayAdapter genreAdapter;
    private Spinner guSpinner;
    private Spinner genreSpinner;
    private String userEmail;
    private String userPassword;
    private String userNickname;
    private String userGu;
    private String userGenre;
    private AlertDialog dialog;
    private boolean validate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText idText = (EditText) findViewById(R.id.idText);
        final Button validateButton = (Button) findViewById(R.id.validateButton);
        final EditText passwordText = (EditText) findViewById(R.id.passwordText);
        final EditText nicknameText = (EditText) findViewById(R.id.nicknameText);

        guSpinner = (Spinner) findViewById(R.id.guSpinner);
        guAdapter = ArrayAdapter.createFromResource(this, R.array.gus, R.layout.spinner_item);
        guAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        guSpinner.setAdapter(guAdapter);

        genreSpinner = (Spinner) findViewById(R.id.genreSpinner);
        genreAdapter = ArrayAdapter.createFromResource(this, R.array.genres, R.layout.spinner_item);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(genreAdapter);

        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = idText.getText().toString().trim();
                if (validate) {  // validate 진행했다면
                    return;
                } else if (userEmail.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("아이디는 빈 칸일 수 없습니다.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("이메일 형식을 다시 확인해주세요.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                } else {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                    dialog = builder.setMessage("사용할 수 있는 아이디입니다.")
                                            .setPositiveButton("확인", null)
                                            .create();
                                    dialog.show();
                                    idText.setEnabled(false);  // 더이상 못 바꾸게
                                    validate = true;
                                    idText.setBackground(getResources().getDrawable(R.color.colorAccent));
                                } else {  // 중복체크 실패했다면
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                    dialog = builder.setMessage("사용할 수 없는 아이디입니다.")
                                            .setNegativeButton("확인", null)
                                            .create();
                                    dialog.show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    EmailValidateRequest emailValidateRequest = new EmailValidateRequest(userEmail, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                    queue.add(emailValidateRequest);
                }
            }
        });

        Button registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                userEmail = idText.getText().toString();
                userPassword = passwordText.getText().toString();
                userNickname = nicknameText.getText().toString();
                userGu = guSpinner.getSelectedItem().toString();
                userGenre = genreSpinner.getSelectedItem().toString();

                if (!validate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("먼저 이메일 중복 체크를 해주세요.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                if (userEmail.equals("") || userPassword.equals("") || userNickname.equals("") || userGu.equals("") || userGenre.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("빈 칸 없이 입력해주세요")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("회원 등록에 성공했습니다")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                                finish();
                            } else {  // 중복체크 실패했다면
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("회원 등록에 실패했습니다")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                RegisterRequest registerRequest = new RegisterRequest(userEmail, userPassword, userNickname, userGu, userGenre, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }
        });
    }

    @Override
    protected void onStop() {  // 회원등록 끝나고 등록 성공 dialog 닫음
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
