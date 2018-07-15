package me.deepanshusinha.popularmovies.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import me.deepanshusinha.popularmovies.R;
import me.deepanshusinha.popularmovies.fragment.MovieFragment.OnListFragmentInteractionListener;
import me.deepanshusinha.popularmovies.model.Movie;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Movie} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyMovieRecyclerViewAdapter extends RecyclerView.Adapter<MyMovieRecyclerViewAdapter.ViewHolder> {

    private final List<Movie> mMoviesList;
    private final OnListFragmentInteractionListener mListener;

    public MyMovieRecyclerViewAdapter(List<Movie> items, OnListFragmentInteractionListener listener) {
        mMoviesList = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = mMoviesList.get(position);
        holder.mName.setText(holder.mItem.getTitle());
        Glide.with(holder.mPoster.getContext())
                .load(holder.mItem.getPosterUri())
                .into(holder.mPoster);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMoviesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mName;
        final ImageView mPoster;
        private Movie mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mName = view.findViewById(R.id.title);
            mPoster = view.findViewById(R.id.posterIv);
        }
    }

    public void clearRecyclerViewData() {
        int size = mMoviesList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                mMoviesList.remove(0);
            }
            notifyItemRangeRemoved(0, size);
        }
    }
}
