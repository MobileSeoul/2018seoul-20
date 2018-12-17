package buskinggo.seoul.com.buskinggo.buskerInfo;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import buskinggo.seoul.com.buskinggo.utils.AsyncListener;
import buskinggo.seoul.com.buskinggo.dto.BuskerDTO;

/*
*  버스커 상세정보 가져오기
* */
public class AsyncBuskerInfo extends AsyncTask<Integer, String, String> {
    private AsyncListener asyncListener;

    AsyncBuskerInfo(AsyncListener asyncListener){
        this.asyncListener = asyncListener;
    }
    @Override
    protected String doInBackground(Integer... params) {
        HttpURLConnection httpURLConnection = null;

        try {
            int userNo = params[0];
            int buskerNo = params[1];

            String data;
            String link;

            data = URLEncoder.encode("userNo", "UTF-8") + "=" + userNo;
            data += "&" + URLEncoder.encode("buskerNo", "UTF-8") + "=" + buskerNo;
            link = "http://buskinggo.cafe24.com/" + "BuskerInfoLoad.php";

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

            BuskerDTO buskerDTO;

            JSONObject jsonObject = new JSONObject(result);

            if(jsonObject.getString("success").equals("false")) return;
            String buskerName, photo, mainPlace, genre, introduce;
            buskerName = jsonObject.getString("buskerName");
            photo = jsonObject.getString("photo");
            mainPlace = jsonObject.getString("mainPlace");
            genre = jsonObject.getString("genre");
            introduce = jsonObject.getString("introduce");
            String likeSum = jsonObject.getString("likeSum");
            String myLike = jsonObject.getString("myLike");
            buskerDTO = new BuskerDTO(buskerName, photo, mainPlace, genre, introduce, Integer.parseInt(likeSum), Integer.parseInt(myLike));


            // ui 작업 리스너 호출
            asyncListener.buskerComplete(buskerDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
