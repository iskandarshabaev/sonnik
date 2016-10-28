package com.ishabaev.sonnik.screen.main;


import android.support.annotation.NonNull;

import com.ishabaev.sonnik.model.Article;
import com.ishabaev.sonnik.repository.SonnikRepository;
import com.ishabaev.sonnik.util.RxSchedulers;

import java.net.UnknownHostException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;

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
                .flatMap(response -> {
                    if (response.raw().request().url().toString().contains("articles")) {
                        String id = getArticleIdFromUrl(response.raw().request().url().toString());
                        return mRepository.getArticle(id);
                    } else {
                        return Observable.fromCallable(() -> mRepository.parseSearchResults(response.body().string()))
                                .flatMap(articles -> Observable.just(mRepository.saveAndLoadArticles(articles)));
                    }
                })
                .compose(RxSchedulers.async())
                .subscribe(response -> {
                    if (response instanceof List) {
                        if (((List) response).size() > 0) {
                            mView.showResults(((List<Article>) response));
                        } else {
                            mView.notFoundError();
                        }
                    } else if (response instanceof Article) {
                        mView.showDetails((Article) response);
                    }
                }, this::showError);
    }

    private String getArticleIdFromUrl(String url) {
        String id = "";
        Pattern idFinder = Pattern.compile(".*articles/.*?(.*?).html", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher regexMatcher = idFinder.matcher(url);
        while (regexMatcher.find()) {
            id = regexMatcher.group(1);
        }
        return id;
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
                .subscribe(mView::showRecentResults, this::showError);
    }

    private void showError(Throwable throwable) {
        throwable.printStackTrace();
        if (throwable instanceof UnknownHostException) {
            mView.showNoInternetError();
        } else {
            mView.showSomethingWrong();
        }
    }
}
