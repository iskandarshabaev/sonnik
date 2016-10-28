package com.ishabaev.sonnik.screen.details;


import android.support.annotation.NonNull;

import com.ishabaev.sonnik.repository.SonnikRepository;
import com.ishabaev.sonnik.util.RxSchedulers;

import java.net.UnknownHostException;

public class DetailsPresenter implements DetailsContract.Presenter {

    private DetailsContract.View mView;
    private SonnikRepository mRepository;

    public DetailsPresenter(@NonNull DetailsContract.View view,
                            @NonNull SonnikRepository repository) {
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
    public void load(String article) {
        mView.setProgressVisibility(true);
        mRepository.getArticle(article)
                .compose(RxSchedulers.async())
                .subscribe(result -> {
                            mView.setProgressVisibility(false);
                            mView.showContent(result);
                        }, Throwable::printStackTrace
                );
    }

    private void showError(Throwable throwable) {
        throwable.printStackTrace();
        mView.setProgressVisibility(false);
        if (throwable instanceof UnknownHostException) {
            mView.showNoInternetError();
        } else {
            mView.showSomethingWrong();
        }
    }

}
