package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CityFinder extends AppCompatActivity {

   TextInputEditText city;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_finder);
        city=findViewById(R.id.ChangeCity);
        button = findViewById(R.id.search);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = city.getText().toString();
                Intent intent = new Intent(CityFinder.this,MainActivity.class);
                intent.putExtra("city",cityName);
                startActivity(intent);
                   finish();
            }
        });


        

    }
}