package com.ishabaev.sonnik.screen.main;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ishabaev.sonnik.R;
import com.ishabaev.sonnik.model.Category;

import java.util.List;

public class CategoryMenuAdapter extends ArrayAdapter<Category> {

    private LayoutInflater mInflater;
    private List<Category> mCategories;

    public CategoryMenuAdapter(Context context, List<Category> categories) {
        super(context, R.layout.item_result, categories);
        mCategories = categories;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.item_category, parent, false);
        TextView textView = (TextView) view.findViewById(R.id.category_name);
        textView.setText(mCategories.get(position).getName());
        return view;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (position == mCategories.size()) {
            return mInflater.inflate(R.layout.item_home, parent, false);
        }
        View view = mInflater.inflate(R.layout.item_category, parent, false);
        TextView textView = (TextView) view.findViewById(R.id.category_name);
        textView.setText(mCategories.get(position).getName());
        return view;
    }

    public List<Category> getCategories() {
        return mCategories;
    }

    @Override
    public int getCount() {
        return super.getCount() + 1;
    }
}
