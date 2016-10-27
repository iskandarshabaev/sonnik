package com.ishabaev.sonnik.repository;


import android.support.annotation.NonNull;

import com.ishabaev.sonnik.api.ApiFactory;
import com.ishabaev.sonnik.model.Article;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import rx.Observable;

import static com.ishabaev.sonnik.api.Api.COF;
import static com.ishabaev.sonnik.api.Api.CX;
import static com.ishabaev.sonnik.api.Api.IE;

public class DefaultSonnikRepository implements SonnikRepository {

    @Override
    public Observable<List<Article>> searchArticle(String query) {
        return ApiFactory.getApi().search(query, IE, COF, query, CX)
                .flatMap(response ->
                        Observable.fromCallable(() -> parseSearchResults(response.body().string())))
                .flatMap(articles -> Observable.just(saveAndLoadArticles(articles)));
    }

    private List<Article> saveAndLoadArticles(List<Article> articles) {
        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            Article localArticle = Realm.getDefaultInstance()
                    .where(Article.class)
                    .equalTo("mId", article.getId())
                    .findFirst();

            if (localArticle != null) {
                localArticle = Realm.getDefaultInstance().copyFromRealm(localArticle);
                if(localArticle.getDate() != null){
                    articles.set(i, localArticle);
                }
            }else {
                Realm.getDefaultInstance().executeTransaction(realm -> realm.insertOrUpdate(article));
            }
        }
        return articles;
    }

    private List<Article> parseSearchResults(String html) {
        List<Article> results = new ArrayList<>();
        Pattern titleFinder = Pattern.compile("<a[^>]*?href\\s*=\\s*(('|\")(/articles/(.*?).html)('|\"))[^>]*?(?!/)>(.*?)</a>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher regexMatcher = titleFinder.matcher(html);
        while (regexMatcher.find()) {
            results.add(new Article(regexMatcher.group(4), regexMatcher.group(6)));
            System.out.println(regexMatcher.group());
        }
        return results;
    }

    @Override
    public Observable<Article> getArticle(String id) {
        return Observable.fromCallable(() -> loadArticleLocal(id))
                .onErrorResumeNext(error -> {
                    return ApiFactory.getApi().article(id)
                            .flatMap(response ->
                                    Observable.fromCallable(() -> parseArticle(id, response.body().string())))
                            .doOnNext(article -> Realm.getDefaultInstance()
                                    .executeTransaction(realm -> realm.insertOrUpdate(article)));

                });

    }

    private Article loadArticleLocal(String id) throws Exception {
        Article article = Realm.getDefaultInstance()
                .where(Article.class)
                .equalTo("mId", id)
                .findFirst();

        if (article.getText() != null) {
            return Realm.getDefaultInstance().copyFromRealm(article);
        } else {
            throw new Exception("");
        }
    }

    private Article parseArticle(@NonNull String id, @NonNull String html) {
        Article article = new Article();
        article.setId(id);
        article.setDate(new Date(System.currentTimeMillis()));
        Pattern titlePattern = Pattern.compile("<h1 class=\"hr\" title=\".*?\">(.*?)</h1>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Pattern articlePattern = Pattern.compile("<div[^>]*?id\\s*=\\s*\"hypercontext\"><index>\\s*<p>(.*?)</p>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

        Matcher regexMatcher = titlePattern.matcher(html);
        while (regexMatcher.find()) {
            article.setTitle(regexMatcher.group(1));
        }

        regexMatcher = articlePattern.matcher(html);
        while (regexMatcher.find()) {
            article.setText(regexMatcher.group(1));
        }

        article.setDate(new Date(System.currentTimeMillis()));

        return article;
    }
}
