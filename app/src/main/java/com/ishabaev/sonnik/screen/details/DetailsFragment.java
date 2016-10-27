package com.ishabaev.sonnik.screen.details;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ishabaev.sonnik.R;
import com.ishabaev.sonnik.model.Article;
import com.ishabaev.sonnik.repository.RepositoryProvider;

public class DetailsFragment extends Fragment implements DetailsContract.View {

    public static final String SEARCH_RESULT_KEY = "search_result";

    private TextView mTitleTV;
    private TextView mContentTV;
    private DetailsContract.Presenter mPresenter;

    public static DetailsFragment newInstance(@NonNull Article result) {
        Bundle args = new Bundle();
        args.putParcelable(SEARCH_RESULT_KEY, result);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        findViews(view);
        initPresenter();
        Article result = getArguments().getParcelable(SEARCH_RESULT_KEY);
        if (result != null) {
            loadContent(result);
        }
        return view;
    }

    private void findViews(View view) {
        mContentTV = (TextView) view.findViewById(R.id.content);
        mTitleTV = (TextView) view.findViewById(R.id.title);
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
        mTitleTV.setText(article.getTitle());
    }
}
