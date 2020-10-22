package com.example.attendance;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private EditText first_name, last_name, email, phone_number, college, course, course_year, reg_number, password, confirm;
    private Button register;
    private static String URL_REGISTER = "http://hgfoundations.000webhostapp.com/register_student.php";
    private static String URL_CONFIRM = "http://hgfoundations.000webhostapp.com/confirm_code.php";
    private static String URL_SEND_CODE = "http://hgfoundations.000webhostapp.com/send_code.php";
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_acitvity);

        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        email = findViewById(R.id.email);
        phone_number = findViewById(R.id.phone_Number);
        college = findViewById(R.id.college);
        course = findViewById(R.id.course);
        course_year = findViewById(R.id.course_year);
        reg_number = findViewById(R.id.reg_number);
        password = findViewById(R.id.password);
        confirm = findViewById(R.id.confirm_pass);
        register = findViewById(R.id.button_register);
        loading = new ProgressDialog(this);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCode();
            }
        });
    }

    private void sendCode() {

        final String email = this.email.getText().toString().trim();
        loading.setMessage("Kindly wait as we register you to the system....");
        loading.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SEND_CODE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (success) {
                                loading.dismiss();
                                Toast.makeText(RegisterActivity.this, "We have sent a verification code to your student email. Check your email to verify your email", Toast.LENGTH_SHORT).show();

                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setTitle("Enter verification code sent to your email");
                                LinearLayout linearLayout = new LinearLayout(RegisterActivity.this);

                                final EditText verification_codeEt = new EditText(RegisterActivity.this);
                                verification_codeEt.setHint("Verification code");
                                verification_codeEt.setInputType(InputType.TYPE_CLASS_PHONE);
                                verification_codeEt.setMinEms(16);

                                linearLayout.addView(verification_codeEt);
                                linearLayout.setPadding(10, 10, 10, 10);
                                builder.setView(linearLayout);

                                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String code = verification_codeEt.getText().toString().trim();
                                        confirmCode(code);
                                    }
                                });

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                builder.create().show();

                            } else {
                                Toast.makeText(RegisterActivity.this, "The email you submitted does not exist. Please use another email!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            loading.dismiss();
                            Toast.makeText(RegisterActivity.this, "An error occurred while processing your request!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(RegisterActivity.this, "Check your internet connection!" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void confirmCode(final String code) {

        final String verification_code = code;
        final String email = this.email.getText().toString().trim();
        loading.setMessage("Verifying your code....");
        loading.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CONFIRM,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (success) {
                                loading.dismiss();
                                Toast.makeText(RegisterActivity.this, "Your student email has been successfully verified.", Toast.LENGTH_SHORT).show();
                                final String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                                Register(deviceId);

                            } else {
                                Toast.makeText(RegisterActivity.this, "The code you submitted does not match the one in our records!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            loading.dismiss();
                            Toast.makeText(RegisterActivity.this, "Verification Error!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(RegisterActivity.this, "Check your internet connection!" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("code", verification_code);
                params.put("student_email", email);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
        requestQueue.add(stringRequest);
    }


    private void Register(final String deviceId) {
        final String firstName = this.first_name.getText().toString().trim();
        final String lastName = this.last_name.getText().toString().trim();
        final String studEmail = this.email.getText().toString().trim();
        final String phoneNumber = this.phone_number.getText().toString().trim();
        final String studCollege = this.college.getText().toString().trim();
        final String studCourse = this.course.getText().toString().trim();
        final String studCourseYear = this.course_year.getText().toString().trim();
        final String regNumber = this.reg_number.getText().toString().trim();
        final String passWord = this.password.getText().toString().trim();
        final String confirmPassword = this.confirm.getText().toString().trim();
        loading.setMessage("Kindly wait as we register you to the system....");
        loading.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            if (success) {
                                loading.dismiss();
                                Toast.makeText(RegisterActivity.this, "You have successfully registered an account with us. Please proceed to logging in.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, SigninActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                loading.dismiss();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String message = object.getString("message");
                                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            loading.dismiss();
                            Toast.makeText(RegisterActivity.this, "An error occurred while processing your request! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(RegisterActivity.this, "Check your internet connection!" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("first", firstName);
                params.put("last", lastName);
                params.put("email", studEmail);
                params.put("phone", phoneNumber);
                params.put("college", studCollege);
                params.put("course", studCourse);
                params.put("course_year", studCourseYear);
                params.put("reg", regNumber);
                params.put("pass_word", passWord);
                params.put("confirm", confirmPassword);
                params.put("device", deviceId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}



