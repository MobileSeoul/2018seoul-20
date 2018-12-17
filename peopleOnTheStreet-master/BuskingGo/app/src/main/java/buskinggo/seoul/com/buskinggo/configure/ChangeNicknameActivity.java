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

public class ChangeNicknameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nickname);

        final EditText changeNicknameText = findViewById(R.id.changeNicknameText);
        changeNicknameText.setText(MyApplication.userDTO.getNickName());

        Button confirmNicknameButton = findViewById(R.id.confirmNicknameButton);
        confirmNicknameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String changedNickname = changeNicknameText.getText().toString();
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                MyApplication.userDTO.setNickName(changedNickname);
                                Toast.makeText(getApplicationContext(), "닉네임을 성공적으로 변경하였습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "정보를 가져오지 못했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                ChangeNicknameRequest nicknameChangeRequest = new ChangeNicknameRequest(MyApplication.userEmail, changedNickname, responseListener);
                RequestQueue queue = Volley.newRequestQueue(ChangeNicknameActivity.this);
                queue.add(nicknameChangeRequest);
            }
        });

    }
}
