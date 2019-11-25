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
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.example.idouban.HomeActivity;
import com.example.idouban.R;
import com.example.idouban.beans.Movie;
import com.example.idouban.moviedetail.MovieDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class MoviesFragment extends Fragment implements MoviesContract.View {
    private static final String TAG = MoviesFragment.class.getSimpleName();

    private List<Movie> mAdapterMoviesData;

    private RecyclerView mMovieRecyclerView;

    private MoviesAdapter mMovieAdapter;

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
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.e(HomeActivity.TAG, "MoviesFragment onAttach, presenter: " + mPresenter);
        if (mPresenter != null) {
            mPresenter.start();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMovieAdapter = new MoviesAdapter(new ArrayList<>(0), R.layout.recyclerview_movies_item);
        mAdapterMoviesData = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movies, container, false);
        mMovieRecyclerView = view.findViewById(R.id.swipe_target);
        mNoMoviesView = view.findViewById(R.id.ll_no_movies);
        mSwipeToLoadLayout = view.findViewById(R.id.swipeToLoadLayout);
        if (mMovieRecyclerView != null) {
            mMovieRecyclerView.setHasFixedSize(true);

            final GridLayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
            mMovieRecyclerView.setLayoutManager(layoutManager);
            mMovieRecyclerView.setAdapter(mMovieAdapter);
            mSwipeToLoadLayout.setOnRefreshListener(() -> {
                Log.e(TAG, "onCreateView: => onRefresh!");
                mPresenter.loadRefreshedMovies(true);
            });
            mSwipeToLoadLayout.setOnLoadMoreListener(() -> {
                Log.e(TAG, "onCreateView: => onLoadMore , item index is : " + mMovieAdapter.getItemCount());
                mPresenter.loadMoreMovies(mMovieAdapter.getItemCount());
            });
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mPresenter != null) {
            mPresenter.start();
        }
    }

    @Override
    public void setPresenter(MoviesContract.Presenter presenter) {
        mPresenter = presenter;
    }


    @Override
    public void showRefreshedMovies(List<Movie> movies) {
        if (mAdapterMoviesData.size() != 0 && movies.get(0).getId().equals(mAdapterMoviesData.get(0).getId())) {
            return;
        }
        mAdapterMoviesData.addAll(movies);
        mMovieAdapter.replaceData(mAdapterMoviesData);
        mMovieRecyclerView.setVisibility(View.VISIBLE);
        mNoMoviesView.setVisibility(View.GONE);

    }

    @Override
    public void showLoadedMoreMovies(List<Movie> movies) {
        mAdapterMoviesData.addAll(movies);
        mMovieAdapter.replaceData(mAdapterMoviesData);
        mSwipeToLoadLayout.setLoadingMore(false);
    }

    @Override
    public void showNoMovies() {
        mMovieRecyclerView.setVisibility(View.GONE);
        mNoMoviesView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNoLoadedMoreMovies() {
        Toast.makeText(getActivity().getApplicationContext(),"No more content.....",Toast.LENGTH_SHORT).show();
        mSwipeToLoadLayout.setLoadingMore(false);
    }

    @Override
    public void setRefreshedIndicator(boolean active) {
        if (getView()==null)return;
        Log.e(TAG, "setRefreshedIndicator: => loading indicator: "+active);
        mSwipeToLoadLayout.post(()->mSwipeToLoadLayout.setRefreshing(active));

    }


    //Movie's Adapter and view holder
    static class MoviesAdapter extends RecyclerView.Adapter<MoviesViewHolder> {

        private List<Movie> movies;

        @LayoutRes
        private int layoutResId;

        public MoviesAdapter(@NonNull List<Movie> movies, @LayoutRes int layoutResId) {
            setList(movies);
            this.layoutResId = layoutResId;
        }

        @Override
        public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
            return new MoviesViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MoviesViewHolder holder, int position) {
            if (holder == null) return;
            holder.updateMovie(movies.get(position));
        }

        @Override
        public int getItemCount() {
            return movies.size();
        }

        private void setList(List<Movie> movies) {
            this.movies = checkNotNull(movies);
        }

        public void replaceData(List<Movie> movies) {
            setList(movies);
            notifyDataSetChanged();
        }
    }

    static class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mMovieImage;
        TextView mMovieTitle;
        RatingBar mMovieStars;
        TextView mMovieRatingAverage;
        Movie movie;

        public MoviesViewHolder(View itemView) {
            super(itemView);

            mMovieImage = itemView.findViewById(R.id.movie_cover);
            mMovieTitle = itemView.findViewById(R.id.movie_title);
            mMovieStars = itemView.findViewById(R.id.rating_star);
            mMovieRatingAverage = itemView.findViewById(R.id.movie_average);

            itemView.setOnClickListener(this);
        }

        public void updateMovie(Movie movie) {
            if (movie == null) return;
            this.movie = movie;

            Context context = itemView.getContext();

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

            if (movie == null) return;
            if (itemView == null) return;

            Context context = itemView.getContext();
            if (context == null) return;

            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra("movie", movie);

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
        mAdapterMoviesData.clear();
        mPresenter.cancelRetrofitRequest();
        Log.e(TAG, "=> onDestroy()!!! ");
    }
}
