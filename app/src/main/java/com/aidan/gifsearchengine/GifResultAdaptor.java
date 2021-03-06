package com.aidan.gifsearchengine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
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

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Thomas on 7/6/2018.
 */

public class GifResultAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Activity activity;
    LayoutInflater layoutInflater;
    JSONObject jsonObject;
    Fragment fragment;

    public GifResultAdaptor(Activity activity, Fragment fragment) {
        this.activity = activity;
        layoutInflater = LayoutInflater.from(activity);
        this.fragment = fragment;
        notifyDataSetChanged();
    }

    public void setData(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        notifyDataSetChanged();
    }
    public void addData(JSONObject jsonObject) {
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
            final JSONObject gif = jsonObject.getJSONArray("data").getJSONObject(position);

            String url = gif.getJSONObject("images").getJSONObject("original").getString("url");

            Glide.with(activity)
                    .load(url)
                    .into(gifHolder.iv_gif);

            gifHolder.iv_gif.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showListDialog(gif);
                }
            });

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

    private void showListDialog(final JSONObject gif) {
        final String[] items = {"Add to favorite", "Share with friends"};
        AlertDialog.Builder listDialog = new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_DARK);
        listDialog.setTitle("Action");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        SharedPreferences sharedPreferences = activity.getSharedPreferences("favorite_list", activity.MODE_PRIVATE);

                        Set<String> list = sharedPreferences.getStringSet("favorite_list", null);
                        try {
                            if (list!=null)
                                list.add(gif.getString("id"));
                            else{
                                list = new HashSet<>();
                                list.add(gif.getString("id"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putStringSet("favorite_list", list);
                        editor.commit();
                        list = sharedPreferences.getStringSet("favorite_list", null);
                        Log.d("qqqqq", list.toString());
                        break;

                    case 1:
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        try {
                            sendIntent.putExtra(Intent.EXTRA_TEXT, gif.getString("url"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        activity.startActivity(shareIntent);
                        break;

                    default:
                        break;
                }
            }
        });
        listDialog.show();
    }
}

