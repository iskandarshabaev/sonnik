package com.ishabaev.sonnik.repository;


import com.ishabaev.sonnik.model.Article;
import com.ishabaev.sonnik.model.Category;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;

public interface SonnikRepository {

    Observable<List<Category>> getCategories();

    Observable<Response<ResponseBody>> searchArticle(String query);

    Observable<Article> getArticle(String id);

    Observable<List<Article>> getCategory(String id);

    Observable<List<Article>> loadRecentArticles();

    List<Article> parseSearchResults(String html);

    List<Article> saveAndLoadArticles(List<Article> articles);

}
