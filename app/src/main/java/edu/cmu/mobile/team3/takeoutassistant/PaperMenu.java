package edu.cmu.mobile.team3.takeoutassistant;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Created by Gang Wu on 7/16/15.
 */
public class PaperMenu implements Parcelable {
    private String restaurant_name;
    private String address;
    private String phone_number;
    private HashMap<String, String> menu_list;

    public PaperMenu() {
        menu_list = new HashMap<String, String>();
    }

    public String getRestaurantName() {
        return restaurant_name;
    }

    public void setRestaurantName(String name) {
        this.restaurant_name = name;
    }

    public void setMenuList(HashMap<String, String> map) {
        menu_list = map;
    }

    public HashMap<String, String> getMenuList() {
        return menu_list;
    }

    public void addToMenu(String name, String price) {
        menu_list.put(name, price);
    }

    public String getPhoneNumber() {
        return phone_number;
    }

    public void setPhoneNumber(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "PaperMenu{" +
                "Restaurant='" + restaurant_name + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        Bundle bundle = new Bundle();

        bundle.putString("RESTAURANT_NAME", restaurant_name);
        bundle.putString("PHONE_NUMBER", phone_number);
        bundle.putString("ADDRESS", address);
        bundle.putSerializable("MENU_LIST", menu_list);

        parcel.writeBundle(bundle);
    }

    public static final Parcelable.Creator<PaperMenu> CREATOR = new Creator<PaperMenu>() {
        @Override
        public PaperMenu createFromParcel(Parcel source) {
            Bundle bundle = source.readBundle();

            PaperMenu pm = new PaperMenu();
            pm.setRestaurantName(bundle.getString("RESTAURANT_NAME"));
            pm.setAddress(bundle.getString("ADDRESS"));
            pm.setPhoneNumber(bundle.getString("PHONE_NUMBER"));
            pm.setMenuList((HashMap<String, String>) bundle.getSerializable("MENU_LIST"));
            return pm;
        }

        @Override
        public PaperMenu[] newArray(int size) {
            return new PaperMenu[size];
        }
    };
}
