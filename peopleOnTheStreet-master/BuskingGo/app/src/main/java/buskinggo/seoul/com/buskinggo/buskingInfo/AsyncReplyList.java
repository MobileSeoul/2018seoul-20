package buskinggo.seoul.com.buskinggo.buskingInfo;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;

import buskinggo.seoul.com.buskinggo.dto.ReplyDTO;

/*
 *  댓글 가져오기
 * */
public class AsyncReplyList extends AsyncTask<Integer, String, String> {
    private ReplyListener replyListener;

    AsyncReplyList(ReplyListener replyListener){
        this.replyListener = replyListener;
    }
    @Override
    protected String doInBackground(Integer... params) {
        HttpURLConnection httpURLConnection = null;

        try {
            int buskingNo = params[0];

            String data;
            String link;

            data = URLEncoder.encode("buskingNo", "UTF-8") + "=" + buskingNo;
            link = "http://buskinggo.cafe24.com/" + "ReplyListLoad.php";

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
    protected void onPostExecute(String result) {
        try {

            LinkedList<ReplyDTO> replyList = new LinkedList<>();

            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("response");

            int count = 0;
            String name, currentTime, comment, replyNo, reReplyNo;
            int commentNo;
            while (count < jsonArray.length()) {
                JSONObject object = jsonArray.getJSONObject(count);
                commentNo = object.getInt("commentNo");
                name = object.getString("nickname");
                currentTime = object.getString("currentTime");
                comment = object.getString("contents");
                replyNo = object.getString("replyNo");
                reReplyNo = object.getString("reReplyNo");

                ReplyDTO replyDTO;

                replyDTO = new ReplyDTO(commentNo, name, currentTime, comment, Integer.parseInt(replyNo), Integer.parseInt(reReplyNo));
                replyList.add(replyDTO);

                count++;
            }

            replyListener.taskComplete(replyList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface ReplyListener{
        void taskComplete(LinkedList<ReplyDTO> list);
    }
}
