package buskinggo.seoul.com.buskinggo.buskingInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.LinkedList;

import buskinggo.seoul.com.buskinggo.R;
import buskinggo.seoul.com.buskinggo.dto.ReplyDTO;

public class ReplyListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private LinkedList<ReplyDTO> replyList;
    private int mLayout;



    ReplyListAdapter(Context context, int layout, LinkedList<ReplyDTO> replyList) {
        this.mContext = context;
        this.mLayout = layout;
        this.replyList = replyList;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return replyList.size();
    }

    @Override
    public Object getItem(int position) {
        return replyList.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ReplyViewHolder viewHolder;

        if(convertView == null){
            convertView = mInflater.inflate(mLayout, viewGroup, false);

            viewHolder = new ReplyViewHolder();
            viewHolder.userNickName = convertView.findViewById(R.id.reply_user_nick_name);
            viewHolder.currentTime = convertView.findViewById(R.id.reply_currentTime);
            viewHolder.comment = convertView.findViewById(R.id.reply_comment);


            convertView.setTag(viewHolder);

        }else{

            viewHolder = (ReplyViewHolder) convertView.getTag();
        }

        String name = replyList.get(position).getUserNickName();
        String currentTime = replyList.get(position).getCurrentTime();
        String comment = replyList.get(position).getComment();
        currentTime = currentTime.substring(0,16);

        if(currentTime.equals("null") || name.equals("null") || comment.equals("null")){
            return convertView;

        }else{
            viewHolder.userNickName.setText(name);
            viewHolder.currentTime.setText(currentTime);
            viewHolder.comment.setText(comment);
        }

        return convertView;
    }
}

