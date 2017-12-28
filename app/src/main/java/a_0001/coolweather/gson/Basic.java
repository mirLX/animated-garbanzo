package a_0001.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 李旭 on 2017/12/26.
 */

public class Basic {
    @SerializedName("city")
    public  String cityName;
    @SerializedName("id")
    public  String  weatherId;
    public  Update  update;
    public  class Update{
        @SerializedName("loc")
        public  String updateTime;
    }
}

