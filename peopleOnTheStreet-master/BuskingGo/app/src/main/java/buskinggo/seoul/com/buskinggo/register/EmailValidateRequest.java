package buskinggo.seoul.com.buskinggo.register;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class EmailValidateRequest extends StringRequest {

    final static private String URL = "http://buskinggo.cafe24.com/EmailValidate.php";
    private Map<String, String> parameters;

    public EmailValidateRequest(String userEmail, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);  // 해당 파라미터를 POST방식으로 전송
        parameters = new HashMap<>();
        parameters.put("userEmail", userEmail);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
