package buskinggo.seoul.com.buskinggo.buskerInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;

import buskinggo.seoul.com.buskinggo.dto.BuskingDTO;
import buskinggo.seoul.com.buskinggo.R;

public class MyBuskingListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<BuskingDTO> buskingList;
    private int mLayout;



    MyBuskingListAdapter(Context context, int layout, ArrayList<BuskingDTO> buskingList) {
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
        MyBuskingViewHolder viewHolder;

        if(convertView == null){
            convertView = mInflater.inflate(mLayout, viewGroup, false);

            viewHolder = new MyBuskingViewHolder();
            viewHolder.date = convertView.findViewById(R.id.tv_date_busking_item);
            viewHolder.place = convertView.findViewById(R.id.tv_place_busking_item);
            viewHolder.time = convertView.findViewById(R.id.tv_time_busking_item);



            convertView.setTag(viewHolder);

        }else{

            viewHolder = (MyBuskingViewHolder) convertView.getTag();
        }

        String date = buskingList.get(position).getBuskingDate();
        String place = buskingList.get(position).getPlace();
        String time = buskingList.get(position).getBuskingTime();
        time = time.substring(0,5);
        String addr[] = place.split(" "); // 구, 동
        if(addr.length > 2){
            place = addr[1] +" "+ addr[2];
        } else if(addr.length == 2){
            place = addr[1];
        }


        if(date.equals("null") || place.equals("null") || time.equals("null")){
            return convertView;
        }else{
            viewHolder.date.setText(date);
            viewHolder.place.setText(place);
            viewHolder.time.setText(time);
        }

        return convertView;
    }
}

