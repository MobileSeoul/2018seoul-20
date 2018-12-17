package buskinggo.seoul.com.buskinggo.utils;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/*
 *  버스커 이미지 가져오기
 * */
public class AsyncPhoto extends AsyncTask<String, String, File> {
    private AsyncPhotoListener asyncListener;


    public AsyncPhoto(AsyncPhotoListener asyncListener){
        this.asyncListener = asyncListener;
    }


    @Override
    protected File doInBackground(String... params) {
        String fileName = params[0]; // db에 저장된 file 이름
        String saveFolder= params[1]; // buskerPhoto 또는 buskingPhoto 명명

        fileName = fileName + ".jpg";


        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), saveFolder); // 폴더 변수
        boolean bool = false;
        if (!dir.exists()) {
            bool = dir.mkdirs(); // 폴더 없으면 폴더 생성
        }

        File file = new File(dir, fileName); // 파일 변수

        if (file.exists()){
            return file; // 이미 있다면 바로 사용.
        }

        HttpURLConnection httpURLConnection = null;
        try {

            String link;
            link = "http://buskinggo.cafe24.com/" + saveFolder +"/"+ fileName;

            URL url = new URL(link);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            int len = httpURLConnection.getContentLength();

            byte[] tmpByte = new byte[len];

            InputStream is = httpURLConnection.getInputStream();
            FileOutputStream fos = new FileOutputStream(file);
            int read;

            while (true) {
                read = is.read(tmpByte);
                if (read <= 0) {
                    break;
                }
                fos.write(tmpByte, 0, read); // 파일 생성
            }

            is.close();
            fos.close();

            return file;
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
    protected void onPostExecute(File file) {
        super.onPostExecute(file);

        asyncListener.taskComplete(file); // 비트맵처리 밑 이미지 띄울 부분.
    }
}
