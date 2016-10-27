package com.ishabaev.sonnik.screen.main;


import android.support.annotation.NonNull;

import com.ishabaev.sonnik.repository.SonnikRepository;
import com.ishabaev.sonnik.util.RxSchedulers;

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View mView;
    private SonnikRepository mRepository;

    public MainPresenter(@NonNull MainContract.View view, @NonNull SonnikRepository repository) {
        mView = view;
        mRepository = repository;
    }

    @Override
    public void init() {
        mRepository.getCategories()
                .compose(RxSchedulers.async())
                .subscribe(
                        results -> {
                            mView.initCategorySpinner(results);
                        }, this::showError
                );
    }

    @Override
    public void search(String query) {
        mRepository.searchArticle(query)
                .compose(RxSchedulers.async())
                .subscribe(response -> {
                    mView.showResults(response);
                }, this::showError);
    }

    @Override
    public void category(String query) {
        mRepository.getCategory(query)
                .compose(RxSchedulers.async())
                .subscribe(mView::addResults, this::showError);
    }

    @Override
    public void loadRecentArticles() {
        mRepository.loadRecentArticles()
                .subscribe(mView::showResults, this::showError);
    }

    private void showError(Throwable throwable) {
        throwable.printStackTrace();
    }
}
