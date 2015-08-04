package edu.cmu.mobile.team3.takeoutassistant;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wgtmac on 7/29/15.
 */
public class OCR {
    /*
    private String restaurant_name;
    private String address;
    private String phone_number;
    private HashMap<String, String> menu_list;
    */
    private List<String> lines;
    private Restaurant menu;
    private String _path;
    private String phoneNum;
    public OCR() {
        lines = new ArrayList<String>();
        menu = new Restaurant();
        phoneNum = "";
        _path = "";
    }

    public OCR(String path) {
        lines = Arrays.asList(path.split("\\n+"));
        _path = path.replaceAll("[^0-9]+", "");
         //menu = new PaperMenu();
        phoneNum = getPhoneNum();
        if(phoneNum.equals("") || phoneNum.length() != 10) {
            Log.v("OCR:", "phone Num not get");
            return;
        }
        //Log.v("OCR:", "phone Num " + phoneNum);

        YelpAPI yelpApi = new YelpAPI();
        //YelpAPI.queryAPI(yelpApi, "4126220133");

        menu  = YelpAPI.queryAPI(yelpApi, phoneNum);
        //Log.v("OCR:", menu.getRestaurantName());

    }

    public Restaurant getRestaurant() {
        return menu;
    }

    public String getPhoneNum() {
        if(_path.length() == 0)
            return "";

        //Pattern p1 = Pattern.compile("[0-9]{3}[-][0-9]{3}[-][0-9]{2}[-][0-9]{2} ");
           Pattern p = Pattern.compile("[0-9]+");
        //Matcher m1 = p1.matcher(_path);
        Matcher m = p.matcher(_path);

        Log.v("OCR:", _path);

        if(m.find()) {
            return m.group(0);
        }
        return "";
    }

}
