package com.example.attendance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class ChangeActivity extends AppCompatActivity {

    private EditText new_password, confirm_password;
    private Button change;
    private static String URL_CHANGE ="http://hgfoundations.000webhostapp.com/update_password.php";
    private ProgressDialog loading;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);

        new_password = findViewById(R.id.new_password);
        confirm_password = findViewById(R.id.confirm_new_password);
        change = findViewById(R.id.button_change);

        loading = new ProgressDialog(this);

        intent = this.getIntent();
        if (intent != null){
            String reg_number = intent.getStringExtra("reg_number");
            Toast.makeText(this, "Hey  "+reg_number, Toast.LENGTH_SHORT).show();
        }

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Password = new_password.getText().toString().trim();
                String Confirm = confirm_password.getText().toString().trim();
                String Reg_number = intent.getStringExtra("reg_number");


                if (!Password.isEmpty() || !Confirm.isEmpty()){
                    Change(Password, Confirm, Reg_number);
                }
                else{
                    new_password.setError("Password cannot be empty!");
                    confirm_password.setError("Password cannot be empty!");
                }
            }
        });


    }

    private void Change(final String password, final String confirm, final String  reg_number) {
        loading.setMessage("Kindly wait as we update your password....");
        loading.show();
        Toast.makeText(this, "Hey  "+reg_number+""+password, Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CHANGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (success){
                                loading.dismiss();
                                Toast.makeText(ChangeActivity.this, "Password updated.", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(ChangeActivity.this, SigninActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            else {
                                Toast.makeText(ChangeActivity.this,"An error occurred! Please ry again later or use relevant details.", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            }

                        } catch (JSONException e){
                            e.printStackTrace();
                            loading.dismiss();
                            Toast.makeText(ChangeActivity.this, "Error occurred! " +e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(ChangeActivity.this, "Error! " +error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("password", password);
                params.put("confirm", confirm);
                params.put("reg_number", reg_number);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}
