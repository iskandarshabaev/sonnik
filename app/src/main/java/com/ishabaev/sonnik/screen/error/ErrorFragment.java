package com.ishabaev.sonnik.screen.error;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ishabaev.sonnik.R;
import com.ishabaev.sonnik.model.Category;
import com.ishabaev.sonnik.repository.RepositoryProvider;
import com.ishabaev.sonnik.screen.MainActivity;
import com.ishabaev.sonnik.screen.Navigator;
import com.ishabaev.sonnik.screen.main.CategoryMenuAdapter;
import com.ishabaev.sonnik.util.RxSchedulers;

import java.util.List;

public class ErrorFragment extends Fragment {

    public static final String ERROR_MESSAGE_KEY = "error_message";

    public static ErrorFragment newInstance() {
        return new ErrorFragment();
    }

    private TextView mErrorMessageTV;
    private Button mBackB;
    private ImageView mMenuIV;
    private CategoryMenuAdapter mCategoryMenuAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_error, container, false);
        String message = getArguments().getString(ERROR_MESSAGE_KEY,
                getString(R.string.something_wrong));
        findViews(view);
        mErrorMessageTV.setText(message);
        showNotification(message);
        initBackButton();
        initToolbarButtons();
        return view;
    }

    private void initToolbarButtons() {
        mMenuIV.setOnClickListener((v) -> {
            showPopupMenu();
        });
        RepositoryProvider.provideRepository().getCategories()
                .compose(RxSchedulers.async())
                .subscribe(this::initCategorySpinner, Throwable::printStackTrace);
    }

    public void initCategorySpinner(List<Category> categories) {
        mCategoryMenuAdapter = new CategoryMenuAdapter(getContext(), categories);
        mCategoryMenuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void showPopupMenu() {
        ListPopupWindow listPopupWindow = new ListPopupWindow(getContext());
        listPopupWindow.setAdapter(mCategoryMenuAdapter);
        listPopupWindow.setAnchorView(mMenuIV);
        listPopupWindow.setWidth(500);
        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener((adapterView, view1, i, l) -> {
            listPopupWindow.dismiss();
            if (i < mCategoryMenuAdapter.getCategories().size()) {
                String id = mCategoryMenuAdapter.getCategories().get(i).getId();
                Navigator.showMainFragmentCategory(getActivity(), id);
            } else {
                Navigator.onBack(getActivity());
            }

        });
        listPopupWindow.show();
    }

    private void findViews(View view) {
        mErrorMessageTV = (TextView) view.findViewById(R.id.error_message);
        mBackB = (Button) view.findViewById(R.id.back);
        mMenuIV = (ImageView) view.findViewById(R.id.menu);
    }

    private void initBackButton() {
        mBackB.setOnClickListener(view -> Navigator.onBack(getActivity()));
    }

    private void showNotification(String message) {
        Intent notificationIntent = new Intent(getContext(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getContext(),
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Uri path = Uri.parse("android.resource://com.ishabaev.sonnik/raw/sound");

        Resources res = getContext().getResources();
        Notification.Builder builder = new Notification.Builder(getContext());
        long[] pattern = {250, 500};
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setSound(path)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setContentTitle(getString(R.string.error))
                .setContentText(message);

        // Notification notification = builder.getNotification(); // до API 16
        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) getContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}
