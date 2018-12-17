package buskinggo.seoul.com.buskinggo.MyPageLike;

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
import buskinggo.seoul.com.buskinggo.dto.BuskerDTO;
import buskinggo.seoul.com.buskinggo.utils.PhotoResizing;
import buskinggo.seoul.com.buskinggo.R;
import buskinggo.seoul.com.buskinggo.utils.AsyncPhoto;

public class MyLikeBuskerAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<BuskerDTO> buskerList;
    private int mLayout;


    MyLikeBuskerAdapter(Context context, int layout, ArrayList<BuskerDTO> buskerList) {
        this.mContext = context;
        this.mLayout = layout;
        this.buskerList = buskerList;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return buskerList.size();
    }

    @Override
    public Object getItem(int position) {
        return buskerList.get(position);
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

        ImageView ivPhoto = convertView.findViewById(R.id.like_busker_photo_item);
        TextView tvName = convertView.findViewById(R.id.like_busker_name_item);
        TextView tvGenre = convertView.findViewById(R.id.like_busker_genre_item);


        String photo = buskerList.get(position).getPhoto();
        String name = buskerList.get(position).getBuskerName();
        String genre = buskerList.get(position).getGenre();


        tvName.setText(name);
        tvGenre.setText(genre);
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
            File file = asyncBuskerPhoto.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, photo, "buskerPhoto").get();
            if (file != null) {
                return new PhotoResizing().loadPictureWithResize(file, 160);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }


}

