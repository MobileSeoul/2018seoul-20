package buskinggo.seoul.com.buskinggo.buskerInfo;

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
 *  버스커의 공연정보 가져오기
 * */
public class AsyncBuskingListByBusker extends AsyncTask<Integer, String, String> {
    private AsyncListener asyncListener;

    AsyncBuskingListByBusker(AsyncListener asyncListener){
        this.asyncListener = asyncListener;
    }
    @Override
    protected String doInBackground(Integer... params) {
        HttpURLConnection httpURLConnection = null;

        try {
            int buskerNo = params[0];

            String data;
            String link;

            data = URLEncoder.encode("buskerNo", "UTF-8") + "=" + buskerNo;
            link = "http://buskinggo.cafe24.com/" + "BuskingListByBusker.php";

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

            ArrayList<BuskingDTO> buskingList = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("response");

            int count = 0;
            String date, place, time;
            int buskingNo;
            while (count < jsonArray.length()) {
                JSONObject object = jsonArray.getJSONObject(count);
                buskingNo = object.getInt("buskingNo");
                date = object.getString("date");
                place = object.getString("place");
                time = object.getString("time");

                BuskingDTO buskingDTO = new BuskingDTO(buskingNo, date, time, place);
                buskingList.add(buskingDTO);
                count++;
            }

            asyncListener.buskingComplete(buskingList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
