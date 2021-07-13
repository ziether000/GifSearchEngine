package com.aidan.gifsearchengine;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    EditText edt_search;
    RecyclerView rv_result;
    int limit = 25;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        edt_search = view.findViewById(R.id.edt_search);
        rv_result = view.findViewById(R.id.rv_result);
        init();
        return view;
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void init() {
        APIService.getInstance(getContext()).getTrending();

        rv_result.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    limit+=25;
                    APIService.getInstance(getContext()).searchGIF(edt_search.getText().toString(),limit);
                }
            }
        });
        edt_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        ((i == KeyEvent.KEYCODE_ENTER))) {
                    limit=25;
                    APIService.getInstance(getContext()).searchGIF(edt_search.getText().toString(),limit);
                    closeKeyboard();
                    return true;
                }
                return false;

            }
        });
    }

    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager manager
                    = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ResultEvent event) {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        rv_result.setLayoutManager(layoutManager);
        GifResultAdaptor gridAdapter = new GifResultAdaptor(getActivity(), this);
        gridAdapter.setData(event.jsonObject);
        rv_result.setAdapter(gridAdapter);
        rv_result.scrollToPosition(limit-25);
    }
}