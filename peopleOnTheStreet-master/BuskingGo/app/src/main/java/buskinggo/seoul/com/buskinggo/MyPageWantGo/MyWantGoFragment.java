package buskinggo.seoul.com.buskinggo.MyPageWantGo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import buskinggo.seoul.com.buskinggo.MyApplication;
import buskinggo.seoul.com.buskinggo.utils.AsyncListener;
import buskinggo.seoul.com.buskinggo.dto.BuskerDTO;
import buskinggo.seoul.com.buskinggo.dto.BuskingDTO;
import buskinggo.seoul.com.buskinggo.R;
import buskinggo.seoul.com.buskinggo.dto.UserDTO;
import buskinggo.seoul.com.buskinggo.buskingInfo.BuskingInfoActivity;

public class MyWantGoFragment extends Fragment {
    ArrayList<BuskingDTO> buskingList;
    GridView buskingListView;
    MyWantBuskingAdapter adapter;
    UserDTO userDTO;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_my_want_go,null);
        userDTO = MyApplication.userDTO;

        buskingListView = view.findViewById(R.id.lv_my_want);
        TextView noList = view.findViewById(R.id.tv_no_list_my_want);
        buskingListView.setEmptyView(noList);

        buskingList = new ArrayList<>();
        adapter = new MyWantBuskingAdapter(Objects.requireNonNull(getActivity()).getApplicationContext(),R.layout.my_want_busking_list_item, buskingList);
        buskingListView.setAdapter(adapter);

        // 공연리스트뷰 로드
        new AsyncMyWant(new AsyncListener() {

            @Override
            public void buskerComplete(BuskerDTO buskerDTO) {}
            @Override
            public void buskerComplete(ArrayList<BuskerDTO> buskerDTO) {}
            @Override
            public void buskingComplete(ArrayList<BuskingDTO> buskingDTOS) {
                if(buskingDTOS != null){
                    buskingList.clear();
                    buskingList.addAll(buskingDTOS);
                    adapter.notifyDataSetChanged();
                }

            }
            @Override
            public void buskingComplete(BuskingDTO buskingDTOS) {}


        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userDTO.getUserNo());


        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        buskingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), BuskingInfoActivity.class);
                intent.putExtra("buskingNo", buskingList.get(position).getBuskingNo());
                startActivity(intent);
            }
        });
    }
}
