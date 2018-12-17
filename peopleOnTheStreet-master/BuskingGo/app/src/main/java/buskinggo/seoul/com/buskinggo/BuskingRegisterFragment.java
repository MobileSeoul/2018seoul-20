package buskinggo.seoul.com.buskinggo;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import buskinggo.seoul.com.buskinggo.configure.AsyncRegisListener;
import buskinggo.seoul.com.buskinggo.configure.RegisterBuskerActivity;
import buskinggo.seoul.com.buskinggo.map.ChooseAddrMap;
import buskinggo.seoul.com.buskinggo.utils.AsyncUploadPhoto;
import buskinggo.seoul.com.buskinggo.utils.PictureDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class BuskingRegisterFragment extends Fragment {

    private String buskingDate;
    private String buskingTime;
    private String place = "";
    private String introduce = "";
    InputMethodManager imm;

    private TextView select_place_TextView;
    private Bitmap FixBitmap;
    private ImageView buskingSelectedImage;
    ByteArrayOutputStream byteArrayOutputStream;
    byte[] byteArray;
    String ConvertImage;
    public static final int GALLERY = 1, CAMERA = 2, BUSKING_ADDR = 3;
    private EditText introduceEditText;

    private String latitude, longitude;

    public BuskingRegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_busking_register, container, false);

        Button registerButton = view.findViewById(R.id.busking_register_button);
        final Button dateButton = view.findViewById(R.id.date_button);
        final Button timeButton = view.findViewById(R.id.time_button);
        introduceEditText = view.findViewById(R.id.edit_text_box);
        final ImageButton buskingGetImageButton = view.findViewById(R.id.buskingGetImageButton);
        final Button buskingGetMapButton = view.findViewById(R.id.buskingGetMapButton);

        select_place_TextView = view.findViewById(R.id.select_place_TextView);
        buskingSelectedImage = view.findViewById(R.id.buskingSelectedImage);


        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        RelativeLayout relativeLayout = view.findViewById(R.id.rl_container_busking_register);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(introduceEditText.getWindowToken(), 0);
            }
        });



        //dateButton, timeButton에 초기값을 주기 위해서 현재 날짜, 시간 정보 가져오기
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat CurTimeFormat = new SimpleDateFormat("HH:mm", Locale.KOREA);
        String strCurDate = CurDateFormat.format(date);
        String strCurTime = CurTimeFormat.format(date);

        dateButton.setText(strCurDate);
        timeButton.setText(strCurTime);

        //일자 dialog
        final Calendar cal = Calendar.getInstance();
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(container.getContext(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        String msg = String.valueOf(year) + "-" + addZero(String.valueOf(month + 1)) + "-" + addZero(String.valueOf(date));
                        dateButton.setText(msg);
                    }

                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
                dialog.getDatePicker().setMinDate(new Date().getTime()); //이전 날짜 선택 X
                dialog.show();
            }
        });

        //시간 dialog
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog dialog = new TimePickerDialog(container.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
                        String msg = addZero(String.valueOf(hour)) + ":" + addZero(String.valueOf(min));
                        timeButton.setText(msg);
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
                dialog.show();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buskingDate = String.valueOf(dateButton.getText());
                buskingTime = String.valueOf(timeButton.getText());
                introduce = String.valueOf(introduceEditText.getText());

                if(MyApplication.userDTO.getCheckBusker() == 0) {
                    Toast.makeText(container.getContext(), "버스커 등록을 먼저 해주세요!", Toast.LENGTH_SHORT).show();
                    Intent buskerRegisIntent = new Intent(container.getContext(), RegisterBuskerActivity.class);
                    startActivity(buskerRegisIntent);
                    return;
                }

                if(place.equals("") || introduce.equals("")) {
                    Toast.makeText(container.getContext(), "빈칸 없이 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                uploadToServer(buskingDate, buskingTime, place, introduce);
            }
        });

        buskingGetImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureDialog pictureDialog = new PictureDialog();
                pictureDialog.showPictureDialog(container.getContext());
            }
        });

        buskingGetMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(container.getContext(), ChooseAddrMap.class);
                mapIntent.putExtra("requestCode", BUSKING_ADDR);
                startActivityForResult(mapIntent, BUSKING_ADDR);
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    FixBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                    buskingSelectedImage.setImageBitmap(FixBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            FixBitmap = (Bitmap) data.getExtras().get("data");
            buskingSelectedImage.setImageBitmap(FixBitmap);
        } else if (requestCode == BUSKING_ADDR) {
            String mapAddr = data.getStringExtra("mapAddr");
            latitude = String.valueOf(data.getDoubleExtra("latitude", 0.0));
            longitude = String.valueOf(data.getDoubleExtra("longitude", 0.0));
            place = mapAddr;
            select_place_TextView.setText(mapAddr);
        }
    }

    //초기값과 날짜 or 시간을 선택했을 때 형식을 같게하기 위하여 1자리수(1월,2월...)일 경우 앞에 0(01월, 02월...)을 붙여주는 메소드
    private String addZero(String string) {
        if (string.length() == 1)
            string = "0" + string;
        return string;
    }

    private void uploadToServer(String buskingDate, String buskingTime, String place, String introduce) {

        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            FixBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
            byteArray = byteArrayOutputStream.toByteArray();
            ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch(Exception e) {
            ConvertImage = "";
        }

        String url = "http://buskinggo.cafe24.com/registerBusking.php";

        HashMap<String, String> extraData = new HashMap<>();
        extraData.put("buskingDate", buskingDate);
        extraData.put("buskingTime", buskingTime);
        extraData.put("place", place);
        extraData.put("introduce", introduce);
        extraData.put("buskerNo", MyApplication.buskerNo);
        extraData.put("latitude", latitude);
        extraData.put("longitude", longitude);

        AsyncUploadPhoto AsyncTaskUploadClassOBJ = new AsyncUploadPhoto(getActivity(), extraData, new AsyncRegisListener() {
            @Override
            public void taskComplete() {
                introduceEditText.setText("");
                HomeFragment homeFragment = new HomeFragment();
                ((MainActivity)getActivity()).changeFragment(homeFragment);
                ((MainActivity)getActivity()).bottomNavigationView.setSelectedItemId(R.id.action_home);
            }
        });
        AsyncTaskUploadClassOBJ.execute(url, ConvertImage);
    }
}
