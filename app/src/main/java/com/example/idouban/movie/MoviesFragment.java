package com.example.idouban.movie;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.idouban.R;
import com.example.idouban.api.DoubanManager;
import com.example.idouban.api.IDoubbanService;
import com.squareup.picasso.Picasso;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesFragment extends Fragment {
    private List<Movie> mMovieList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;

    public MoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.e(TAG, "onAttach: " + context.getClass().getSimpleName());
        loadMovies(new Callback<HotMoviesInfo>() {
            @Override
            public void onResponse(Call<HotMoviesInfo> call, Response<HotMoviesInfo> response) {
                Log.e(TAG, "onResponse: Thread.ID = " + Thread.currentThread().getId());
                mMovieList = response.body().getMovies();
                mMoviesAdapter.setData(mMovieList);
                Log.e(TAG, "onResponse: size" + mMovieList.size());
                for (Movie movie : mMovieList) {
                    Log.e(TAG, "onResponse: " + movie.getTitle());
                }
            }

            @Override
            public void onFailure(Call<HotMoviesInfo> call, Throwable t) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movies, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_hot_movies);
        return view;
    }

    private void loadMovies(Callback<HotMoviesInfo> callback) {
        IDoubbanService movieService = DoubanManager.createDoubanService();
        movieService.searchHotMovies().enqueue(callback);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mRecyclerView != null)
            mRecyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mMoviesAdapter = new MoviesAdapter(getContext(), mMovieList, R.layout.recyclerview_movies_item);
        mRecyclerView.setAdapter(mMoviesAdapter);
    }



    static class MoviesAdapter extends RecyclerView.Adapter<MoviesViewHolder> {
        private List<Movie> movies;
        private Context context;
        @LayoutRes
        private int layoutResId;

        public MoviesAdapter(Context context, @Nullable List<Movie> movies, @LayoutRes int layoutResId) {
            this.context = context;
            this.movies = movies;
            this.layoutResId = layoutResId;
        }

        @NonNull
        @Override
        public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(layoutResId, parent, false);
            return new MoviesViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MoviesViewHolder holder, int position) {
            if (holder == null) return;
            holder.updateMovie(movies.get(position));
        }

        @Override
        public int getItemCount() {
            return movies.size();
        }

        public void setData(List<Movie> movies) {
            this.movies = movies;
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

        @Override
        public void onClick(View v) {
            Log.e(TAG, "=> onClick ... Item");
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
    }

}
