package com.ishabaev.sonnik.screen.details;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ishabaev.sonnik.R;
import com.ishabaev.sonnik.model.Article;
import com.ishabaev.sonnik.model.Category;
import com.ishabaev.sonnik.repository.RepositoryProvider;
import com.ishabaev.sonnik.screen.Navigator;
import com.ishabaev.sonnik.screen.main.CategoryMenuAdapter;

import java.util.List;

public class DetailsFragment extends Fragment implements DetailsContract.View {

    public static final String SEARCH_RESULT_KEY = "search_result";

    private TextView mTitleTV;
    private TextView mContentTV;
    private DetailsContract.Presenter mPresenter;
    private ImageView mMenuIV;
    private CategoryMenuAdapter mCategoryMenuAdapter;

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
        initToolbarButtons();
        initPresenter();
        Article result = getArguments().getParcelable(SEARCH_RESULT_KEY);
        if (result != null) {
            loadContent(result);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.init();
    }

    private void findViews(View view) {
        mContentTV = (TextView) view.findViewById(R.id.content);
        mTitleTV = (TextView) view.findViewById(R.id.title);
        mMenuIV = (ImageView) view.findViewById(R.id.menu);
    }

    private void initToolbarButtons() {
        mMenuIV.setOnClickListener((v) -> {
            showPopupMenu();
        });
    }

    private void showPopupMenu() {
        ListPopupWindow listPopupWindow = new ListPopupWindow(getContext());
        listPopupWindow.setAdapter(mCategoryMenuAdapter);
        listPopupWindow.setAnchorView(mMenuIV);
        listPopupWindow.setWidth(500);
        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener((adapterView, view1, i, l) -> {
            listPopupWindow.dismiss();
            if (i < mCategoryMenuAdapter.getCategories().size()) {
                String id = mCategoryMenuAdapter.getCategories().get(i).getId();
                Navigator.showMainFragmentCategory(getActivity(), id);
            } else {
                Navigator.onBack(getActivity());
            }

        });
        listPopupWindow.show();
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

    @Override
    public void initCategorySpinner(List<Category> categories) {
        mCategoryMenuAdapter = new CategoryMenuAdapter(getContext(), categories);
        mCategoryMenuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public void showNoInternetError() {
        Navigator.showError(getActivity(), getString(R.string.no_internet));
    }

    @Override
    public void showSomethingWrong() {
        Navigator.showError(getActivity(), getString(R.string.something_wrong));
    }
}
