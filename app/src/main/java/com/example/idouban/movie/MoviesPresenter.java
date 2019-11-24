package com.example.idouban.movie;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.idouban.api.IDoubbanService;
import com.example.idouban.beans.HotMoviesInfo;
import com.example.idouban.beans.Movie;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.google.common.base.Preconditions.checkNotNull;

public class MoviesPresenter implements MoviesContract.Presenter{
    private final  static String TAC=MoviesPresenter.class.getSimpleName();
    private final MoviesContract.View mMoviesView;
    private final IDoubbanService mIDuobanService;
    private boolean mFirstLoad=true;

    public MoviesPresenter(@NonNull IDoubbanService moviesService,@NonNull MoviesContract.View moviesView){
        mIDuobanService=checkNotNull(moviesService,"IDoubanServie cannot be null!");
        mMoviesView=checkNotNull(moviesView,"moviesView cannot be null");
        Log.e(TAG, "MoviesPresenter : "+",MoviesPresenter:create"+this);
        mMoviesView.setPresenter(this);
    }

    @Override
    public void loadMovies(boolean forceUpdate) {
        Log.e(TAG, "loadMovies:");
        loadMovies(forceUpdate||mFirstLoad,true);
        mFirstLoad=false;
    }

    private void loadMovies(boolean forceUpdate, final boolean showLoadingUI){
        Log.e(TAG, "loadMovies: "+"forceUpdate");
        if(showLoadingUI){
            //MoviesFragment需要显示Loading 界面
            mMoviesView.setLoadingIndicator(true);
        }
        if(forceUpdate){
           // Log.e(TAG, "loadMovies: "+"forceUpdate");
            mIDuobanService.searchHotMovies().enqueue(new Callback<HotMoviesInfo>() {
                @Override
                public void onResponse(Call<HotMoviesInfo> call, Response<HotMoviesInfo> response) {
                    Log.d(TAG, "===> onResponse: Thread.Id = " + Thread.currentThread().getId());
                    List<Movie> moviesList = response.body().getMovies();
                    for (Movie movie :moviesList){
                        Log.d(TAG, "===> onResponse: Thread.Id = " +movie.getTitle());
                    }
                    //debug
                    Log.e(TAG, "===> Response, size = " + moviesList.size()
                            + " showLoadingUI: " + showLoadingUI);
                    //获取数据成功，Loading UI消失
                    if(showLoadingUI) {
                        mMoviesView.setLoadingIndicator(false);
                    }
                    processMovies(moviesList);
                }

                @Override
                public void onFailure(Call<HotMoviesInfo> call, Throwable t) {
                    Log.d(TAG, "===> onFailure: Thread.Id = "
                            + Thread.currentThread().getId() + ", Error: " + t.getMessage());

                    //获取数据成功，Loading UI消失
                    if(showLoadingUI) {
                        mMoviesView.setLoadingIndicator(false);
                    }
                    //mMoviesView.showLoadingMoviesError();
                }
            });
        }

    }

    @Override
    public void start() {
        loadMovies(false);
    }
    private void processMovies( List<Movie> movies){
        if(movies.isEmpty()){
            processEmptyTasks();
        }else {
            mMoviesView.showMovies(movies);
        }
    }
    private void processEmptyTasks(){
        mMoviesView.showNoMovies();
    }
}
