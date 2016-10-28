package com.ishabaev.sonnik.screen.details;


import android.support.annotation.NonNull;

import com.ishabaev.sonnik.model.Article;
import com.ishabaev.sonnik.model.Category;

import java.util.List;

public interface DetailsContract {

    interface Presenter {

        void init();

        void load(String query);

    }

    interface View {

        void showContent(@NonNull Article article);

        void initCategorySpinner(List<Category> categories);

        void showNoInternetError();

        void showSomethingWrong();
    }

}
