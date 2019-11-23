package com.example.idouban.movie;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.idouban.R;
import com.example.idouban.api.DoubanManager;
import com.example.idouban.api.IDoubbanService;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.lang.reflect.Array;
import java.util.List;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesFragment extends Fragment {
    private List<Movie> mMovieList= new ArrayList<>();
    private RecyclerView mRecyclerView;

    public MoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.e(TAG, "onAttach: "+context.getClass().getSimpleName());
        loadMovies(new Callback<HotMoviesInfo>() {
            @Override
            public void onResponse(Call<HotMoviesInfo> call, Response<HotMoviesInfo> response) {
                Log.e(TAG, "onResponse: Thread.ID = "+Thread.currentThread().getId());
                mMovieList =response.body().getMovies();
                Log.e(TAG, "onResponse: size"+mMovieList.size());
                for (Movie movie:mMovieList){
                    Log.e(TAG, "onResponse: "+movie.getTitle());
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
        View view=inflater.inflate(R.layout.fragment_movies, container, false);
        mRecyclerView=view.findViewById(R.id.recycler_hot_movies);
        return view;
    }
    private void loadMovies(Callback<HotMoviesInfo> callback){
        IDoubbanService movieService = DoubanManager.createDoubanService();
        movieService.searchHotMovies().enqueue(callback);
    }
    static class MoviesAdapter extends RecyclerView.Adapter<MoviesViewHolder>{
        private List<Movie> movies;
        private Context context;
        @LayoutRes
        private int LayoutResId;
        public MoviesAdapter(Context context,@Nullable List<Movie>movies,@LayoutRes int layoutResId){
            this.context=context;
            this.movies=movies;
            this.LayoutResId=layoutResId;
        }


        @NonNull
        @Override
        public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull MoviesViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
    static class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
      public MoviesViewHolder(View itemView){
          super(itemView);
      }
        @Override
        public void onClick(View view) {

        }
    }

}
