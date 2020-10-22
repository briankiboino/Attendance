package com.example.attendance.ui.home;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.attendance.HomeActivity;
import com.example.attendance.R;
import com.example.attendance.RegisterActivity;
import com.example.attendance.SigninActivity;
import com.example.attendance.StarterActivity;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private static String URL_FETCH = "http://hgfoundations.000webhostapp.com/profile.php";
    private TextView nameET, classesAttended, weekProgress, monthProgress, semesterProgress, yearProgress;
    private Button generatepdf;
    private Intent intent;
    private ProgressDialog loading;
    private ProgressDialog loading2;
    private CircularProgressBar percentageProgressbar;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        loading = new ProgressDialog(getActivity());
        nameET = view.findViewById(R.id.welcome_name);
        weekProgress = view.findViewById(R.id.weeklyprogress);
        monthProgress = view.findViewById(R.id.monthlyprogress);
        semesterProgress = view.findViewById(R.id.semesterprogress);
        yearProgress = view.findViewById(R.id.yearlyprogress);
        classesAttended = view.findViewById(R.id.textView);
        generatepdf = view.findViewById(R.id.generate_pdf);
        percentageProgressbar = view.findViewById(R.id.circular);
        intent = getActivity().getIntent();
        final String reg_number = intent.getStringExtra("username");
        if (!reg_number.isEmpty()) {
            loading.setMessage("Fetching your details...");
            loading.show();
        }
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
                                    loading.dismiss();

                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String first_name = object.getString("first_name");
                                    String last_name = object.getString("last_name");
                                    String week = object.getString("weeklyprogress");
                                    String month = object.getString("monthlyprogress");
                                    String semester = object.getString("semesterprogress");
                                    String year = object.getString("yearprogress");
                                    String percentageClasses = object.getString("percentage");
                                    //set data to views
                                    nameET.setText("Welcome " + first_name + " " + last_name);
                                    weekProgress.setText("Weekly progress: " + Math.round(Float.parseFloat(week)) + "%");
                                    monthProgress.setText("Monthly progress: " + Math.round(Float.parseFloat(month)) + "%");
                                    semesterProgress.setText("Semester progress: " + Math.round(Float.parseFloat(semester)) + "%");
                                    yearProgress.setText("Yearly progress: " + Math.round(Float.parseFloat(year)) + "%");
                                    classesAttended.setText("Attendance\n   " + Math.round(Float.parseFloat(percentageClasses)) + "%");
                                    percentageProgressbar.setProgress(Float.parseFloat(percentageClasses));

                                }

                                ActivityCompat.requestPermissions(getActivity(), new String[]{
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
                                generatePdf(jsonArray, generatepdf);

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

    private void generatePdf(JSONArray jsonArray, Button generatepdf){
        loading2 = new ProgressDialog(getActivity());
        final String reg_number = intent.getStringExtra("username");
        final JSONArray json = jsonArray;
        Button generate = generatepdf;
        generate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick (View v){

                PdfDocument myPdfDocument = new PdfDocument();
                Paint myPaint = new Paint();

                PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
                PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);
                Canvas canvas = myPage.getCanvas();

                for (int i = 0; i < json.length(); i++) {
                    loading.dismiss();

                    JSONObject object = null;
                    try {

                        object = json.getJSONObject(i);

                        String first_name = object.getString("first_name");
                        String last_name = object.getString("last_name");
                        String college = object.getString("college");
                        String course = object.getString("course");
                        String week = object.getString("weeklyprogress");
                        String month = object.getString("monthlyprogress");
                        String semester = object.getString("semesterprogress");
                        String year = object.getString("yearprogress");
                        String total = object.getString("total_classes");
                        canvas.drawText("Name: "+first_name+" "+last_name, 40, 50, myPaint);
                        canvas.drawText("Registration number: "+reg_number, 40, 80, myPaint);
                        canvas.drawText("College: "+college, 40, 110, myPaint);
                        canvas.drawText("Course: "+course, 40, 140, myPaint);
                        canvas.drawText("Weekly progress: "+Math.round(Float.parseFloat(week))+"%", 40, 170, myPaint);
                        canvas.drawText("Monthly progress: "+Math.round(Float.parseFloat(month))+"%", 40, 200, myPaint);
                        canvas.drawText("Semester progress: "+Math.round(Float.parseFloat(semester))+"%", 40, 230, myPaint);
                        canvas.drawText("Yearly progress: "+Math.round(Float.parseFloat(year))+"%", 40, 260, myPaint);
                        canvas.drawText("Total classes attended: "+total, 40, 290, myPaint);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                myPdfDocument.finishPage(myPage);

                File file = new File(Environment.getExternalStorageDirectory(), reg_number);
                androidx.appcompat.app.AlertDialog.Builder builder= new androidx.appcompat.app.AlertDialog.Builder(getActivity());
                builder.setMessage("Pdf was successfully generated containing your progress report. Please search for a file named: "+reg_number+ " in your external storage");
                AlertDialog dialog = builder.create();
                dialog.show();

                try{

                    myPdfDocument.writeTo(new FileOutputStream(file));

                } catch (IOException e) {

                    e.printStackTrace();
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                }

                myPdfDocument.close();

            }
        });
    }

}
