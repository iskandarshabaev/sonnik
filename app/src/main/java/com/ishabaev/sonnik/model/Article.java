package com.ishabaev.sonnik.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Article extends RealmObject implements Parcelable {

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel parcel) {
            return new Article(parcel);
        }

        @Override
        public Article[] newArray(int i) {
            return new Article[0];
        }
    };

    @PrimaryKey
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

    public Article(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mText = in.readString();
        mDate = new Date(in.readLong());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeString(mTitle);
        parcel.writeString(mText);
        parcel.writeLong(mDate.getTime());

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
