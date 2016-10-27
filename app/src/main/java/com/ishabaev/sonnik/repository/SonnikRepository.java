package com.ishabaev.sonnik.repository;


import com.ishabaev.sonnik.model.Article;

import java.util.List;

import rx.Observable;

public interface SonnikRepository {

    Observable<List<Article>> searchArticle(String query);

    Observable<Article> getArticle(String id);

}
