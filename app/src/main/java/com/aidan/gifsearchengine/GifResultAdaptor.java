package com.aidan.gifsearchengine;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Thomas on 7/6/2018.
 */

public class GifResultAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    LayoutInflater layoutInflater;
    JSONObject jsonObject;
    Fragment fragment;

    public GifResultAdaptor(Context mContext, Fragment fragment) {
        this.mContext = mContext;
        layoutInflater = LayoutInflater.from(mContext);
        this.fragment = fragment;
    }

    public void setData(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gif_grid, parent, false);
        return new GifHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final GifHolder gifHolder = (GifHolder) holder;
        try {
            JSONObject gif = jsonObject.getJSONArray("data").getJSONObject(position);

            String url = gif.getJSONObject("images").getJSONObject("original").getString("url");

            Glide.with(mContext)
                    .load(url)
                    .into(gifHolder.iv_gif);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        try {
            if (jsonObject == null || jsonObject.getJSONArray("data").length() == 0) {
                return 0;
            } else {
                return jsonObject.getJSONArray("data").length();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    static class GifHolder extends RecyclerView.ViewHolder {
         ImageView iv_gif;


        public GifHolder(View itemView) {
            super(itemView);
            iv_gif = itemView.findViewById(R.id.iv_gif);
        }
    }

}
