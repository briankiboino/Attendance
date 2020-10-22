package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class SigninActivity extends AppCompatActivity {

    private EditText username, pass;
    private Button log_in;
    private static String URL_LOGIN="http://hgfoundations.000webhostapp.com/attendance_login.php";
    private ProgressDialog loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

         username = findViewById(R.id.username);
         pass = findViewById(R.id.pass_word);
         log_in = findViewById(R.id.button_log_in);

         loading = new ProgressDialog(this);

         log_in.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String Username = username.getText().toString().trim();
                 String Password = pass.getText().toString().trim();

                 if (!Username.isEmpty() || !Password.isEmpty()){
                     Login(Username, Password);
                 }
                 else{
                     username.setError("Registration number cannot be empty!");
                     pass.setError("Password cannot be empty!");
                 }
             }
         });
    }

    private void Login(final String username, final String pass_word) {
        final String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        loading.setMessage("Signing you in....");
        loading.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        if (success){
                            loading.dismiss();
                            Toast.makeText(SigninActivity.this, "Success Login.", Toast.LENGTH_SHORT).show();

                            for (int i = 0; i < jsonArray.length(); i++){

                                JSONObject object = jsonArray.getJSONObject(i);
                                String username = object.getString("username");
                                Intent intent = new Intent(SigninActivity.this, HomeActivity.class);
                                intent.putExtra("username", username);
                                startActivity(intent);
                                finish();
                            }
                        }
                        else {
                            loading.dismiss();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String message = object.getString("message");
                                Toast.makeText(SigninActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e){
                        e.printStackTrace();
                        loading.dismiss();
                        Toast.makeText(SigninActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                    }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(SigninActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", pass_word);
                params.put("device", deviceId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public void forgotPassword(View view){
        TextView textView = (TextView) findViewById(R.id.forgot);
        textView.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v){
                Intent myIntent = new Intent(SigninActivity.this,ForgotPassword.class);
                startActivity(myIntent);
            }
        });
    }
}
