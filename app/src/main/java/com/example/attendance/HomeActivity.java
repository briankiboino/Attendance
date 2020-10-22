package com.example.attendance;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private  Intent intent;
    private static String URL_LOGOUT="http://hgfoundations.000webhostapp.com/attendance_logout.php";
    private ProgressDialog loading;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    TextView txtLat;
    String lat;
    String provider;
    protected String latitude,longitude;
    protected boolean gps_enabled,network_enabled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_attendance, R.id.nav_timetable, R.id.nav_progress)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int  id = item.getItemId();
        if (id == R.id.action_logout){
            showLogoutDialogue();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutDialogue() {
        AlertDialog.Builder builder= new AlertDialog.Builder(HomeActivity.this);
        LinearLayout linearLayout = new LinearLayout(HomeActivity.this);
        final TextView textView = new TextView(HomeActivity.this);
        textView.setText("Are you sure you want to log out?");
        textView.setTextSize(20);
        linearLayout.addView(textView);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intent = getIntent();
                final String reg_number = intent.getStringExtra("username");
                logout(reg_number);
            }
        });

        builder.create().show();
    }

    private void logout(final String reg_number) {
            loading = new ProgressDialog(this);
            loading.setMessage("Signing you out....");
            loading.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGOUT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");
                                JSONArray jsonArray = jsonObject.getJSONArray("data");

                                if (success){
                                    loading.dismiss();
                                    Toast.makeText(HomeActivity.this, "Success sign out", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(new Intent(HomeActivity.this, SigninActivity.class)));
                                    finish();

                                }
                                else {

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        loading.dismiss();
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        String message = object.getString("message");
                                        Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (JSONException e){
                                e.printStackTrace();
                                loading.dismiss();
                                Toast.makeText(HomeActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loading.dismiss();
                            Toast.makeText(HomeActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", reg_number);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);
            requestQueue.add(stringRequest);
        }


    }



