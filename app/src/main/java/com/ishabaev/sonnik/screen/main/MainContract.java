package com.ishabaev.sonnik.screen.main;


import android.support.annotation.NonNull;

import com.ishabaev.sonnik.model.Article;
import com.ishabaev.sonnik.model.Category;

import java.util.List;

public interface MainContract {

    interface Presenter {

        void init();

        void search(String query);

        void category(String query);

        void loadRecentArticles();

    }

    interface View {

        void showResults(@NonNull List<Article> results);

        void addResults(@NonNull List<Article> results);

        void initCategorySpinner(List<Category> categories);
    }

}
