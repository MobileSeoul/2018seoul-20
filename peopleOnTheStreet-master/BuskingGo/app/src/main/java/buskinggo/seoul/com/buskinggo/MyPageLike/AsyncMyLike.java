package buskinggo.seoul.com.buskinggo.MyPageLike;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import buskinggo.seoul.com.buskinggo.utils.AsyncListener;
import buskinggo.seoul.com.buskinggo.dto.BuskerDTO;

/*
 *  내가 좋아요한 버스커 가져오기
 * */
public class AsyncMyLike extends AsyncTask<Integer, String, String> {
    private AsyncListener asyncListener;

    AsyncMyLike(AsyncListener asyncListener){
        this.asyncListener = asyncListener;
    }
    @Override
    protected String doInBackground(Integer... params) {
        HttpURLConnection httpURLConnection = null;

        try {
            int userNo = params[0];

            String data;
            String link;

            data = URLEncoder.encode("userNo", "UTF-8") + "=" + userNo;
            link = "http://buskinggo.cafe24.com/" + "LikeBuskerInfoLoad.php";

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
                sb.append(line); // 데이터 받기
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
    protected void onPostExecute(String result) {   // 결과 처리부분
        try {

            ArrayList<BuskerDTO> buskerDTOS = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("response");

            int count = 0;
            int buskerNo;
            String buskerName, photo, genre;
            while (count < jsonArray.length()) {
                JSONObject object = jsonArray.getJSONObject(count);
                buskerNo = object.getInt("buskerNo");
                buskerName = object.getString("buskerName");
                photo = object.getString("photo");
                genre = object.getString("genre");

                BuskerDTO buskerDTO = new BuskerDTO(buskerNo, buskerName, photo, genre);
                buskerDTOS.add(buskerDTO);
                count++;
            }
            // ui 작업 리스너 호출
            asyncListener.buskerComplete(buskerDTOS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
