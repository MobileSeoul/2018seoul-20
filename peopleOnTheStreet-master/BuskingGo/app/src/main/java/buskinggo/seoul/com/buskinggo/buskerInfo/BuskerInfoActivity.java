package buskinggo.seoul.com.buskinggo.buskerInfo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import buskinggo.seoul.com.buskinggo.MyApplication;
import buskinggo.seoul.com.buskinggo.utils.AsyncListener;
import buskinggo.seoul.com.buskinggo.utils.AsyncPhotoListener;
import buskinggo.seoul.com.buskinggo.dto.BuskerDTO;
import buskinggo.seoul.com.buskinggo.dto.BuskingDTO;
import buskinggo.seoul.com.buskinggo.utils.PhotoResizing;
import buskinggo.seoul.com.buskinggo.R;
import buskinggo.seoul.com.buskinggo.buskingInfo.BuskingInfoActivity;
import buskinggo.seoul.com.buskinggo.utils.AsyncPhoto;

public class BuskerInfoActivity extends AppCompatActivity {
    int userNo; // 현재 유저
    int buskerNo; // 선택한 버스커
    int favorite = 0; // 좋아요 유무

    ArrayList<BuskingDTO> buskingList;
    MyBuskingListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busker_info);
        Intent intent = getIntent();
        userNo = MyApplication.userDTO.getUserNo();
        buskerNo = intent.getIntExtra("buskerNo", 0);

        Toolbar toolbar = findViewById(R.id.busker_info_toolbar);
        setSupportActionBar(toolbar); // 툴바
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_white_24dp);

        final ListView buskingListView = findViewById(R.id.lv_busker_info); // 리스트뷰
        TextView noList = findViewById(R.id.tv_no_list_my_busking);
        buskingListView.setEmptyView(noList);

        buskingList = new ArrayList<>();
        adapter = new MyBuskingListAdapter(getApplicationContext(),R.layout.my_busking_list_item, buskingList);
        buskingListView.setAdapter(adapter);

        // 공연리스트뷰 로드
        new AsyncBuskingListByBusker(new AsyncListener() {


            @Override
            public void buskerComplete(BuskerDTO buskerDTO) {

            }

            @Override
            public void buskerComplete(ArrayList<BuskerDTO> buskerDTOS) {

            }

            @Override
            public void buskingComplete(ArrayList<BuskingDTO> buskingDTOS) {
                if(buskingDTOS != null){
                    buskingList.clear();
                    buskingList.addAll(buskingDTOS);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void buskingComplete(BuskingDTO buskingDTOS) {

            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, buskerNo);


        // DB 저장된 값 로드
        AsyncBuskerInfo asyncBuskerInfo = new AsyncBuskerInfo(new AsyncListener() {
            @Override
            public void buskerComplete(BuskerDTO buskerDTO) {
                TextView tvName = findViewById(R.id.busker_info_name);
                TextView tvJenre = findViewById(R.id.busker_info_jenre_item);
                TextView tvPlace = findViewById(R.id.busker_info_place_item);
                TextView tvIntroduce = findViewById(R.id.busker_info_introduce_item);
                TextView tvFavorite = findViewById(R.id.busker_info_favorite_cnt);
                ImageView ivFavorite = findViewById(R.id.busker_info_favorite_img);

                if(buskerDTO.getMyFavorite() != 0){
                    // 있으면 하트 칠하기
                    ivFavorite.setImageResource(R.drawable.ic_favorite_white_24dp);
                    favorite = 1;
                }
                // 총 좋아요 수 출력
                tvFavorite.setText(String.valueOf(buskerDTO.getFavorite()));
                tvName.setText(buskerDTO.getBuskerName());
                tvJenre.setText(buskerDTO.getGenre());
                tvPlace.setText(buskerDTO.getMainPlace());
                tvIntroduce.setText(buskerDTO.getIntroduce());

                // 프로필이미지 출력
                if(buskerDTO.getPhoto()!=null){
                    bitmapImgDownload(buskerDTO.getPhoto());
                }

            }

            @Override
            public void buskerComplete(ArrayList<BuskerDTO> buskerDTOS) {

            }

            @Override
            public void buskingComplete(ArrayList<BuskingDTO> buskingDTOS) {

            }

            @Override
            public void buskingComplete(BuskingDTO buskingDTOS) {

            }

        });
        asyncBuskerInfo.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userNo, buskerNo);

        final TextView tvFavorite = findViewById(R.id.busker_info_favorite_cnt);
        final ImageView ivFavorite = findViewById(R.id.busker_info_favorite_img);

        // 좋아요 클릭이벤트
        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favorite == 1){
                    ivFavorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    favorite = 0;
                    int cnt = Integer.parseInt(tvFavorite.getText().toString()) - 1;
                    tvFavorite.setText(String.valueOf(cnt));
                    // 좋아요 삭제
                    new AsyncUpdateLike().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userNo, buskerNo, 0);

                }else if (favorite == 0){
                    ivFavorite.setImageResource(R.drawable.ic_favorite_white_24dp);
                    favorite = 1;
                    int cnt = Integer.parseInt(tvFavorite.getText().toString()) + 1;
                    tvFavorite.setText(String.valueOf(cnt));
                    // 좋아요 추가
                    new AsyncUpdateLike().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userNo, buskerNo, 1);
                }

            }
        });

        // 리스트뷰 클릭이벤트
        buskingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BuskerInfoActivity.this, BuskingInfoActivity.class);
                intent.putExtra("userNo", userNo);
                intent.putExtra("buskingNo", buskingList.get(position).getBuskingNo());
                startActivity(intent);
            }
        });
    }

    void bitmapImgDownload(String photo) {
        // 이미지 다운로드
        AsyncPhoto asyncPhoto = new AsyncPhoto(new AsyncPhotoListener() {
            @Override
            public void taskComplete(File file) {
                if(file != null){
                    ImageView ivPhoto = findViewById(R.id.busker_info_photo);
                    Bitmap bitmap = new PhotoResizing().loadPictureWithResize(file, 160);
                    ivPhoto.setImageBitmap(bitmap);
                }
            }
        });
      asyncPhoto.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, photo, "buskerPhoto");

    }
}
