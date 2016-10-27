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
    public void search(String query) {
        mRepository.searchArticle(query)
                .compose(RxSchedulers.async())
                .subscribe(response -> {
                    mView.showResults(response);
                }, this::showError);
    }

    private void showError(Throwable throwable) {
        throwable.printStackTrace();
    }
}
