package com.ishabaev.sonnik.screen.main;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ishabaev.sonnik.R;
import com.ishabaev.sonnik.model.Article;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ResultHolder extends RecyclerView.ViewHolder {

    private View mRootView;
    private TextView mTextTV;
    private TextView mDateTV;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    public ResultHolder(View view) {
        super(view);
        mRootView = view;
        findViews(view);
    }

    private void findViews(View view) {
        mTextTV = (TextView) view.findViewById(R.id.text);
        mDateTV = (TextView) view.findViewById(R.id.date);
    }

    public void bind(Article result) {
        mTextTV.setText(result.getTitle());
        if (result.getDate() == null) {
            mDateTV.setVisibility(View.GONE);
        } else {
            mDateTV.setText(dateFormat.format(result.getDate()));
        }
    }

    public View getRootView() {
        return mRootView;
    }
}
