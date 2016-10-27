package com.ishabaev.sonnik.screen;


import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.ishabaev.sonnik.R;
import com.ishabaev.sonnik.model.Article;
import com.ishabaev.sonnik.screen.details.DetailsFragment;
import com.ishabaev.sonnik.screen.main.MainFragment;

public class Navigator {

    public static void showMainFragment(@NonNull FragmentActivity activity) {
        MainFragment fragment = MainFragment.newInstance();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragment)
                .commit();
    }

    public static void showDetailsFragment(@NonNull FragmentActivity activity,
                                           @NonNull Article article) {
        DetailsFragment fragment = DetailsFragment.newInstance(article);
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

}
