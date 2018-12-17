package buskinggo.seoul.com.buskinggo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.ExecutionException;

import buskinggo.seoul.com.buskinggo.utils.AsyncPhoto;
import buskinggo.seoul.com.buskinggo.utils.AsyncPhotoListener;
import buskinggo.seoul.com.buskinggo.utils.PhotoResizing;

public class RecommendBuskingAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private JSONArray jsonArray;
    private int layout;

    RecommendBuskingAdapter(Context context, JSONArray jsonArray, int layout) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.jsonArray = jsonArray;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return jsonArray.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(layout, viewGroup, false);
        }

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject = jsonArray.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String photo = null;
        String place = null;
        String time = null;
        String name = null;
        String date = null;

        try {
            photo = String.valueOf(jsonObject.get("Photo"));
            place = String.valueOf(jsonObject.get("Place"));
            date = String.valueOf(jsonObject.get("BuskingDate"));
            time = String.valueOf(jsonObject.get("BuskingTime"));
            name = String.valueOf(jsonObject.get("BuskerName"));
            time = time.substring(0, 5);
            String addr[] = place.split(" "); // 구, 동
            if(addr.length > 2){
                place = addr[1] +" "+ addr[2];
            } else if(addr.length == 2){
                place = addr[1];
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView nameTextview = view.findViewById(R.id.busker_name);
        TextView placeTextview = view.findViewById(R.id.textViewBuskingPlace);
        TextView timeTextview = view.findViewById(R.id.textViewBuskingTime);
        TextView dateTextview = view.findViewById(R.id.tv_recommend_date_item);
        ImageView photoImageView = view.findViewById(R.id.like_busker_photo_item);
        Bitmap bitmap = bitmapImgDownload(photo);
        if (bitmap != null) {
            photoImageView.setImageBitmap(bitmap);
        }


        nameTextview.setText(name);
        placeTextview.setText(place);
        timeTextview.setText(time);
        dateTextview.setText(date);

        return view;
    }

    private Bitmap bitmapImgDownload(String photo) {
        if (photo == null) return null;

        // 이미지 다운로드
        AsyncPhoto asyncBuskerPhoto = new AsyncPhoto(new AsyncPhotoListener() {
            @Override
            public void taskComplete(File file) {
            }
        });

        try {
            File file = asyncBuskerPhoto.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, photo, "buskingPhoto").get();
            if (file != null) {
                return new PhotoResizing().loadPictureWithResize(file, 150);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }
}