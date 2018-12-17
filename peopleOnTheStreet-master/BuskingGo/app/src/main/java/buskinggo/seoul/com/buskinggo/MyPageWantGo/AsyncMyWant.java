package buskinggo.seoul.com.buskinggo.MyPageWantGo;

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
import buskinggo.seoul.com.buskinggo.dto.BuskingDTO;

/*
 *  내가 가볼래요한 공연 가져오기
 * */
public class AsyncMyWant extends AsyncTask<Integer, String, String> {
    private AsyncListener asyncListener;

    AsyncMyWant(AsyncListener asyncListener){
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
            link = "http://buskinggo.cafe24.com/" + "WantBuskingInfoLoad.php";

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

            ArrayList<BuskingDTO> buskingDTOS = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("response");

            int count = 0;
            int buskingNo;
            String buskerName, photo, place, buskingDate, buskingTime;
            while (count < jsonArray.length()) {
                JSONObject object = jsonArray.getJSONObject(count);
                buskingNo = object.getInt("buskingNo");
                buskerName = object.getString("buskerName");
                photo = object.getString("photo");
                place = object.getString("place");
                buskingDate = object.getString("buskingDate");
                buskingTime = object.getString("buskingTime");


                BuskingDTO buskingDTO = new BuskingDTO(buskingNo, buskerName, photo, place, buskingDate, buskingTime);
                buskingDTOS.add(buskingDTO);
                count++;
            }
            // ui 작업 리스너 호출
            asyncListener.buskingComplete(buskingDTOS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
