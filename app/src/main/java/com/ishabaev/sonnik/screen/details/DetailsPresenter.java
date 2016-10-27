package com.ishabaev.sonnik.screen.details;


import android.support.annotation.NonNull;

import com.ishabaev.sonnik.repository.SonnikRepository;
import com.ishabaev.sonnik.util.RxSchedulers;

public class DetailsPresenter implements DetailsContract.Presenter {

    private DetailsContract.View mView;
    private SonnikRepository mRepository;

    public DetailsPresenter(@NonNull DetailsContract.View view,
                            @NonNull SonnikRepository repository) {
        mView = view;
        mRepository = repository;
    }

    @Override
    public void load(String article) {
        mRepository.getArticle(article)
                .compose(RxSchedulers.async())
                .subscribe(mView::showContent, Throwable::printStackTrace);
    }
}
