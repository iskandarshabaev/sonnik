package com.ishabaev.sonnik.screen.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.ishabaev.sonnik.R;
import com.ishabaev.sonnik.model.Article;
import com.ishabaev.sonnik.repository.RepositoryProvider;
import com.ishabaev.sonnik.screen.Navigator;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements MainContract.View {

    private MainContract.Presenter mPresenter;
    private EditText mKeyET;
    private Button mSearchB;
    private RecyclerView mResultsRV;
    private ResultAdapter mResultAdapter;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        findViews(view);
        initSearchButton();
        initResultsRV();
        initPresenter();
        return view;
    }

    private void findViews(View view) {
        mKeyET = (EditText) view.findViewById(R.id.key);
        mSearchB = (Button) view.findViewById(R.id.search);
        mResultsRV = (RecyclerView) view.findViewById(R.id.results);
    }

    private void initSearchButton() {
        mSearchB.setOnClickListener(v -> {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            mPresenter.search(mKeyET.getText().toString());
        });
    }

    private void initResultsRV() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.ItemAnimator animator = new DefaultItemAnimator();
        mResultAdapter = new ResultAdapter(new ArrayList<>());
        mResultAdapter.setOnItemClickListener((v, result) -> {
            Navigator.showDetailsFragment(getActivity(), result);
        });
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
