package a_0001.coolweather;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;

import a_0001.coolweather.gson.Forecast;
import a_0001.coolweather.gson.Weather;
import a_0001.coolweather.service.AutoUpdateService;
import a_0001.coolweather.util.HttpUtil;
import a_0001.coolweather.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private  TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private  TextView aqiText;
    private  TextView pm25Text;
    private TextView comforText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefreshLayout;
    private  String mWeatherId;
    public DrawerLayout drawerLayout;
    private Button navButton;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //版本控制(当系统版本大于等于21，也就是5.0以上系统时才会执行后面的代码)
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        //初始化控件
        drawerLayout=findViewById(R.id.drawer_layout);
        navButton=findViewById(R.id.nav_button);
        weatherLayout=findViewById(R.id.weather_Layout);
        titleCity=findViewById(R.id.title_city);
        titleUpdateTime=findViewById(R.id.title_update_time);
        degreeText=findViewById(R.id.degree_text);
        weatherInfoText=findViewById(R.id.weather_info_text);
        forecastLayout=findViewById(R.id.forecast_layout);
        aqiText=findViewById(R.id.aqi_text);
        pm25Text=findViewById(R.id.pm25_text);
        comforText=findViewById(R.id.comfort_text);
        carWashText=findViewById(R.id.car_wash_text);
        sportText=findViewById(R.id.sport_text);
        bingPicImg=findViewById(R.id.bing_pic_img);
        swipeRefreshLayout=findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=prefs.getString("weather",null);
        String bingPic=prefs.getString("bing_pic",null);
        if (bingPic!=null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else {
            loadBingpic();
        }
        if (weatherString!=null){
            //有缓存是直接解析天气数据
            Weather weather= Utility.handleWeatherResponse(weatherString);
            mWeatherId=weather.basic.weatherId;
            showWeatherInfo(weather);
        }else {
            //无缓存时去服务器查询天气
            mWeatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requsetWeather(mWeatherId);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(WeatherActivity.this);
                String temp = prefs.getString("weather_id", mWeatherId);
               requsetWeather(temp);
            }
        });
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }
    /**
     * 根据天气id请求城市天气信息
     */
    public void requsetWeather(final String weatherId){
        String weatherUrl="http://guolin.tech/api/weather?cityid=" + weatherId + "&key=3f30fa4f75f74537bbc0da50566dc862";
        this.mWeatherId=weatherId;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                final Weather weather=Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather!=null&&"ok".equals(weather.status)){
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
        loadBingpic();
    }
    /**
     * 加载必应每一日图
     */
    private void loadBingpic() {
        String requsetBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requsetBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final  String bingPic=response.body().string();
                SharedPreferences .Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
    /**
     * 处理并展示Weather实体类中的数据
     */
    public  void showWeatherInfo(Weather weather){
         String cityName=weather.basic.cityName;
         String updateTime=weather.basic.update.updateTime.split(" ")[1];
         String degree=weather.now.temperature+"℃";
         String weatherInfo=weather.now.more.info;
         titleCity.setText(cityName);
         titleUpdateTime.setText(updateTime);
         degreeText.setText(degree);
         weatherInfoText.setText(weatherInfo);
         forecastLayout.removeAllViews();
         for (Forecast forecast:weather.forecastList){
             View view= LayoutInflater.from(this).inflate(R.layout.forecast_itme,forecastLayout,false);
             TextView dateText=view.findViewById(R.id.date_text);
             TextView infoText=view.findViewById(R.id.info_text);
             TextView maxText=view.findViewById(R.id.max_text);
             TextView minText=view.findViewById(R.id.min_text);
             dateText.setText(forecast.date);
             infoText.setText(forecast.more.info);
             maxText.setText(forecast.temperature.max);
             minText.setText(forecast.temperature.min);
             forecastLayout.addView(view);
         }
         if (weather.aqi!=null){
             aqiText.setText(""+weather.aqi.city.qlty);
             pm25Text.setText(""+weather.aqi.city.pm25);
         }
         String comfort="舒适度："+weather.suggestion.comfort.info;
         String carWash="洗车指数："+weather.suggestion.carWash.info;
         String sport="运动建议："+weather.suggestion.sport.info;
         comforText.setText(comfort);
         carWashText.setText(carWash);
         sportText.setText(sport);
         weatherLayout.setVisibility(View.VISIBLE);
        Intent intent=new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

}
