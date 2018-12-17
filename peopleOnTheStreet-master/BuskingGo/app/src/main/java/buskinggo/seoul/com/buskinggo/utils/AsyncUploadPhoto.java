package buskinggo.seoul.com.buskinggo.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

import buskinggo.seoul.com.buskinggo.configure.AsyncRegisListener;


public class AsyncUploadPhoto extends AsyncTask<String, Void, String> {

    private AsyncRegisListener asyncListener;

    private Context mContext;
    private String url;
    private String ConvertImage;
    private String ImageTag = "image_tag";
    private String ImageName = "image_data";
    private HashMap<String, String> HashMapParams;

    public AsyncUploadPhoto(Context context, HashMap<String, String> extraData, AsyncRegisListener asyncListener) {
        mContext = context;
        this.HashMapParams = extraData;
        this.asyncListener = asyncListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        url = params[0];
        ConvertImage = params[1];

        ImageProcess imageProcessClass = new ImageProcess();
        if(!(ConvertImage.isEmpty())){
            HashMapParams.put(ImageTag, String.valueOf(System.currentTimeMillis()));
            HashMapParams.put(ImageName, ConvertImage);
        }
        String FinalData = imageProcessClass.ImageHttpRequest(url, HashMapParams);
        return FinalData;
    }

    @Override
    protected void onPostExecute(String resultMessage) {
        super.onPostExecute(resultMessage);
        Toast.makeText(mContext, resultMessage, Toast.LENGTH_LONG).show();
        asyncListener.taskComplete();
    }
}
