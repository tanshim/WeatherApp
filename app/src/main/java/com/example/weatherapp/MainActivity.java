package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private TextView textViewApi;
    private EditText editTextCity;

    private String urlApi="https://api.openweathermap.org/data/2.5/weather?q=%s&appid=(Put_your_API_key_here)&units=metric&lang=ru";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewApi = findViewById(R.id.textViewWeather);
        editTextCity = findViewById(R.id.editTextCity);
    }

    public void onClickGetWeather(View view) {
        DownloadContent task = new DownloadContent();
        String city = editTextCity.getText().toString().trim();
        String url = String.format(urlApi,city);
        String response = null;
        try {
            response = task.execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject(response);
            String cityName = jsonObject.getString("name");
            String temp = jsonObject.getJSONObject("main").getString("temp");
            String weather = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
            String result = String.format("%s\nТемпература: %s°C\nНа улице: %s",cityName,temp,weather);

            textViewApi.setText(result);

        } catch (JSONException e) {
            e.printStackTrace();
        }
          catch (NullPointerException e){
            textViewApi.setText("");
            Toast.makeText(this, "Нету такого города", Toast.LENGTH_SHORT).show();
        }
    }


    private static class DownloadContent extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder stringBuilder = new StringBuilder();

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = bufferedReader.readLine();
                while (line != null){
                    stringBuilder.append(line);
                    line = bufferedReader.readLine();
                }
                return stringBuilder.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
            }
            return null;
        }
    }
}
