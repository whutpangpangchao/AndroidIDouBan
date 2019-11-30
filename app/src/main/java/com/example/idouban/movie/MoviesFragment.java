package com.example.idouban.movie;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.example.idouban.HomeActivity;
import com.example.idouban.R;
import com.example.idouban.base.BaseFragment;
import com.example.idouban.base.BaseRecycleViewAdapter;
import com.example.idouban.base.BaseRecycleViewHolder;
import com.example.idouban.beans.Movie;
import com.example.idouban.moviedetail.MovieDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class MoviesFragment extends BaseFragment<Movie> implements MoviesContract.View {
    private static final String TAG = MoviesFragment.class.getSimpleName();
    private MoviesContract.Presenter mPresenter;
    private View mNoMoviesView;
    private SwipeToLoadLayout mSwipeToLoadLayout;

    public MoviesFragment() {
        // Required empty public constructor
    }

    public static MoviesFragment newInstance() {
        Log.e(HomeActivity.TAG, "MoviesFragment newInstance!");
        return new MoviesFragment();
    }

    @Override
    protected void initVariables() {
        Log.e(TAG, TAG + "onCreate() -> initVariables");
        mAdapterData = new ArrayList<>();

    }

    @Override
    protected void initRecycleView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_movies, container, false);
        mSwipeToLoadLayout = mView.findViewById(R.id.swipeToLoadLayout);
        mNoMoviesView = mView.findViewById(R.id.ll_no_movies);
        mRecyclerView = mView.findViewById(R.id.swipe_target);

        mLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void initRecycleViewAdapter() {
        Log.e(TAG, "initRecycleViewAdapter: " + " onCreate() -> initRecycleViewAdapter");
        mAdapter = new BaseRecycleViewAdapter<>(new ArrayList<>(0),
                R.layout.recyclerview_movies_item,
                MoviesViewHolder::new
        );

    }

    @Override
    protected void initSwipeRefreshLayout() {
        Log.e(HomeActivity.TAG, TAG + " onCreateView() -> initSwipeRefreshLayout");
        mSwipeToLoadLayout.setOnRefreshListener(() -> {
            Log.e(TAG, TAG + "=> onRefresh");
            mPresenter.loadRefreshedMovies(true);
        });
        mSwipeToLoadLayout.setOnLoadMoreListener(() -> {
            Log.e(TAG, TAG + "=> onLoadMore,item index is:" + mAdapter.getItemCount());
            mPresenter.loadMoreMovies(mAdapter.getItemCount());
        });
    }

    @Override
    protected void initEndlessScrollListener() {

    }

    @Override
    protected void startPresenter() {
        if (mPresenter != null) {
            mPresenter.start();
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mPresenter!=null){
            mPresenter.start();
        }
    }

    @Override
    public void setPresenter(MoviesContract.Presenter presenter) {
        mPresenter = presenter;
    }


    @Override
    public void showRefreshedMovies(List<Movie> movies) {
        if (mAdapterData.size() != 0 && movies.get(0).getId().equals(mAdapterData.get(0).getId())){
            return;
        }
        mAdapterData.addAll(movies);
        mAdapter.replaceData(mAdapterData);
        mRecyclerView.setVisibility(View.VISIBLE);
        mNoMoviesView.setVisibility(View.GONE);

    }

    @Override
    public void showLoadedMoreMovies(List<Movie> movies) {
        mAdapterData.addAll(movies);
        mAdapter.replaceData(mAdapterData);
        mSwipeToLoadLayout.setLoadingMore(false);
    }

    @Override
    public void showNoMovies() {
        mRecyclerView.setVisibility(View.GONE);
        mNoMoviesView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNoLoadedMoreMovies() {
        Toast.makeText(getActivity().getApplicationContext(), "No more content.....", Toast.LENGTH_SHORT).show();
        mSwipeToLoadLayout.setLoadingMore(false);
    }

    @Override
    public void setRefreshedIndicator(boolean active) {
        if (getView() == null) return;
        Log.e(TAG, "setRefreshedIndicator: => loading indicator: " + active);
        mSwipeToLoadLayout.post(() -> mSwipeToLoadLayout.setRefreshing(active));

    }


    static class MoviesViewHolder extends BaseRecycleViewHolder<Movie> implements View.OnClickListener {

        ImageView mMovieImage;
        TextView mMovieTitle;
        RatingBar mMovieStars;
        TextView mMovieRatingAverage;

        public MoviesViewHolder(View itemView) {
            super(itemView);

            mMovieImage = itemView.findViewById(R.id.movie_cover);
            mMovieTitle = itemView.findViewById(R.id.movie_title);
            mMovieStars = itemView.findViewById(R.id.rating_star);
            mMovieRatingAverage = itemView.findViewById(R.id.movie_average);

            itemView.setOnClickListener(this);
        }

        @Override
        protected void onBindItem(Movie movie) {
            Context context=itemView.getContext();
            if (movie == null) return;
            Picasso.with(context)
                    .load(movie.getImages().getLarge())
                    .placeholder(context.getResources().getDrawable(R.mipmap.ic_launcher))
                    .into(mMovieImage);

            mMovieTitle.setText(movie.getTitle());
            final double average = movie.getRating().getAverage();
            if (average == 0.0) {
                mMovieStars.setVisibility(View.GONE);
                mMovieRatingAverage.setText("No Rating");
            } else {
                mMovieStars.setVisibility(View.VISIBLE);
                mMovieRatingAverage.setText(String.valueOf(average));
                mMovieStars.setStepSize(0.5f);
                mMovieStars.setRating((float) (movie.getRating().getAverage() / 2));
            }

        }
        @Override
        public void onClick(View v) {
            Log.e(HomeActivity.TAG, "==> onClick....Item");

            if (itemContent == null||itemView == null) return;

            Context context = itemView.getContext();
            if (context == null) return;

            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra("movie", itemContent);

            if (context instanceof Activity) {
                Activity activity = (Activity) context;

                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, mMovieImage, "cover").toBundle();
                ActivityCompat.startActivity(activity, intent, bundle);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       mAdapterData.clear();
//        mPresenter.cancelRetrofitRequest();
        mPresenter.unSubscribe();
        Log.e(TAG, "=> onDestroy()!!! ");
    }
}
