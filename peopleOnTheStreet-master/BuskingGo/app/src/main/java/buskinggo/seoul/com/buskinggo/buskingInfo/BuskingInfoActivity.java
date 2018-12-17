package buskinggo.seoul.com.buskinggo.buskingInfo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

import buskinggo.seoul.com.buskinggo.MyApplication;
import buskinggo.seoul.com.buskinggo.R;
import buskinggo.seoul.com.buskinggo.buskerInfo.BuskerInfoActivity;
import buskinggo.seoul.com.buskinggo.dto.BuskerDTO;
import buskinggo.seoul.com.buskinggo.dto.BuskingDTO;
import buskinggo.seoul.com.buskinggo.dto.ReplyDTO;
import buskinggo.seoul.com.buskinggo.map.ChooseAddrMap;
import buskinggo.seoul.com.buskinggo.utils.AsyncListener;
import buskinggo.seoul.com.buskinggo.utils.AsyncPhoto;
import buskinggo.seoul.com.buskinggo.utils.AsyncPhotoListener;
import buskinggo.seoul.com.buskinggo.utils.PhotoResizing;

public class BuskingInfoActivity extends AppCompatActivity {
    public static final int BUSKING_DETAIL = 4;

    int userNo; // 현재 유저
    int buskingNo; // 선택한 버스킹
    int buskerNo; // 선택한 버스커
    int want = 0; // 가볼래요 유무

    double latitude;
    double longitude;

    int replyNo = 100; // 댓글
    int reReplyNo = 0; // 대댓글

    ReplyListAdapter adapter; // 댓글
    LinkedList<ReplyDTO> replyList; // 댓글 리스트
    ListView replyListview;

    InputMethodManager imm;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busking_info);
        Intent intent = getIntent();
        userNo = MyApplication.userDTO.getUserNo();
        buskingNo = intent.getIntExtra("buskingNo", 0);

        Toolbar toolbar = findViewById(R.id.busking_info_toolbar);
        setSupportActionBar(toolbar); // 툴바
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_white_24dp);

        replyListview = findViewById(R.id.lv_reply); // 리스트뷰
        TextView noList = findViewById(R.id.tv_no_list_reply);
        replyListview.setEmptyView(noList);

        replyList = new LinkedList<>();
        adapter = new ReplyListAdapter(getApplicationContext(),R.layout.busking_lnfo_reply_item, replyList);
        replyListview.setAdapter(adapter);
        setListViewHeight(replyListview);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        loadReply();

        // DB 저장된 값 로드
        AsyncBuskingInfo asyncBuskingInfo = new AsyncBuskingInfo(new AsyncListener() {
            @Override
            public void buskerComplete(BuskerDTO buskerDTO) {}
            @Override
            public void buskerComplete(ArrayList<BuskerDTO> buskerDTOS) {}
            @Override
            public void buskingComplete(ArrayList<BuskingDTO> buskingDTOS) {}

            @Override
            public void buskingComplete(BuskingDTO buskingDTO) {

                TextView tvName = findViewById(R.id.busking_info_name);
                SpannableString nameUnderline = new SpannableString(buskingDTO.getBuskerName());
                nameUnderline.setSpan(new UnderlineSpan(), 0, nameUnderline.length(), 0);
                TextView tvDate = findViewById(R.id.busking_info_date_Item);
                TextView tvTime = findViewById(R.id.busking_info_time_item);
                TextView tvPlace = findViewById(R.id.busking_info_place_item);
                SpannableString placeUnderline = new SpannableString(buskingDTO.getPlace());
                placeUnderline.setSpan(new UnderlineSpan(), 0, placeUnderline.length(), 0);
                TextView tvIntroduce = findViewById(R.id.busking_info_introduce_item);
                TextView tvWantSum= findViewById(R.id.busking_info_want_cnt);
                ImageView ivWant = findViewById(R.id.busking_info_want_img);

                latitude = buskingDTO.getLatitude();
                longitude = buskingDTO.getLongitude();

                buskerNo = buskingDTO.getBuskerNo();
                if(buskingDTO.getMyWant() != 0){
                    // 있으면 별 칠하기
                    ivWant.setImageResource(R.drawable.ic_star_white_24dp);
                    want = 1;
                }
                String time = buskingDTO.getBuskingTime();
                time = time.substring(0,5);

                tvName.setText(nameUnderline);
                tvDate.setText(buskingDTO.getBuskingDate());
                tvTime.setText(time);
                tvPlace.setText(placeUnderline);
                tvIntroduce.setText(buskingDTO.getIntroduce());
                tvWantSum.setText(String.valueOf(buskingDTO.getWantSum()));

                // 프로필이미지 출력
                if(buskingDTO.getPhoto()!=null){
                    bitmapImgDownload(buskingDTO.getPhoto());
                }

            }

        });
        asyncBuskingInfo.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userNo, buskingNo);

        final TextView tvWantSum= findViewById(R.id.busking_info_want_cnt);
        final ImageView ivWant = findViewById(R.id.busking_info_want_img);

        // 이름 클릭시 버스커 상세보기
        TextView tvName = findViewById(R.id.busking_info_name);
        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BuskingInfoActivity.this, BuskerInfoActivity.class);
                intent.putExtra("buskerNo", buskerNo);
                startActivity(intent);
            }
        });

        // 장소 클릭시 상세보기
        TextView tvPlace = findViewById(R.id.busking_info_place_item);
        tvPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(BuskingInfoActivity.this, ChooseAddrMap.class);
                mapIntent.putExtra("requestCode", BUSKING_DETAIL);
                mapIntent.putExtra("latitude", latitude);
                mapIntent.putExtra("longitude", longitude);
                startActivityForResult(mapIntent, BUSKING_DETAIL);
            }
        });

        // 가볼래요 클릭이벤트
        ivWant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(want == 1){
                    ivWant.setImageResource(R.drawable.ic_star_border_white_24dp);
                    want = 0;
                    int cnt = Integer.parseInt(tvWantSum.getText().toString()) - 1;
                    tvWantSum.setText(String.valueOf(cnt));
                    // 좋아요 삭제
                    new AsyncUpdateWant().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userNo, buskingNo, 0);

                }else if (want == 0){
                    ivWant.setImageResource(R.drawable.ic_star_white_24dp);
                    want = 1;
                    int cnt = Integer.parseInt(tvWantSum.getText().toString()) + 1;
                    tvWantSum.setText(String.valueOf(cnt));
                    // 좋아요 추가
                    new AsyncUpdateWant().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, userNo, buskingNo, 1);
                }

            }
        });

        // 댓글 달기
        Button replyBtn = findViewById(R.id.reply_comment_btn);
        replyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText = findViewById(R.id.reply_comment_edit);
                String contents = editText.getText().toString();

                if(replyList.size() != 0){
                    replyNo = replyList.getFirst().getReplyNo();
                    replyNo += 100;
                }

                // db 저장
                AsyncInsertReply asyncInsertReply = new AsyncInsertReply(new AsyncInsertReply.AsyncReplyListener() {
                    @Override
                    public void taskComplete() {
                        // 리스트 갱신
                        loadReply();
                    }
                });
                asyncInsertReply.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(userNo), String.valueOf(buskingNo), contents, String.valueOf(replyNo), String.valueOf(reReplyNo));

                // 키보드 내리기
                hideKeyboard();
                editText.setText("");

            }
        });

        // 전체화면 클릭시 키보드 내리기
        RelativeLayout rl = findViewById(R.id.rl_container_reply);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });

    }

    void hideKeyboard(){
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

    }

    void bitmapImgDownload(String photo) {
        // 이미지 다운로드
        AsyncPhoto asyncPhoto = new AsyncPhoto(new AsyncPhotoListener() {
            @Override
            public void taskComplete(File file) {
                if(file != null){
                    ImageView ivPhoto = findViewById(R.id.busking_info_photo);
                    Bitmap bitmap = new PhotoResizing().loadPictureWithResize(file, 160);
                    ivPhoto.setImageBitmap(bitmap);
                }
            }
        });
        asyncPhoto.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, photo, "buskingPhoto");

    }

    void setListViewHeight(ListView listView){
        ListAdapter listAdapter = listView.getAdapter();
        if(listAdapter == null){
            return;
        }

        int totalHeight = 0;
        for(int i = 0; i < listAdapter.getCount(); i++){
            View listitem = listAdapter.getView(i, null, listView);
            listitem.measure(0,0);
            totalHeight += listitem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() -1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    void loadReply(){
        // 댓글 로드
        new AsyncReplyList(new AsyncReplyList.ReplyListener() {

            @Override
            public void taskComplete(LinkedList<ReplyDTO> list) {
                if (list != null) {
                    replyList.clear();
                    replyList.addAll(list);
                    setListViewHeight(replyListview);
                    adapter.notifyDataSetChanged();
                }
            }

        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, buskingNo);
    }
}
