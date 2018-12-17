package buskinggo.seoul.com.buskinggo.register;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest{

    final static private String URL = "http://buskinggo.cafe24.com/UserRegister.php";
    private Map<String, String> parameters;

    public RegisterRequest(String userEmail, String userPassword, String userNickname, String userGu, String userGenre, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);  // 해당 파라미터를 POST방식으로 전송
        parameters = new HashMap<>();
        parameters.put("userEmail", userEmail);
        parameters.put("userPassword", userPassword);
        parameters.put("userNickname", userNickname);
        parameters.put("userGu", userGu);
        parameters.put("userGenre", userGenre);
    }


    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
