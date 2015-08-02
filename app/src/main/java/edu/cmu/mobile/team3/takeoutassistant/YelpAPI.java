package edu.cmu.mobile.team3.takeoutassistant;

import android.util.Log;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;


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


    public static Restaurant queryAPI(YelpAPI yelpApi, String PhoneNum) {
        // Select the first business and display business details
        String response = yelpApi.searchForRestaurantByPhoneNumber(PhoneNum).replaceAll("[\\[ \\] \\{ \\} \"]", "");
        //System.out.println(response.replaceAll("[\\[ \\] \\{ \\}]", ""));
        Log.v("MyYelpAPI:", response );
        String[]str = response.split(",");
        Restaurant menu = new Restaurant();
        for(int i = 0; i < str.length; i++) {
            String[] tmp = str[i].split(":");
            if(tmp[0].equals("name"))
                menu.setName(tmp[1] + "\n");
            else if(tmp[0].equals("phone"))
                menu.setPhone(tmp[1] + "\n");
            else if(tmp[0].equals("display_address")) {
                String s = "";
                s += tmp[1] + "\n";//311SCraigSt
                i++;
                s += str[i++] + "\n";//Pittsburgh
                s += str[i++] + "\n";//Oakland
                //System.out.println(str[i++]);//display_address:311SCraigSt
                menu.setAddress(s);
                break;
            }
        }
        return menu;
    }

}
