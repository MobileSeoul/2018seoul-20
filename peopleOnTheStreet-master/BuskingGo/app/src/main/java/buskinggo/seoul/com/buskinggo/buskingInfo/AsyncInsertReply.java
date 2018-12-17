package buskinggo.seoul.com.buskinggo.buskingInfo;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/*
 *  댓글달기
 * */
public class AsyncInsertReply extends AsyncTask<String, String, String> {
    private AsyncReplyListener asyncReplyListener;

    AsyncInsertReply(AsyncReplyListener asyncReplyListener){
        this.asyncReplyListener = asyncReplyListener;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection httpURLConnection = null;

        try {
            String userNo= params[0];
            String buskingNo = params[1];
            String contents = params[2];
            String replyNo = params[3];
            String reReplyNo = params[3];

            String data;
            String link;

            data = URLEncoder.encode("userNo", "UTF-8") + "=" + userNo;
            data += "&" + URLEncoder.encode("buskingNo", "UTF-8") + "=" + buskingNo;
            data += "&" + URLEncoder.encode("contents", "UTF-8") + "=" + contents;
            data += "&" + URLEncoder.encode("replyNo", "UTF-8") + "=" + replyNo;
            data += "&" + URLEncoder.encode("reReplyNo", "UTF-8") + "=" + reReplyNo;

            link = "http://buskinggo.cafe24.com/" + "InsertReplyList.php";

            URL url = new URL(link);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
            wr.write(data); // data 보내기
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader
                    (httpURLConnection.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONObject jsonObject = new JSONObject(s);
            if("true".equals(jsonObject.getString("success"))) {
                asyncReplyListener.taskComplete();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    interface AsyncReplyListener{
        void taskComplete();
    }
}
