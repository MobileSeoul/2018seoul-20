package buskinggo.seoul.com.buskinggo.MyPagePast;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import buskinggo.seoul.com.buskinggo.utils.AsyncPhotoListener;
import buskinggo.seoul.com.buskinggo.dto.BuskingDTO;
import buskinggo.seoul.com.buskinggo.utils.PhotoResizing;
import buskinggo.seoul.com.buskinggo.R;
import buskinggo.seoul.com.buskinggo.utils.AsyncPhoto;

public class MyPastBuskingAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<BuskingDTO> buskingList;
    private int mLayout;


    MyPastBuskingAdapter(Context context, int layout, ArrayList<BuskingDTO> buskingList) {
        this.mContext = context;
        this.mLayout = layout;
        this.buskingList = buskingList;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return buskingList.size();
    }

    @Override
    public Object getItem(int position) {
        return buskingList.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {
            convertView = mInflater.inflate(mLayout, viewGroup, false);
        }
        TextView tvName = convertView.findViewById(R.id.past_name_item);
        TextView tvPlace = convertView.findViewById(R.id.past_place_item);
        TextView tvDate = convertView.findViewById(R.id.past_date_item);
        TextView tvTime = convertView.findViewById(R.id.past_time_item);
        ImageView ivPhoto = convertView.findViewById(R.id.past_photo_item);

        String name = buskingList.get(position).getBuskerName();
        String date = buskingList.get(position).getBuskingDate();
        String place = buskingList.get(position).getPlace();
        String time = buskingList.get(position).getBuskingTime();
        String photo = buskingList.get(position).getPhoto();
        time = time.substring(0, 5);
        String addr[] = place.split(" "); // 구, 동
        if (addr.length > 2) {
            place = addr[1] + " " + addr[2];
        } else if (addr.length == 2) {
            place = addr[1];
        }


        tvDate.setText(date);
        tvTime.setText(time);
        tvPlace.setText(place);
        tvName.setText(name);
        Bitmap bitmap = bitmapImgDownload(photo);
        if (bitmap != null) {
            ivPhoto.setImageBitmap(bitmap);
        }

        return convertView;
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


