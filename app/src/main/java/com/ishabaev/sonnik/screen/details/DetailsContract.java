package com.ishabaev.sonnik.screen.details;


import android.support.annotation.NonNull;

import com.ishabaev.sonnik.model.Article;

public interface DetailsContract {

    interface Presenter {
        void load(String query);
    }

    interface View {

        void showContent(@NonNull Article article);

    }

}
