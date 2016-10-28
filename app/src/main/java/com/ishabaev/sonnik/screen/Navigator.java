package com.ishabaev.sonnik.screen;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.ishabaev.sonnik.R;
import com.ishabaev.sonnik.model.Article;
import com.ishabaev.sonnik.screen.details.DetailsFragment;
import com.ishabaev.sonnik.screen.error.ErrorFragment;
import com.ishabaev.sonnik.screen.main.MainFragment;

import static com.ishabaev.sonnik.screen.error.ErrorFragment.ERROR_MESSAGE_KEY;

public class Navigator {

    public static void showMainFragment(@NonNull FragmentActivity activity) {
        MainFragment fragment = MainFragment.newInstance();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragment, MainFragment.TAG)
                .commit();
    }

    public static void onBack(@NonNull FragmentActivity activity) {
        activity.getSupportFragmentManager().popBackStack();
    }

    public static void showMainFragmentCategory(@NonNull FragmentActivity activity, String id) {
        activity.getSupportFragmentManager().popBackStack();
        MainFragment fragment = (MainFragment) activity.getSupportFragmentManager()
                .findFragmentByTag(MainFragment.TAG);
        if (fragment != null) {
            fragment.setCategory(id);
        }
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

    public static void showError(@NonNull FragmentActivity activity, @NonNull String message) {
        Bundle args = new Bundle();
        args.putString(ERROR_MESSAGE_KEY, message);
        ErrorFragment fragment = ErrorFragment.newInstance();
        fragment.setArguments(args);
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

}
