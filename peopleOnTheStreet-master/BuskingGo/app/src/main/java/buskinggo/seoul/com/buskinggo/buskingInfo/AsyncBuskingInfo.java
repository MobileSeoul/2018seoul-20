package buskinggo.seoul.com.buskinggo.buskingInfo;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import buskinggo.seoul.com.buskinggo.utils.AsyncListener;
import buskinggo.seoul.com.buskinggo.dto.BuskingDTO;

/*
 *  버스커 상세정보 가져오기
 * */
public class AsyncBuskingInfo extends AsyncTask<Integer, String, String> {
    private AsyncListener asyncListener;

    AsyncBuskingInfo(AsyncListener asyncListener){
        this.asyncListener = asyncListener;
    }
    @Override
    protected String doInBackground(Integer... params) {
        HttpURLConnection httpURLConnection = null;

        try {
            int userNo = params[0];
            int buskingNo = params[1];
            System.out.println(userNo + " UserNo " + buskingNo + "  buskingNo");

            String data;
            String link;

            data = URLEncoder.encode("userNo", "UTF-8") + "=" + userNo;
            data += "&" + URLEncoder.encode("buskingNo", "UTF-8") + "=" + buskingNo;
            link = "http://buskinggo.cafe24.com/" + "BuskingInfoLoad.php";

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

            BuskingDTO buskingDTO;

            JSONObject jsonObject = new JSONObject(result);

            if(jsonObject.getString("success").equals("false")) return;
            String photo, buskerNo, buskerName, buskingDate, buskingTime, place, latitude, longitude, introduce, wantSum, myWant;
            photo = jsonObject.getString("photo");
            buskerNo = jsonObject.getString("buskerNo");
            buskerName = jsonObject.getString("buskerName");
            buskingDate = jsonObject.getString("buskingDate");
            buskingTime = jsonObject.getString("buskingTime");
            place = jsonObject.getString("place");
            latitude = jsonObject.getString("latitude");
            longitude = jsonObject.getString("longitude");
            introduce = jsonObject.getString("introduce");
            wantSum = jsonObject.getString("wantSum");
            myWant = jsonObject.getString("myWant");
            buskingDTO = new BuskingDTO(photo, Integer.parseInt(buskerNo), buskerName, buskingDate, buskingTime, place, latitude, longitude, introduce, Integer.parseInt(wantSum), Integer.parseInt(myWant));

            // ui 작업 리스너 호출
            asyncListener.buskingComplete(buskingDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
