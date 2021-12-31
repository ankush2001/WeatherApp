package com.example.weather;
import androidx.annotation.NonNull;
import cz.msebera.android.httpclient.Header;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {



    final String API = "dab3af44de7d24ae7ff86549334e45bd";
    final String url = "https://api.openweathermap.org/data/2.5/weather";
    final long MIN_TIME = 5000;
    final long MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;


    String Location_provider = LocationManager.GPS_PROVIDER;

    TextView temperatureIV, conditionIV, cityNameIV;
    ImageView weatherIconIV;

    RelativeLayout relativeLayout;
    LocationManager locationManager;
    LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperatureIV = findViewById(R.id.temperature);
        conditionIV = findViewById(R.id.condition);
        cityNameIV = findViewById(R.id.cityName);
        weatherIconIV = findViewById(R.id.weatherIcon);
        relativeLayout = findViewById(R.id.cityFinder);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,CityFinder.class);
                startActivity(intent);

            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String city = intent.getStringExtra("city");

                 if(city!=null) {

                     getWeatherForNewCity(city);
                 }else {
                     getWeatherForCurrentLocation();
                 }
    }

    private void getWeatherForNewCity(String city){
        RequestParams params = new RequestParams();
        params.put("q",city);
        params.put("appid",API);
        letsDoSomeNetworking(params);
    }

    private void getWeatherForCurrentLocation() {
        //provide the current location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged( Location location) {
                //getting longitude and latitude of users location
                String Latitude = String.valueOf(location.getLatitude());
                String Longitude = String.valueOf(location.getLongitude());

                RequestParams params = new RequestParams();
                params.put("lat",Latitude);
                params.put("long",Longitude);
                params.put("appid",API);
                letsDoSomeNetworking(params);

            }


            @Override
            public void onProviderEnabled(@NonNull String provider) {
                //not able to get Location
                Toast.makeText(MainActivity.this, "Not able to get the location", Toast.LENGTH_SHORT).show();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                //not able to get location
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        locationManager.requestLocationUpdates(Location_provider, MIN_TIME, MIN_DISTANCE, locationListener);
    }

    //weather the user allow the location or not
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==REQUEST_CODE){

            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this,"Location fetching Succesffull",Toast.LENGTH_SHORT).show();
                getWeatherForCurrentLocation();
            }else{
                //user denied the permission
                Toast.makeText(MainActivity.this,"Please allow the location access",Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void letsDoSomeNetworking(RequestParams params){

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(url,params,new JsonHttpResponseHandler(){


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                Toast.makeText(MainActivity.this,"Done",Toast.LENGTH_SHORT).show();
                WeatherData weatherD = WeatherData.fromJson(response);

                updateUI(weatherD);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,JSONObject response) {
               // super.onFailure(statusCode, headers, responseString, throwable);
            }
        });

    }
    private void updateUI(WeatherData weather){
        temperatureIV.setText(weather.getTemperature());
        conditionIV.setText(weather.getWeatherType());
        cityNameIV.setText(weather.getCity());
        int resourceID=getResources().getIdentifier(weather.getMIcon(),"drawable",getPackageName());
        weatherIconIV.setImageResource(resourceID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(locationManager!=null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}