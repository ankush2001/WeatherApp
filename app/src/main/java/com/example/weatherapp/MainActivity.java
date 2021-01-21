package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    TextView resultTextview;
    public void ShowWeather(View view){
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute("https//openweathermap.org/data/2.5/forecast/hourly?q="+editText.getText().toString()+"&appid=b1b15e88fa797225412429c1c50c122a1");//plz changed link to use it
    }
    public class DownloadTask extends AsyncTask<String , Void , String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String  weather = jsonObject.getString("weather");
                Log.i("Weather Content",weather);

                JSONArray array = new JSONArray();
                String msg = "";
                for(int i=0; i<array.length() ; i++){
                    JSONObject jsonPart = array.getJSONObject(i);
                    String main = jsonPart.getString("main");
                    String description =jsonPart.getString("description");
                    if(!main.equals("") && !description.equals("")){
                        msg += main + ": " + description;
                    }
                }
                if(!msg.equals("")){
                    resultTextview.setText(msg);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.cityText);
        resultTextview = findViewById(R.id.weatherView);
    }
}