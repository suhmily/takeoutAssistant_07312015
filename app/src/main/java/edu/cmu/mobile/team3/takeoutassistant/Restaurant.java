package edu.cmu.mobile.team3.takeoutassistant;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Gang Wu on 7/16/15.
 */
public class Restaurant implements Parcelable {
    private String name;
    private String address;
    private String phone;
    private String category;
    private String review;
    private String rating;
    private String image;
    private String url;

    public String getReview() {
        return review == null ? "Nice food!" : review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    private String snippet;

    public Restaurant() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone_number) {
        this.phone = phone_number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", category='" + category + '\'' +
                ", review='" + review + '\'' +
                ", rating='" + rating + '\'' +
                ", image='" + image + '\'' +
                ", url='" + url + '\'' +
                ", snippet='" + snippet + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        Bundle bundle = new Bundle();

        bundle.putString("RESTAURANT_NAME", name);
        bundle.putString("PHONE_NUMBER", phone);
        bundle.putString("ADDRESS", address);
        bundle.putString("CATEGORY", category);
        bundle.putString("SNIPPET", snippet);
        bundle.putString("IMAGE", image);
        bundle.putString("URL", url);
        bundle.putString("REVIEW", review);
        bundle.putString("RATING", rating);

        parcel.writeBundle(bundle);
    }

    public static final Parcelable.Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel source) {
            Bundle bundle = source.readBundle();

            Restaurant pm = new Restaurant();
            pm.setName(bundle.getString("RESTAURANT_NAME"));
            pm.setAddress(bundle.getString("ADDRESS"));
            pm.setPhone(bundle.getString("PHONE_NUMBER"));
            pm.setCategory(bundle.getString("CATEGORY"));
            pm.setSnippet(bundle.getString("SNIPPET"));

            pm.setImage(bundle.getString("IMAGE"));
            pm.setUrl(bundle.getString("URL"));
            pm.setReview(bundle.getString("REVIEW"));
            pm.setRating(bundle.getString("RATING"));
            return pm;
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };
}
