package com.example.attendance.ui.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.attendance.ForgotPassword;
import com.example.attendance.HomeActivity;
import com.example.attendance.R;
import com.example.attendance.SigninActivity;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private static String URL_FETCH = "http://hgfoundations.000webhostapp.com/profile.php";
    private Intent intent;
    private TextView Name, Phone, Email, College, Course, Reg_number,classesAttended, classesPercentage;
    private ProgressBar loading1;
    private ProgressBar loading2;
    private ProgressBar loading3;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        //init views
        Name = view.findViewById(R.id.name);
        Email = view.findViewById(R.id.email);
        Phone = view.findViewById(R.id.phone);
        College = view.findViewById(R.id.college);
        Course = view.findViewById(R.id.course);
        classesAttended = view.findViewById(R.id.total_classes);
        classesPercentage = view.findViewById(R.id.total_classes_percentage);
        Reg_number = view.findViewById(R.id.reg_number);
        loading1 = view.findViewById(R.id.personal_details);
        loading2 = view.findViewById(R.id.course_details);
        loading3 = view.findViewById(R.id.attendance_details);


        intent = getActivity().getIntent();
        final String reg_number = intent.getStringExtra("username");

        if (!reg_number.isEmpty()){
           loading1.setVisibility(View.VISIBLE);
           loading2.setVisibility(View.VISIBLE);
           loading3.setVisibility(View.VISIBLE);
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_FETCH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            if (success){
                                for (int i = 0; i < jsonArray.length(); i++){
                                    loading1.setVisibility(View.GONE);
                                    loading2.setVisibility(View.GONE);
                                    loading3.setVisibility(View.GONE);

                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String first_name = object.getString("first_name");
                                    String last_name = object.getString("last_name");
                                    String email = object.getString("email");
                                    String phone_number = object.getString("phone_number");
                                    String college = object.getString("college");
                                    String course = object.getString("course");
                                    String registration_number = object.getString("username");
                                    String classes = object.getString("total_classes");
                                    String percent = object.getString("percentage");

                                    //set data to views
                                    Name.setText("Name: "+first_name+" "+last_name);
                                    Email.setText("Email: "+email);
                                    Phone.setText("Phone: "+phone_number);
                                    College.setText("College: "+college);
                                    Course.setText("Course: "+course);
                                    Reg_number.setText("Registration number: "+registration_number);
                                    classesAttended.setText("Classes attended: "+classes);
                                    classesPercentage.setText("Attendance in percentage: "+percent+ "%");

                                }
                            }
                            else {
                                loading1.setVisibility(View.GONE);
                                loading2.setVisibility(View.GONE);
                                loading3.setVisibility(View.GONE);
                                Toast.makeText(getActivity(),"Error occurred!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e){
                            e.printStackTrace();
                            loading1.setVisibility(View.GONE);
                            loading2.setVisibility(View.GONE);
                            loading3.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Error occurred ", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading1.setVisibility(View.GONE);
                        loading2.setVisibility(View.GONE);
                        loading3.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                })
        {
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
