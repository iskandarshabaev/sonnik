package com.ishabaev.sonnik.model;


import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;

public class Article extends RealmObject implements Serializable {

    private String mId;
    private String mTitle;
    private String mText;
    private Date mDate;

    public Article() {
    }

    public Article(@NonNull String id, @NonNull String title) {
        mId = id;
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }
}
