package com.ishabaev.sonnik.screen.details;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.ishabaev.sonnik.R;
import com.ishabaev.sonnik.model.Article;
import com.ishabaev.sonnik.repository.RepositoryProvider;

public class DetailsActivity extends AppCompatActivity implements DetailsContract.View {

    public static final String SEARCH_RESULT_KEY = "search_result";

    private TextView mContentTV;
    private DetailsContract.Presenter mPresenter;

    public static void showActivity(Activity activity, Article result) {
        Intent intent = new Intent(activity, DetailsActivity.class);
        intent.putExtra(SEARCH_RESULT_KEY, result);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        findViews();
        initPresenter();
        Article result = (Article) getIntent().getSerializableExtra(SEARCH_RESULT_KEY);
        if (result != null) {
            loadContent(result);
        }
    }

    private void findViews() {
        mContentTV = (TextView) findViewById(R.id.content);
    }

    private void initPresenter() {
        mPresenter = new DetailsPresenter(this, RepositoryProvider.provideRepository());
    }

    private void loadContent(Article result) {
        mPresenter.load(result.getId());
    }

    @Override
    public void showContent(@NonNull Article article) {
        mContentTV.setText(article.getText());
    }
}
