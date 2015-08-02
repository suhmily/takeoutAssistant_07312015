package edu.cmu.mobile.team3.takeoutassistant;

import android.util.Log;

import java.util.Map;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import edu.cmu.mobile.team3.takeoutassistant.PaperMenu;
import edu.cmu.mobile.team3.takeoutassistant.TwoStepOAuth;


/**
 * Code sample for accessing the Yelp API V2.
 *
 * This program demonstrates the capability of the Yelp API version 2.0 by using the Search API to
 * query for businesses by a search term and location, and the Business API to query additional
 * information about the top result from the search query.
 *
 * <p>
 * See <a href="http://www.yelp.com/developers/documentation">Yelp Documentation</a> for more info.
 *
 */
public class YelpAPI {

    private static final String API_HOST = "api.yelp.com";

    /*
     * Update OAuth credentials below from the Yelp Developers API site:
     * http://www.yelp.com/developers/getting_started/api_access
     */
    private static final String CONSUMER_KEY = "6kQLsigUnTnDQbmGpEcg4A";
    private static final String CONSUMER_SECRET = "H88UJpXep-kxvZ2xlgaRML0nR4w";
    private static final String TOKEN = "0P2j4-EFmRKcuWFNUopYE1yDxvEvoad-";
    private static final String TOKEN_SECRET = "O3Z75Xono99iPrYYWQC2olMDd-c";

    OAuthService service;
    Token accessToken;


    public YelpAPI() {
        this.service =
                new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(CONSUMER_KEY)
                        .apiSecret(CONSUMER_SECRET).build();
        this.accessToken = new Token(TOKEN, TOKEN_SECRET);
        //Log.v("MyYelpAPI:", "Created");
    }

    public String searchForRestaurantByPhoneNumber(String phoneNumber) {
        OAuthRequest request = createOAuthRequest("/v2/phone_search?phone=+1" + phoneNumber);
        //Log.v("MyYelpAPI:", "searchForRestaurantByPhoneNumber");
        return sendRequestAndGetResponse(request);
    }
    /**
     * Creates and returns an {@link OAuthRequest} based on the API endpoint specified.
     *
     * @param path API endpoint to be queried
     * @return <tt>OAuthRequest</tt>
     */
    private OAuthRequest createOAuthRequest(String path) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://" + API_HOST + path);
        //Log.v("MyYelpAPI:", "createOAuthRequest");
        return request;
    }

    /**
     * Sends an {@link OAuthRequest} and returns the {@link Response} body.
     *
     * @param request {@link OAuthRequest} corresponding to the API request
     * @return <tt>String</tt> body of API response
     */
    private String sendRequestAndGetResponse(OAuthRequest request) {
        //Log.v("MyYelpAPI:", "sendRequestAndGetResponse " + request.getCompleteUrl());
        this.service.signRequest(this.accessToken, request);
        // Log.v("MyYelpAPI:", "Service");
        //Log.v("MyYelpAPI:", request.g);
        Response response = request.send();
        //Log.v("MyYelpAPI:", );
        //Log.v("MyYelpAPI:", "response");
        return response.getBody();
    }


    public static PaperMenu queryAPI(YelpAPI yelpApi,String PhoneNum) {
        // Select the first business and display business details
        String response = yelpApi.searchForRestaurantByPhoneNumber(PhoneNum).replaceAll("[\\[ \\] \\{ \\} \"]", "");
        //System.out.println(response.replaceAll("[\\[ \\] \\{ \\}]", ""));
        Log.v("MyYelpAPI:", response );
        PaperMenu menu = new PaperMenu();
        try{
            JSONObject mainJson = new JSONObject(response);
            JSONArray businesses = mainJson.getJSONArray("businesses");

            JSONObject business = businesses.getJSONObject(0);
            menu.setRestaurantName(business.getString("name"));
            //System.out.println("rating: " + business.getString("rating"));
            //System.out.println("mobile_url: " + business.getString("mobile_url"));
            menu.setPhoneNumber(business.getString("phone"));
            JSONArray address = business.getJSONObject("location").getJSONArray("display_address");
            String address_string = "";
            for (int j=0; j < address.length(); j++)
                address_string = address_string + address.getString(j) + " ";
            menu.setAddress(address_string);

            JSONArray category = business.getJSONArray("categories");
            String category_string = "";
            for (int j = 0; j < category.length(); j = j + 1) {
                JSONArray subCat = category.getJSONArray(j);
                category_string = category_string + subCat.getString(1) + ", ";
            }

            menu.setCategory(category_string);

            
            menu.setReview(business.getString("review_count"));
            menu.setImage(business.getString("snippet_image_url"));
            menu.setUrl(business.getString("mobile_url"));
             menu.setRating(business.getString("rating_img_url"));

        }
        catch(Exception e){System.out.println(e);}
        return menu;
    }

}
