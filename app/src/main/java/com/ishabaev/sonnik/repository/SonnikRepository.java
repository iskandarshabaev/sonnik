package com.ishabaev.sonnik.repository;


import com.ishabaev.sonnik.model.Article;
import com.ishabaev.sonnik.model.Category;

import java.util.List;

import rx.Observable;

public interface SonnikRepository {

    Observable<List<Category>> getCategories();

    Observable<List<Article>> searchArticle(String query);

    Observable<Article> getArticle(String id);

    Observable<List<Article>> getCategory(String id);

    Observable<List<Article>> loadRecentArticles();

}
