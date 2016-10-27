package com.ishabaev.sonnik.screen.main;


import android.support.annotation.NonNull;

import com.ishabaev.sonnik.model.Article;

import java.util.List;

public interface MainContract {

    interface Presenter {
        void search(String query);
    }

    interface View {

        void showResults(@NonNull List<Article> results);

    }

}
