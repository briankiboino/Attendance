package com.example.attendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.attendance.ui.timetable.Attendance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class nav_progress extends Fragment {

    public nav_progress() {}
    RecyclerView recyclerView;

    private Intent intent;
    private static String URL_FETCH = "http://hgfoundations.000webhostapp.com/view_timetable.php";
    private ProgressDialog loading;

    List<Timetable> timetableList;

    public static nav_progress newInstance(String param1, String param2) {
        nav_progress fragment = new nav_progress();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_nav_progress, container, false);
        timetableList = new ArrayList<>();
        recyclerView  = view.findViewById(R.id.recyclerView2);
        recyclerView.setHasFixedSize(true);
        intent = getActivity().getIntent();
        final String reg_number = intent.getStringExtra("username");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_FETCH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            if (success) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String code = object.getString("code");
                                    String name = object.getString("unit");
                                    String lecturer = object.getString("lecturer");
                                    String venue = object.getString("venue");
                                    String time = object.getString("time");
                                    //set data to recyclerview
                                    Timetable timetable = new Timetable(name, code, lecturer, venue, time);
                                    timetableList.add(timetable);
                                }

                                MyAdapter2 myAdapter = new MyAdapter2(getActivity(), timetableList);
                                recyclerView.setAdapter(myAdapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


                            } else {
                                loading.dismiss();
                                for (int i = 0; i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String message = object.getString("message");
                                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            loading.dismiss();
                            Toast.makeText(getActivity(), "Error occurred ", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(getActivity(), "An error occurred. Check your internet connection. " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("reg", reg_number);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

        return view;

    }
}
