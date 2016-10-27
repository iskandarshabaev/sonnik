package com.ishabaev.sonnik.screen.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;

import com.ishabaev.sonnik.R;
import com.ishabaev.sonnik.model.Article;
import com.ishabaev.sonnik.repository.RepositoryProvider;
import com.ishabaev.sonnik.screen.details.DetailsActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private MainContract.Presenter mPresenter;
    private EditText mKeyET;
    private Button mSearchB;
    private RecyclerView mResultsRV;
    private ResultAdapter mResultAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        initSearchButton();
        initResultsRV();
        initPresenter();
    }

    private void findViews() {
        mKeyET = (EditText) findViewById(R.id.key);
        mSearchB = (Button) findViewById(R.id.search);
        mResultsRV = (RecyclerView) findViewById(R.id.results);
    }

    private void initSearchButton() {
        mSearchB.setOnClickListener(v -> {
            mPresenter.search(mKeyET.getText().toString());
        });
    }

    private void initResultsRV() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator animator = new DefaultItemAnimator();
        mResultAdapter = new ResultAdapter(new ArrayList<>());
        mResultAdapter.setOnItemClickListener((v, result) ->
                DetailsActivity.showActivity(this, result));
        mResultsRV.setLayoutManager(layoutManager);
        mResultsRV.setItemAnimator(animator);
        mResultsRV.setAdapter(mResultAdapter);
    }

    private void initPresenter() {
        mPresenter = new MainPresenter(this, RepositoryProvider.provideRepository());
    }

    @Override
    public void showResults(@NonNull List<Article> results) {
        mResultAdapter.changeDataSet(results);
    }
}
