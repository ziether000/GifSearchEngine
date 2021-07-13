package com.aidan.gifsearchengine;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

public class APIService {
    private static APIService apiService;
    final String API_KEY = "CPsrnq9WrniTYvUC0JZUzMvGoe2GcJyF";
    String domain = "https://api.giphy.com/v1/gifs";

    Context context;
    JSONObject result;

    private APIService(Context context) {
        this.context = context;
    }

    public static APIService getInstance(Context context) {
        if (apiService == null){
            apiService = new APIService(context);
        }
        return apiService;
    }

    public void apiCall(String url){
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        EventBus.getDefault().post(new ResponseEvent(response));
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error: ",error.getMessage());
                    }
                });
        queue.add(jsonObjectRequest);

    }

    public void getTrending() {
        String url = domain+"/trending?api_key="+API_KEY;
        apiCall(url);
    }

    public void searchGIF(String keyword) {
        String url = "";
        apiCall(url);
    }
}
