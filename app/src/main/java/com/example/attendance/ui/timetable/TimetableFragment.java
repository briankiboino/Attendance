package com.example.attendance.ui.timetable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.attendance.MyAdapter;
import com.example.attendance.R;
import com.example.attendance.ui.attendance.AttendanceFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimetableFragment extends Fragment {

    public TimetableFragment(){}

    RecyclerView recyclerView;

    private Intent intent;
    private static String URL_FETCH = "http://hgfoundations.000webhostapp.com/progress.php";
    private ProgressDialog loading;

    List<Attendance> attendanceList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_timetable, container, false);
        attendanceList = new ArrayList<>();
        recyclerView  = view.findViewById(R.id.recyclerView);
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
                                    String code = object.getString("unit_code");
                                    String id = object.getString("id");
                                    String lecturer = object.getString("lecturer");
                                    String date = object.getString("date");
                                    //set data to recyclerview
                                    Attendance attendance = new Attendance(code, id, lecturer, date);
                                    attendanceList.add(attendance);

                                }

                                MyAdapter myAdapter = new MyAdapter(getActivity(), attendanceList);
                                recyclerView.setAdapter(myAdapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


                            } else {
                                loading.dismiss();
                                Toast.makeText(getActivity(), "Error occurred!", Toast.LENGTH_SHORT).show();
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
