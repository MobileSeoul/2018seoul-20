package buskinggo.seoul.com.buskinggo.buskingInfo;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/*
 *  가볼래요한 버스킹 정보 갱신
 *  가볼래요 추가/ 삭제
 * */
public class AsyncUpdateWant extends AsyncTask<Integer, String, String> {

    @Override
    protected String doInBackground(Integer... params) {
        HttpURLConnection httpURLConnection = null;

        try {
            int userNo = params[0];
            int buskingNo = params[1];
            int check = params[2];

            String data;
            String link;

            data = URLEncoder.encode("userNo", "UTF-8") + "=" + userNo;
            data += "&" + URLEncoder.encode("buskingNo", "UTF-8") + "=" + buskingNo;
            data += "&" + URLEncoder.encode("check", "UTF-8") + "=" + check;
            link = "http://buskinggo.cafe24.com/" + "UpdateWant.php";

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
}
