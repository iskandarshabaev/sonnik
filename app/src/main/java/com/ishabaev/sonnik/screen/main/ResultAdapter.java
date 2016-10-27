package com.ishabaev.sonnik.screen.main;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ishabaev.sonnik.R;
import com.ishabaev.sonnik.model.Article;

import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultHolder> {

    private List<Article> mResults;
    private OnItemClickListener mOnItemClickListener;

    public ResultAdapter(@NonNull List<Article> results) {
        mResults = results;
    }

    public List<Article> getResults() {
        return mResults;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    private View.OnClickListener mOnClickListener = view -> {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onClick(view, (Article) view.getTag(R.id.text));
        }
    };

    public void changeDataSet(@NonNull List<Article> results) {
        mResults.clear();
        mResults.addAll(results);
        notifyDataSetChanged();
    }

    public void clear() {
        mResults.clear();
    }

    public void addResults(@NonNull List<Article> results) {
        mResults.addAll(results);
        notifyDataSetChanged();
    }

    @Override
    public ResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);
        view.setOnClickListener(mOnClickListener);
        return new ResultHolder(view);
    }

    @Override
    public void onBindViewHolder(ResultHolder holder, int position) {
        holder.bind(mResults.get(position));
        holder.getRootView().setTag(R.id.text, mResults.get(position));
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

    public interface OnItemClickListener {
        void onClick(@NonNull View v, @NonNull Article result);
    }
}
