package com.ishabaev.sonnik.screen.main;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ishabaev.sonnik.R;
import com.ishabaev.sonnik.model.Article;

public class ResultHolder extends RecyclerView.ViewHolder {

    private View mRootView;
    private TextView mTextTV;

    public ResultHolder(View view) {
        super(view);
        mRootView = view;
        findViews(view);
    }

    private void findViews(View view) {
        mTextTV = (TextView) view.findViewById(R.id.text);
    }

    public void bind(Article result) {
        mTextTV.setText(result.getTitle());
    }

    public View getRootView() {
        return mRootView;
    }
}
