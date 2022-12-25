package com.seabird.whatsdev.network.di;

import android.util.Log;

import com.seabird.whatsdev.utils.AppConstants;
import com.seabird.whatsdev.utils.SharedPreferenceManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Authenticator;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {
    private static final String TAG = TokenAuthenticator.class.getSimpleName();
    private static final OkHttpClient client = new OkHttpClient();
    private static final String TOKEN = "token";
    private static final int RETRY_LIMIT = 3;

    public TokenAuthenticator() {
    }

    public Request authenticate(Route route, @NotNull Response response) throws IOException {
        int retryCount = this.responseCount(response);
        if (retryCount >= 3) {
            Log.i(TAG, "Retry count exceeded! Giving up.");
            return null;
        } else {
            Log.d(TAG, "Retrying count: " + retryCount);
            Log.i(TAG, "Refreshing Auth token...");
            if (this.refreshToken()) {
                Log.i(TAG, "Proceeding the request with new Auth token...");
                String newAccessToken = SharedPreferenceManager.Companion.getStringValue(AppConstants.ACCESS_TOKEN);
                return response.request().newBuilder().header("Authorization", "Bearer "+newAccessToken).build();
            } else {
                Log.e(TAG, "Refreshing Auth token Failed...");
                return response.request();
            }
        }
    }

    private int responseCount(Response response) {
        int result;
        for(result = 1; (response = response.priorResponse()) != null; ++result) {
        }

        return result;
    }

    private boolean refreshToken() throws IOException {
        String token = SharedPreferenceManager.Companion.getStringValue(AppConstants.REFRESH_TOKEN);

        MediaType json = MediaType.parse("application/json; charset=utf-8");
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(AppConstants.BASE_URL + "auth/token/refresh");
        requestBuilder.addHeader("Authorization", "Bearer "+token);
        requestBuilder.get();
        Response response = client.newCall(requestBuilder.build()).execute();
        if (response.body() != null) {
            String responseString = ((ResponseBody) Objects.requireNonNull(response.body())).string();
            this.updateToken(responseString);
            return true;
        } else {
            return false;
        }
    }

    private void updateToken(String response) {

        try {
            String result = returnEmptyIfNull(response);
            JSONObject jsonResponseObject = new JSONObject(result);
            Log.d(TAG, "Response : " + jsonResponseObject);
            if (jsonResponseObject.has("access_token")) {
                String newToken = jsonResponseObject.getString("access_token");
                SharedPreferenceManager.Companion.setStringValue(AppConstants.ACCESS_TOKEN, newToken);
                Log.i(TAG, "Token Refresh status : success");
                Log.d(TAG, "new token : " + newToken);
            } else {
                Log.e(TAG, "Token Refresh status : failed");
            }
        } catch (Exception var9) {
            Log.e(TAG, "Exception : " + var9.getMessage());
        }

    }

    private String returnEmptyIfNull(String response) {
        if (response == null)
            return AppConstants.EMPTY_STRING;
        else
            return response;

    }
}

