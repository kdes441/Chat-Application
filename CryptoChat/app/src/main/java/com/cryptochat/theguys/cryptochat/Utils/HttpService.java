package com.cryptochat.theguys.cryptochat.Utils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


/**
 * Created by kyle on 3/25/17.
 */

public class HttpService {

    //Creates new async http clients
    private static AsyncHttpClient client = new AsyncHttpClient();

    //Sends get request
    public  static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    //Sends post request
    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    //Gets the full url
    private static String getAbsoluteUrl(String relativeUrl) {
        return Utils.API_URL + relativeUrl;
    }

}
