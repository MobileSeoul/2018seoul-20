package buskinggo.seoul.com.buskinggo;


import android.content.Context;
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
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import buskinggo.seoul.com.buskinggo.buskingInfo.BuskingInfoActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    JSONArray jsonArray;
    JSONArray recommendJsonArray;

    private static final String TAG_JSON = "response";

    private Context context;

    private GridView recommendGridView;
    private ListView todayBuskingListView;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        context = container.getContext();
        todayBuskingListView = view.findViewById(R.id.today_busking_listview);
        recommendGridView = view.findViewById(R.id.recommend_busking_gridView);

        TodayBuskingAsync todayBuskingAsync = new TodayBuskingAsync();
        todayBuskingAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        RecommendBuskingAsync recommendBuskingAsync = new RecommendBuskingAsync();
        recommendBuskingAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        todayBuskingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String buskingNo = jsonArray.getJSONObject(position).getString("BuskingNo");
                    Intent intent = new Intent(getActivity(), BuskingInfoActivity.class);
                    intent.putExtra("buskingNo",  Integer.parseInt(buskingNo));
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        recommendGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String buskingNo = recommendJsonArray.getJSONObject(position).getString("BuskingNo");
                    Intent intent = new Intent(getActivity(), BuskingInfoActivity.class);
                    intent.putExtra("buskingNo",  Integer.parseInt(buskingNo));
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class TodayBuskingAsync extends AsyncTask<String, Void, String> {
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            JSONObject jsonObject = new JSONObject();
            jsonArray = new JSONArray();

            try {
                jsonObject = new JSONObject(result);
                jsonArray = jsonObject.getJSONArray(TAG_JSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            BuskingListAdapter buskingListAdapter = new BuskingListAdapter(context, jsonArray, R.layout.busking_list_item);
            todayBuskingListView.setAdapter(buskingListAdapter);
            setListViewHeightBasedOnChildren(todayBuskingListView);


        }

        @Override
        protected String doInBackground(String... strings) {

            try{
                URL url = new URL("http://buskinggo.cafe24.com/TodayBuskingList.php");

                Map<String,Object> params = new LinkedHashMap<>();

                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String,Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postDataBytes);

                Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                StringBuilder sb = new StringBuilder();
                for (int c; (c = in.read()) >= 0;)
                    sb.append((char)c);
                String response = sb.toString().trim();

                in.close();
                conn.disconnect();
                return response;

            }
            catch (IOException e){
                errorString = e.toString();

                return null;
            }
        }
    }

    private class RecommendBuskingAsync extends AsyncTask<String, Void, String> {
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            JSONObject jsonObject = new JSONObject();
            recommendJsonArray = new JSONArray();

            try {
                jsonObject = new JSONObject(result);
                recommendJsonArray = jsonObject.getJSONArray(TAG_JSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RecommendBuskingAdapter recommendAdapter = new RecommendBuskingAdapter(context, recommendJsonArray, R.layout.home_busking_item);
            recommendGridView.setAdapter(recommendAdapter);
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection httpURLConnection = null;

            try{
                URL url = new URL("http://buskinggo.cafe24.com/RecommendBusking.php");

                String data;

                data = URLEncoder.encode("userNo", "UTF-8") + "=" + MyApplication.userDTO.getUserNo();

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
                wr.write(data); // data 보내기
                wr.flush();

                Reader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));

                StringBuilder sb = new StringBuilder();
                for (int c; (c = in.read()) >= 0;)
                    sb.append((char)c);
                String response = sb.toString().trim();

                in.close();
                httpURLConnection.disconnect();
                return response;

            }
            catch (IOException e){
                errorString = e.toString();

                return null;
            }
        }
    }

    //ScrollView안에 listview를 넣기 위해서 item 갯수에 따라 height값을 동적으로 변경
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


}
