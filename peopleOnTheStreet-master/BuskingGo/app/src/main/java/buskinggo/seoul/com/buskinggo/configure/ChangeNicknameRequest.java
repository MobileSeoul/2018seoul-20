package buskinggo.seoul.com.buskinggo.configure;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ChangeNicknameRequest extends StringRequest{
    final static private String URL = "http://buskinggo.cafe24.com/ChangeNickname.php";
    private Map<String, String> parameters;

    public ChangeNicknameRequest(String userEmail, String changedNickname, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);  // 해당 파라미터를 POST방식으로 전송
        parameters = new HashMap<>();
        parameters.put("userEmail", userEmail);
        parameters.put("changedNickname", changedNickname);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
