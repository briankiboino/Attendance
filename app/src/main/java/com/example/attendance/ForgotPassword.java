package com.example.attendance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class ForgotPassword extends AppCompatActivity {

    private EditText reg_number, email;
    private Button verify;
    private static String URL_CONFIRM_CODE = "http://hgfoundations.000webhostapp.com/confirm_code.php";
    private static String URL_VERIFY_EMAIL ="http://hgfoundations.000webhostapp.com/attendance_verification.php";
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        reg_number = findViewById(R.id.verify_reg_number);
        email = findViewById(R.id.verify_email);
        verify = findViewById(R.id.button_verify);

        loading = new ProgressDialog(this);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String registration_number = reg_number.getText().toString().trim();
                String student_email = email.getText().toString().trim();

                if (!registration_number.isEmpty() || !student_email.isEmpty()){
                    Verify(registration_number, student_email);
                }
                else{
                    reg_number.setError("Registration number cannot be empty!");
                    email.setError("Student email cannot be empty cannot be empty!");
                }
            }
        });
    }
    private void Verify(final String registration_number, final String student_email) {

        loading.setMessage("Verifying your registration number...");
        loading.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_VERIFY_EMAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (success){
                                loading.dismiss();
                                Toast.makeText(ForgotPassword.this, "Registration number verified successfully. We have sent a verification code to your student email.", Toast.LENGTH_SHORT).show();

                                AlertDialog.Builder builder= new AlertDialog.Builder(ForgotPassword.this);
                                builder.setTitle("Enter verification code sent to your email");
                                LinearLayout linearLayout = new LinearLayout(ForgotPassword.this);

                                final EditText verification_codeEt = new EditText(ForgotPassword.this);
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
                                        verifyCode(code);
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
                            else {
                                Toast.makeText(ForgotPassword.this,"User does not exist!", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            }

                        } catch (JSONException e){
                            e.printStackTrace();
                            loading.dismiss();
                            Toast.makeText(ForgotPassword.this, "Error occurred while processing your request! " +e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(ForgotPassword.this, "Error! " +error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("reg", registration_number);
                params.put("student_email", student_email);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void verifyCode(final String code) {

        final String email = this.email.getText().toString().trim();
        loading.setMessage("Verifying your email and code....");
        loading.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CONFIRM_CODE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (success){
                                loading.dismiss();
                                Toast.makeText(ForgotPassword.this, "Your code has been successfully verified.", Toast.LENGTH_SHORT).show();
                                String registration_number = reg_number.getText().toString().trim();
                                Intent intent = new Intent(ForgotPassword.this, ChangeActivity.class);
                                intent.putExtra("reg_number", registration_number);
                                startActivity(intent);
                                finish();

                            }
                            else{
                                Toast.makeText(ForgotPassword.this, "The code you submitted does not match the one in our records!", Toast.LENGTH_SHORT).show();
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                            loading.dismiss();
                            Toast.makeText(ForgotPassword.this,"Verification Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(ForgotPassword.this, "Verification Error! " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("code", code);
                params.put("student_email", email);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ForgotPassword.this);
        requestQueue.add(stringRequest);
    }

}
