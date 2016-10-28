package com.ishabaev.sonnik.repository;


import android.support.annotation.NonNull;

import com.ishabaev.sonnik.api.ApiFactory;
import com.ishabaev.sonnik.model.Article;
import com.ishabaev.sonnik.model.Category;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.Sort;
import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;

import static com.ishabaev.sonnik.api.Api.COF;
import static com.ishabaev.sonnik.api.Api.CX;
import static com.ishabaev.sonnik.api.Api.IE;

public class DefaultSonnikRepository implements SonnikRepository {

    @Override
    public Observable<List<Article>> getCategory(String id) {
        return ApiFactory.getApi().category(id + "_0")
                .flatMap(response ->
                        Observable.fromCallable(() -> parseCategory(response.body().string())))
                .flatMap(categories -> Observable.merge(makeCategoryList(categories)))
                .flatMap(Observable::from)
                .toList()
                .flatMap(articles -> Observable.fromCallable(() -> saveAndLoadArticles(articles)));
    }

    private List<Observable<List<Article>>> makeCategoryList(List<String> categoryIds) {
        List<Observable<List<Article>>> categories = new ArrayList<>();
        for (String category : categoryIds) {
            categories.add(getArticlesFromCategory(category));
        }
        return categories;
    }

    private Observable<List<Article>> getArticlesFromCategory(String id) {
        return ApiFactory.getApi().category(id)
                .flatMap(response ->
                        Observable.fromCallable(() -> parseSearchResults(response.body().string())));
    }

    private List<String> parseCategory(String html) {
        List<String> categories = new ArrayList<>();
        Pattern titleFinder = Pattern.compile("<li.*?><a href=\"/themes/theme(.*?).html\">.*?</a></li>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher regexMatcher = titleFinder.matcher(html);
        while (regexMatcher.find()) {
            categories.add(regexMatcher.group(1));
        }
        return categories;
    }

    @Override
    public Observable<List<Category>> getCategories() {
        return Observable.fromCallable(this::getOfflineCategories)
                .flatMap(categories -> {
                    if (categories.size() == 0) {
                        return getCategoriesRemote();
                    } else {
                        return Observable.just(categories);
                    }
                });
    }

    private Observable<List<Category>> getCategoriesRemote() {
        return ApiFactory.getApi().categories()
                .flatMap(response ->
                        Observable.fromCallable(() ->
                                parseCategories(response.body().string())))
                .doOnNext(categories -> Realm.getDefaultInstance().executeTransaction(realm -> {
                    realm.insertOrUpdate(categories);
                }));
    }

    private List<Category> getOfflineCategories() {
        List<Category> categories = Realm.getDefaultInstance().where(Category.class)
                .findAll();
        return Realm.getDefaultInstance().copyFromRealm(categories);
    }

    private List<Category> parseCategories(String html) {
        List<Category> categories = new ArrayList<>();
        Pattern cateoryPattern = Pattern.compile("<option value=\"(.*?)\">(.*?)</option>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher regexMatcher = cateoryPattern.matcher(html);
        while (regexMatcher.find()) {
            Category category = new Category(regexMatcher.group(1), regexMatcher.group(2));
            categories.add(category);
        }
        return categories;
    }

    @Override
    public Observable<Response<ResponseBody>> searchArticle(String query) {
        return ApiFactory.getApi().search(query, IE, COF, query, CX);
    }

    @Override
    public List<Article> saveAndLoadArticles(List<Article> articles) {
        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            Article localArticle = Realm.getDefaultInstance()
                    .where(Article.class)
                    .equalTo("mId", article.getId())
                    .findFirst();

            if (localArticle != null) {
                localArticle = Realm.getDefaultInstance().copyFromRealm(localArticle);
                if (localArticle.getDate() != null) {
                    articles.set(i, localArticle);
                }
            } else {
                Realm.getDefaultInstance().executeTransaction(realm -> realm.insertOrUpdate(article));
            }
        }
        return articles;
    }

    @Override
    public List<Article> parseSearchResults(String html) {
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
                                    Observable.fromCallable(() -> parseArticle(id, response.body().string())));
                })
                .doOnNext(article -> {
                    article.setDate(new Date(System.currentTimeMillis()));
                    Realm.getDefaultInstance()
                            .executeTransaction(realm -> realm.insertOrUpdate(article));
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

    @Override
    public Observable<List<Article>> loadRecentArticles() {
        List<Article> articles = Realm.getDefaultInstance().where(Article.class)
                .isNotNull("mDate")
                .findAll()
                .sort("mDate", Sort.DESCENDING);
        return Observable.fromCallable(() -> Realm.getDefaultInstance().copyFromRealm(articles));
    }
}
