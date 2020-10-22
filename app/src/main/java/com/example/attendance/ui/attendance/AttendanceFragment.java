package com.example.attendance.ui.attendance;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.attendance.GpsTracker;
import com.example.attendance.HomeActivity;
import com.example.attendance.MainActivity;
import com.example.attendance.R;
import com.example.attendance.RegisterActivity;
import com.example.attendance.SigninActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AttendanceFragment extends Fragment {
    private GpsTracker gpsTracker;
    private EditText student_name, reg_number, student_email, unit_name, lecturer;
    private Button confirm;
    private static String URL_VERIFY = "http://hgfoundations.000webhostapp.com/attendance_verification.php";
    private static String URL_CONFIRM = "http://hgfoundations.000webhostapp.com/attendance_confirmation.php";
    private ProgressDialog loading;
    private Intent intent;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    TextView txtLat;
    String lat;
    String provider;
    protected String latitude, longitude;
    protected boolean gps_enabled, network_enabled;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_attendance, container, false);

        student_name = view.findViewById(R.id.student_name);
        reg_number = view.findViewById(R.id.reg_number);
        student_email = view.findViewById(R.id.student_email);
        unit_name = view.findViewById(R.id.unit_name);
        lecturer = view.findViewById(R.id.lecturer_name);
        confirm = view.findViewById(R.id.button_confirm);
        loading = new ProgressDialog(getActivity());
        try {
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                getLocation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
            confirm.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    getLocation();

                }
            });
            return view;
    }

    public void getLocation() {
        gpsTracker = new GpsTracker(getActivity());
        if (gpsTracker.canGetLocation()) {
            double class_latitude = -1.095118;
            double class_longitude = 37.013858;
            double current_latitude = gpsTracker.getLatitude();
            double current_longitude = gpsTracker.getLongitude();
            float[] dist = new float[1];

            Location.distanceBetween(class_latitude,class_longitude,current_latitude,current_longitude,dist);

            if(dist[0]/1000 > 1){
                AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
                builder.setMessage("You are out of range of the school to be able to attend a class. Please visit the school and attend your class!!!");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                Verify();
            }

        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private void Verify() {

        intent = getActivity().getIntent();
        final String registration_number = intent.getStringExtra("username");
        final String email = this.student_email.getText().toString().trim();
        loading.setMessage("Verifying your email and registration number....");
        loading.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_VERIFY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (success){
                                loading.dismiss();
                                Toast.makeText(getActivity(), "Verification successful. We have sent a verification code to your student email.", Toast.LENGTH_SHORT).show();


                                AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
                                builder.setTitle("Enter verification code sent to your email");
                                LinearLayout linearLayout = new LinearLayout(getActivity());

                                final EditText verification_codeEt = new EditText(getActivity());
                                verification_codeEt.setHint("Verification code");
                                verification_codeEt.setInputType(InputType.TYPE_CLASS_PHONE);
                                verification_codeEt.setMinEms(16);

                                linearLayout.addView(verification_codeEt);
                                linearLayout.setPadding(10,10,10,10);
                                builder.setView(linearLayout);

                                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String code = verification_codeEt.getText().toString().trim();
                                        confirmAttendance(code);
                                    }
                                });

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                builder.create().show();


                            }
                            else{
                                Toast.makeText(getActivity(), "Verification unsuccessful!", Toast.LENGTH_SHORT).show();
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                            loading.dismiss();
                            Toast.makeText(getActivity(),"Verification Error!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(getActivity(), "Verification Error!" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("reg", registration_number);
                params.put("student_email", email);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void confirmAttendance(final String code) {
            intent = getActivity().getIntent();
            final String registration_number = intent.getStringExtra("username");
            final String stud_name = this.student_name.getText().toString().trim();
            final String reg = this.reg_number.getText().toString().trim();
            final String email = this.student_email.getText().toString().trim();
            final String unit = this.unit_name.getText().toString().trim();
            final String lec = this.lecturer.getText().toString().trim();
            loading.setMessage("Confirming your class attendance....");
            loading.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CONFIRM,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");
                                JSONArray jsonArray = jsonObject.getJSONArray("data");

                                if (success){
                                    loading.dismiss();
                                    student_name.setText("");
                                    student_email.setText("");
                                    reg_number.setText("");
                                    unit_name.setText("");
                                    lecturer.setText("");
                                    Toast.makeText(getActivity(), "You have successfully confirmed you class attendance.", Toast.LENGTH_SHORT).show();

                                }
                                else{
                                    for (int i = 0; i < jsonArray.length(); i++){
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        String message = object.getString("message");
                                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }catch (JSONException e){
                                e.printStackTrace();
                                loading.dismiss();
                                Toast.makeText(getActivity(),"Confirmation Error!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loading.dismiss();
                            Toast.makeText(getActivity(), "Confirmation Error!" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("reg", registration_number);
                    params.put("code", code);
                    params.put("student_name", stud_name);
                    params.put("reg_number", reg);
                    params.put("student_email", email);
                    params.put("unit_name", unit);
                    params.put("lecturer", lec);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(stringRequest);
    }
}

