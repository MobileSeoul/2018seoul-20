package buskinggo.seoul.com.buskinggo.configure;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import buskinggo.seoul.com.buskinggo.MyApplication;
import buskinggo.seoul.com.buskinggo.R;
import buskinggo.seoul.com.buskinggo.login.LoginActivity;
import buskinggo.seoul.com.buskinggo.login.LoginRequest;
import buskinggo.seoul.com.buskinggo.utils.AsyncPhoto;
import buskinggo.seoul.com.buskinggo.utils.AsyncPhotoListener;
import buskinggo.seoul.com.buskinggo.utils.AsyncUploadPhoto;
import buskinggo.seoul.com.buskinggo.utils.PictureDialog;

public class RegisterBuskerActivity extends AppCompatActivity {

    private ArrayAdapter guAdapter;
    private ArrayAdapter genreAdapter;
    private Spinner guSpinner;
    private Spinner genreSpinner;
    private Button confirmBuskerButton;
    private EditText buskerNameText;
    private EditText buskerIntroductionText;

    // image related
    private ImageButton GetImageFromGalleryButton;
    private ImageView ShowSelectedImage;
    private Bitmap FixBitmap;
    ByteArrayOutputStream byteArrayOutputStream;
    byte[] byteArray;
    String ConvertImage;
    String pastImage;
    private int GALLERY = 1, CAMERA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_busker);

        buskerNameText = findViewById(R.id.buskerNameText);
        buskerIntroductionText = findViewById(R.id.buskerIntroductionText);

        guSpinner = findViewById(R.id.guSpinner);
        guAdapter = ArrayAdapter.createFromResource(this, R.array.gus, R.layout.spinner_item);
        guAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        guSpinner.setAdapter(guAdapter);

        genreSpinner = findViewById(R.id.genreSpinner);
        genreAdapter = ArrayAdapter.createFromResource(this, R.array.genres, R.layout.spinner_item);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(genreAdapter);


        if (MyApplication.userDTO.getCheckBusker() == 1) {
            requestBuskerInfos();
        }

        GetImageFromGalleryButton = findViewById(R.id.getImageFromGalleryButton);
        ShowSelectedImage = findViewById(R.id.showImageFromGalleryButton);
        GetImageFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PictureDialog pictureDialog = new PictureDialog();
                pictureDialog.showPictureDialog(RegisterBuskerActivity.this);
            }
        });

        confirmBuskerButton = findViewById(R.id.confirmBuskerButton);
        confirmBuskerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadToServer();
                finish();
            }
        });

    }

    private void requestBuskerInfos() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        buskerNameText.setText(jsonResponse.getString("BuskerName"));
                        buskerIntroductionText.setText(jsonResponse.getString("Introduce"));
                        setSpinText(guSpinner, jsonResponse.getString("MainPlace"));
                        setSpinText(genreSpinner, jsonResponse.getString("Genre"));

                        // 사진 불러오기
                        AsyncPhoto asyncPhoto = new AsyncPhoto(new AsyncPhotoListener() {
                            @Override
                            public void taskComplete(File file) {
                                if (file != null) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                    ShowSelectedImage.setImageBitmap(bitmap);
                                }
                            }
                        });
                        asyncPhoto.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonResponse.getString("Photo"), "buskerPhoto");
                        pastImage = jsonResponse.getString("Photo");
                    } else {
                        Toast.makeText(RegisterBuskerActivity.this, "버스커 정보를 불러오는데 실패하였습니다", Toast.LENGTH_LONG)
                                .show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        BuskerInfoRequest buskerInfoRequest = new BuskerInfoRequest(String.valueOf(MyApplication.userDTO.getUserNo()), responseListener);
        RequestQueue queue = Volley.newRequestQueue(RegisterBuskerActivity.this);
        queue.add(buskerInfoRequest);
    }

    private void setSpinText(Spinner spin, String text) {
        for (int i = 0; i < spin.getAdapter().getCount(); i++) {
            if (spin.getAdapter().getItem(i).toString().equals(text)) {
                spin.setSelection(i);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    FixBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    ShowSelectedImage.setImageBitmap(FixBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterBuskerActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            FixBitmap = (Bitmap) data.getExtras().get("data");
            ShowSelectedImage.setImageBitmap(FixBitmap);
        }
    }

    public void uploadToServer() {
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            FixBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
            byteArray = byteArrayOutputStream.toByteArray();
            ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            ConvertImage = "";
        }

        HashMap<String, String> extraData = new HashMap<>();
        String url = null;
        if (MyApplication.userDTO.getCheckBusker() == 1) {
            url = "http://buskinggo.cafe24.com/updateBusker.php";
            extraData.put("pastImage", pastImage);
        } else {
            url = "http://buskinggo.cafe24.com/registerBusker.php";
        }

        extraData.put("buskerName", buskerNameText.getText().toString());
        extraData.put("buskerGu", guSpinner.getSelectedItem().toString());
        extraData.put("buskerGenre", genreSpinner.getSelectedItem().toString());
        extraData.put("buskerIntroduction", buskerIntroductionText.getText().toString());
        extraData.put("userNo", String.valueOf(MyApplication.userDTO.getUserNo()));

        AsyncUploadPhoto AsyncTaskUploadClassOBJ = new AsyncUploadPhoto(RegisterBuskerActivity.this, extraData, new AsyncRegisListener() {
            @Override
            public void taskComplete() {
                MyApplication.userDTO.setCheckBusker(1);
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String buskerNo = jsonResponse.getString("BuskerNo");
                            MyApplication.buskerNo = buskerNo;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                BuskingNoRequest buskerNoRequest = new BuskingNoRequest(String.valueOf(MyApplication.userDTO.getUserNo()), responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterBuskerActivity.this);
                queue.add(buskerNoRequest);
            }
        });
        AsyncTaskUploadClassOBJ.execute(url, ConvertImage);
    }
}
