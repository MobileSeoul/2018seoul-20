package buskinggo.seoul.com.buskinggo;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import buskinggo.seoul.com.buskinggo.buskingInfo.BuskingInfoActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class BuskingListFragment extends Fragment {

    private static final String TAG_JSON = "response";
    private Context context;
    private ListView buskingListView;
    private BuskingListAdapter buskingListAdapter;

    private JSONObject jsonObject;
    private JSONArray jsonArray = new JSONArray();

    public BuskingListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_busking_list, container, false);

        context = container.getContext();
        buskingListView = view.findViewById(R.id.busking_listView);

        Spinner Main_spinner = view.findViewById(R.id.sort_spinner);

        //스피너 어댑터 설정
        ArrayAdapter adapter = ArrayAdapter.createFromResource(context,R.array.sort, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Main_spinner.setAdapter(adapter);

        //스피너 이벤트 발생
        Main_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    try {
                        jsonArray = sortByDate(jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    buskingListAdapter = new BuskingListAdapter(context, jsonArray, R.layout.busking_list_item);
                    buskingListView.setAdapter(buskingListAdapter);
                    setListViewHeightBasedOnChildren(buskingListView);
                    buskingListAdapter.notifyDataSetChanged();

                }

                if(position == 1){
                    try {
                        jsonArray = sortByBuskerName(jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    buskingListAdapter = new BuskingListAdapter(context, jsonArray, R.layout.busking_list_item);
                    buskingListView.setAdapter(buskingListAdapter);
                    setListViewHeightBasedOnChildren(buskingListView);
                    buskingListAdapter.notifyDataSetChanged();

                }

                if(position == 2){
                    try {
                        jsonArray = sortByPlace(jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    buskingListAdapter = new BuskingListAdapter(context, jsonArray, R.layout.busking_list_item);
                    buskingListView.setAdapter(buskingListAdapter);
                    setListViewHeightBasedOnChildren(buskingListView);
                    buskingListAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        buskingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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



        BuskingListFragment.BuskingAsync todayBuskingAsync = new BuskingListFragment.BuskingAsync();
        todayBuskingAsync.execute();

        return view;
    }

    private class BuskingAsync extends AsyncTask<String, Void, String> {
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                jsonObject = new JSONObject(result);
                jsonArray = jsonObject.getJSONArray(TAG_JSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            buskingListAdapter = new BuskingListAdapter(context, jsonArray, R.layout.busking_list_item);
            buskingListView.setAdapter(buskingListAdapter);
            setListViewHeightBasedOnChildren(buskingListView);

        }

        @Override
        protected String doInBackground(String... strings) {

            try{
                URL url = new URL("http://buskinggo.cafe24.com/BuskingList.php");

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

    //버스커 이름으로 정렬
    private static JSONArray sortByBuskerName(JSONArray array) throws JSONException {
        List<JSONObject> jsons = new ArrayList<JSONObject>();
        for (int i = 0; i < array.length(); i++) {
            jsons.add(array.getJSONObject(i));
        }
        Collections.sort(jsons, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                String lid = null;
                try {
                    lid = lhs.getString("BuskerName");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String rid = null;
                try {
                    rid = rhs.getString("BuskerName");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Here you could parse string id to integer and then compare.
                return lid.compareTo(rid);
            }
        });
        return new JSONArray(jsons);
    }

    //버스킹날짜로 정렬
    private static JSONArray sortByDate(JSONArray array) throws JSONException {
        List<JSONObject> jsons = new ArrayList<JSONObject>();
        for (int i = 0; i < array.length(); i++) {
            jsons.add(array.getJSONObject(i));
        }
        Collections.sort(jsons, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                String lid = null;
                try {
                    lid = lhs.getString("BuskingDate");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String rid = null;
                try {
                    rid = rhs.getString("BuskingDate");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Here you could parse string id to integer and then compare.
                return lid.compareTo(rid);
            }
        });
        return new JSONArray(jsons);
    }

    //버스킹장소로 정렬
    private static JSONArray sortByPlace(JSONArray array) throws JSONException {
        List<JSONObject> jsons = new ArrayList<JSONObject>();
        for (int i = 0; i < array.length(); i++) {
            jsons.add(array.getJSONObject(i));
        }
        Collections.sort(jsons, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                String lid = null;
                try {
                    lid = lhs.getString("Place");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String rid = null;
                try {
                    rid = rhs.getString("Place");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Here you could parse string id to integer and then compare.
                return lid.compareTo(rid);
            }
        });
        return new JSONArray(jsons);
    }
}
