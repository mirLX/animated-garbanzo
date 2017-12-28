package a_0001.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 李旭 on 2017/12/25.
 */

public class City extends DataSupport {
    private  int id;
    private  String cityName;//记录城市的名字
    private  int cityCode;//记录城市的代号
    private  int  provinceId;//记录当前市所属省份的id值
    public int getId(){
        return id;
    }
    public  void setId(int id){
        this.id=id;

    }
    public String getCityName(){
        return  cityName;
    }
    public void setCityName(String cityName){
        this.cityName=cityName;
    }
    public int getCityCode(){
        return cityCode;
    }
    public void setCityCode(int cityCode){
        this.cityCode=cityCode;
    }
    public  int getProvinceId(){
        return provinceId;
    }
    public  void setProvinceId(int provinceId){
        this.provinceId=provinceId;
    }
}
