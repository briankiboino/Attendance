package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StarterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void registerAccount(View view){
        Button button = (Button) findViewById(R.id.register);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v){
                Intent myIntent = new Intent(StarterActivity.this, RegisterActivity.class);
                startActivity(myIntent);
            }
        });
    }

    public void signIn(View view){
        Button button = (Button) findViewById(R.id.button_signin);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v){
                Intent myIntent = new Intent(StarterActivity.this,SigninActivity.class);
                startActivity(myIntent);
            }
        });
    }
}
