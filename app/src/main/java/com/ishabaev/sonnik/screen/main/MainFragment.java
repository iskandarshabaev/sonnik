package com.ishabaev.sonnik.screen.main;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ishabaev.sonnik.R;
import com.ishabaev.sonnik.model.Article;
import com.ishabaev.sonnik.model.Category;
import com.ishabaev.sonnik.repository.RepositoryProvider;
import com.ishabaev.sonnik.screen.Navigator;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements MainContract.View {

    public static final String TAG = MainFragment.class.getName();

    public static final String ARTICLES_KEY = "articles";
    public static final String CATEGORY_KEY = "category";

    private MainContract.Presenter mPresenter;

    private TextView mTitleTV;
    private EditText mKeyET;
    private Button mSearchB;
    private RecyclerView mResultsRV;
    private ResultAdapter mResultAdapter;
    private Spinner mCategoryS;
    private ImageView mMenuIV;
    private ImageView mBackIV;
    private CategoryAdapter mCategoryAdapter;
    private CategoryMenuAdapter mCategoryMenuAdapter;
    private String mCategory;

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
        initToolbarButtons(savedInstanceState);
        initResultsRV();
        initPresenter(savedInstanceState);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
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
        mMenuIV = (ImageView) view.findViewById(R.id.menu);
        mBackIV = (ImageView) view.findViewById(R.id.back);
        mTitleTV = (TextView) view.findViewById(R.id.title);
    }

    private void initToolbarButtons(Bundle savedInstanceState) {
        mMenuIV.setOnClickListener((v) -> {
            showPopupMenu();
        });
        mBackIV.setOnClickListener(view -> {
            mBackIV.setVisibility(View.GONE);
            mPresenter.loadRecentArticles();
        });
        if (savedInstanceState == null) {
            mBackIV.setVisibility(View.GONE);
        }
    }

    private void showPopupMenu() {
        ListPopupWindow listPopupWindow = new ListPopupWindow(getContext());
        listPopupWindow.setAdapter(mCategoryMenuAdapter);
        listPopupWindow.setAnchorView(mMenuIV);
        listPopupWindow.setWidth(500);
        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener((adapterView, view1, i, l) -> {
            listPopupWindow.dismiss();
            if (i < mCategoryAdapter.getCategories().size()) {
                mCategoryS.setSelection(i);
            } else {
                mPresenter.loadRecentArticles();
            }

        });
        listPopupWindow.show();
    }

    @Override
    public void initCategorySpinner(List<Category> categories) {
        mCategoryAdapter = new CategoryAdapter(getContext(), categories);
        mCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategoryS.setAdapter(mCategoryAdapter);
        if (mCategory == null) {
            mCategoryS.setSelection(0);
        } else {
            int index = findCategoryIndex(mCategory, categories);
            if (index != -1) {
                mCategoryS.setSelection(index);
                mCategory = null;
            }
        }
        mCategoryS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean initialized;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (initialized) {
                    mBackIV.setVisibility(View.VISIBLE);
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
        mCategoryMenuAdapter = new CategoryMenuAdapter(getContext(), categories);
        mCategoryMenuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private int findCategoryIndex(@NonNull String id, List<Category> categories) {
        for (int i = 0; i < categories.size(); i++) {
            if (id.equals(categories.get(i).getId())) {
                return i;
            }
        }
        return -1;
    }

    private void initSearchButton() {
        mSearchB.setOnClickListener(v -> {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            mPresenter.search(mKeyET.getText().toString());
            mBackIV.setVisibility(View.VISIBLE);
        });
    }

    private void initResultsRV() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.ItemAnimator animator = new DefaultItemAnimator();
        mResultAdapter = new ResultAdapter(new ArrayList<>());
        mResultAdapter.setOnItemClickListener((v, result) -> {
            showDetails(result);
        });
        mResultsRV.setLayoutManager(layoutManager);
        mResultsRV.setItemAnimator(animator);
        mResultsRV.setAdapter(mResultAdapter);
    }

    private void initPresenter(@Nullable Bundle savedInstanceState) {
        mPresenter = new MainPresenter(this, RepositoryProvider.provideRepository());
        mPresenter.init();
        if (mCategory != null) {
            mBackIV.setVisibility(View.VISIBLE);
            mPresenter.category(mCategory);
            return;
        }
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

    public void setCategory(String id) {
        mCategory = id;
    }

    @Override
    public void showRecentResults(@NonNull List<Article> results) {
        mTitleTV.setText(R.string.recent_results);
        mResultAdapter.changeDataSet(results);
        mBackIV.setVisibility(View.GONE);
    }

    @Override
    public void showResults(@NonNull List<Article> results) {
        mTitleTV.setText(R.string.results);
        mResultAdapter.changeDataSet(results);
    }

    @Override
    public void addResults(@NonNull List<Article> results) {
        mTitleTV.setText(R.string.results);
        mResultAdapter.addResults(results);
    }

    @Override
    public void showNoInternetError() {
        Navigator.showError(getActivity(), getString(R.string.no_internet));
    }

    @Override
    public void showSomethingWrong() {
        Navigator.showError(getActivity(), getString(R.string.something_wrong));
    }

    @Override
    public void notFoundError() {
        Navigator.showError(getActivity(), getString(R.string.not_found));
    }

    @Override
    public void showDetails(@NonNull Article article) {
        Navigator.showDetailsFragment(getActivity(), article);
    }
}
