package com.aidan.gifsearchengine;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyFavoriteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyFavoriteFragment extends Fragment {

    RecyclerView rv_favorites;
    public MyFavoriteFragment() {
        // Required empty public constructor
    }

    public static MyFavoriteFragment newInstance() {
        MyFavoriteFragment fragment = new MyFavoriteFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View view = inflater.inflate(R.layout.fragment_my_favorite, container, false);
        rv_favorites = view.findViewById(R.id.rv_favorites);
        return view;
    }
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            init();
        } else {

        }
    }

    public void init() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("favorite_list", getActivity().MODE_PRIVATE);
        Set<String> list = sharedPreferences.getStringSet("favorite_list", null);
        if (list.size()!=0){
            String ids = "";
            for (String id: list) {
                ids += id+",";
            }
            APIService.getInstance(getContext()).getGIFsById(ids);
        }else{
            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
            rv_favorites.setLayoutManager(layoutManager);
            GifFavoritesAdaptor gridAdapter = new GifFavoritesAdaptor(getActivity(), this);
            rv_favorites.setAdapter(gridAdapter);
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FavoritesEvent event) {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        rv_favorites.setLayoutManager(layoutManager);
        GifFavoritesAdaptor gridAdapter = new GifFavoritesAdaptor(getActivity(), this);
        gridAdapter.setData(event.jsonObject);
        rv_favorites.setAdapter(gridAdapter);
    }
}