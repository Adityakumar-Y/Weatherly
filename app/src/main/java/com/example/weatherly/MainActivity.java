package com.example.weatherly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.weatherly.Interfaces.ApiEndpoints;
import com.example.weatherly.Models.Main;
import com.example.weatherly.Models.Data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvDetails, tvDate;
    private EditText etCityName;
    private Button btnSend;
    private ApiEndpoints api;
    private Spinner spTempType;
    private String units;
    private ImageView wtIcon;
    private Typeface custom_font;
    private static final String API_KEY = "0c4c0c2856ddeb6c2a2a5ff04998d875";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @SuppressLint("NewApi")
    private void init() {
        tvDetails = (TextView) findViewById(R.id.tvDetails);
        tvDate = (TextView) findViewById(R.id.tvDate);
        etCityName = (EditText) findViewById(R.id.etCity);
        spTempType = (Spinner) findViewById(R.id.spinner);
        wtIcon = (ImageView) findViewById(R.id.wtIcon);
        btnSend = (Button) findViewById(R.id.btnSend);
        custom_font = ResourcesCompat.getFont(this, R.font.dancingscript);;
        tvDate.setTypeface(custom_font, Typeface.BOLD);
        tvDate.setLetterSpacing((float) 0.35);

        btnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        hideKeyPad();
        String cityName = etCityName.getText().toString();
        String tempType = spTempType.getSelectedItem().toString();
        if (tempType.equals("°C")) {
            units = "Metric";
        } else if (tempType.equals("°F")) {
            units = "Imperial";
        }
        getWeatherData(cityName, units);
    }

    private void resetData(){
        tvDetails.setText("");
        tvDate.setText("");
        wtIcon.setVisibility(View.GONE);
    }

    private void hideKeyPad() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),0);
    }

    private void getWeatherData(String cityName, final String unit) {
        api = RetrofitInstance.getRetrofitInstance().create(ApiEndpoints.class);
        Call<Data> call = api.getData(cityName, API_KEY, unit);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                Data data = response.body();
                if (data != null && data.getCod() == 200) {
                    tvDetails.setText(data.toString());
                    Main main = data.getMain();
                    String s = "<b>City Name :</b> " + data.getName() + "<br>"
                            + "<b>Country :</b> " + data.getSys().getCountry() + "<br>"
                            + "<b>Temperature :</b> " + main.getTemp() + "<br>"
                            + "<b>Pressure :</b> " + main.getPressure() + "<br>"
                            + "<b>Humidity :</b> " + main.getHumidity() + "<br>"
                            + "<b>Description :</b> " + data.getWeather().get(0).getDescription();
                    tvDetails.setText(Html.fromHtml(s));

                    String imgUrl = "http://openweathermap.org/img/wn/"+data.getWeather().get(0).getIcon()+"@2x.png";
                    if(wtIcon.getVisibility() == View.GONE){
                        wtIcon.setVisibility(View.VISIBLE);
                    }
                    wtIcon.setBackgroundColor(Color.parseColor("#e8eaf6"));
                    Glide.with(MainActivity.this)
                            .load(imgUrl)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(wtIcon);


                    Date date = new Date(Long.valueOf(data.getDt())*1000);
                    DateFormat dateFormat = new SimpleDateFormat("EEEE dd/MM");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
                    String formattedDate = dateFormat.format(date);

                    tvDate.setText(formattedDate);

                } else {
                    Toast.makeText(MainActivity.this, "Invalid City Name !! ", Toast.LENGTH_SHORT).show();
                    resetData();
                }

            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                resetData();
                Toast.makeText(MainActivity.this, "Something Went Wrong :( ", Toast.LENGTH_SHORT).show();
                Log.d("MainActivity", t.toString());
            }
        });
    }
}
