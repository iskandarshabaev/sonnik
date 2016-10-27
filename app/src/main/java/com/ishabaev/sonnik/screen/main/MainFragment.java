package com.ishabaev.sonnik.screen.main;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.ishabaev.sonnik.R;
import com.ishabaev.sonnik.model.Article;
import com.ishabaev.sonnik.model.Category;
import com.ishabaev.sonnik.repository.RepositoryProvider;
import com.ishabaev.sonnik.screen.Navigator;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements MainContract.View {

    public static final String ARTICLES_KEY = "articles";

    private MainContract.Presenter mPresenter;
    private EditText mKeyET;
    private Button mSearchB;
    private RecyclerView mResultsRV;
    private ResultAdapter mResultAdapter;
    private Spinner mCategoryS;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        findViews(view);
        initCategorySpinner(new ArrayList<>());
        initSearchButton();
        initResultsRV();
        initPresenter(savedInstanceState);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.init();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mResultAdapter != null) {
            outState.putParcelableArrayList(ARTICLES_KEY,
                    (ArrayList<? extends Parcelable>) mResultAdapter.getResults());
        }
    }

    private void findViews(View view) {
        mKeyET = (EditText) view.findViewById(R.id.key);
        mSearchB = (Button) view.findViewById(R.id.search);
        mResultsRV = (RecyclerView) view.findViewById(R.id.results);
        mCategoryS = (Spinner) view.findViewById(R.id.category);
    }

    @Override
    public void initCategorySpinner(List<Category> categories) {
        CategoryAdapter adapter = new CategoryAdapter(getContext(), categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategoryS.setAdapter(adapter);
        mCategoryS.setSelection(0);
        mCategoryS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean initialized;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (initialized) {
                    mResultAdapter.clear();
                    mPresenter.category(categories.get(i).getId());
                } else {
                    initialized = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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

    private void initPresenter(@Nullable Bundle savedInstanceState) {
        mPresenter = new MainPresenter(this, RepositoryProvider.provideRepository());
        if (savedInstanceState == null) {
            mPresenter.loadRecentArticles();
        } else {
            List<Article> articles = savedInstanceState.getParcelableArrayList(ARTICLES_KEY);
            if (articles != null) {
                showResults(articles);
            } else {
                mPresenter.loadRecentArticles();
            }
        }
    }

    @Override
    public void showResults(@NonNull List<Article> results) {
        mResultAdapter.changeDataSet(results);
    }

    @Override
    public void addResults(@NonNull List<Article> results) {
        mResultAdapter.addResults(results);
    }
}
