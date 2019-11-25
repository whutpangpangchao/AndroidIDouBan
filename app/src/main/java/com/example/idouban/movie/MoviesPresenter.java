package com.example.idouban.movie;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.idouban.HomeActivity;
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
    private int mMovieTotal;
    private Call<HotMoviesInfo> mMoviesRetrofitCallback;

    public MoviesPresenter(@NonNull IDoubbanService moviesService,@NonNull MoviesContract.View moviesView){
        mIDuobanService=checkNotNull(moviesService,"IDoubanServie cannot be null!");
        mMoviesView=checkNotNull(moviesView,"moviesView cannot be null");
        Log.e(TAG, "MoviesPresenter : "+",MoviesPresenter:create"+this);
        mMoviesView.setPresenter(this);
    }

    private void loadMovies(boolean forceUpdate, final boolean showLoadingUI){
        Log.e(TAG, "loadMovies: "+"forceUpdate");
        if(showLoadingUI){
            //MoviesFragment需要显示Loading 界面
            mMoviesView.setRefreshedIndicator(true);
        }
        if(forceUpdate){
           // Log.e(TAG, "loadMovies: "+"forceUpdate");
            mMoviesRetrofitCallback =mIDuobanService.searchHotMovies(0);
            mMoviesRetrofitCallback.enqueue(new Callback<HotMoviesInfo>() {
                @Override
                public void onResponse(Call<HotMoviesInfo> call, Response<HotMoviesInfo> response) {
                    Log.d(TAG, "===> onResponse: Thread.Id = " + Thread.currentThread().getId());
                    List<Movie> moviesList = response.body().getMovies();
                    mMovieTotal=response.body().getTotal();
                    //debug
                    Log.e(TAG, "===> Response, size = " + moviesList.size()
                            + " showLoadingUI: " + showLoadingUI);
                    //获取数据成功，Loading UI消失
                    if(showLoadingUI) {
                        mMoviesView.setRefreshedIndicator(false);
                    }
                    processMovies(moviesList);
                }

                @Override
                public void onFailure(Call<HotMoviesInfo> call, Throwable t) {
                    Log.d(TAG, "===> onFailure: Thread.Id = "
                            + Thread.currentThread().getId() + ", Error: " + t.getMessage());

                    //获取数据成功，Loading UI消失
                    if(showLoadingUI) {
                        mMoviesView.setRefreshedIndicator(false);
                    }
                   processEmptyTasks();
                }
            });
        }

    }

    @Override
    public void start() {loadRefreshedMovies(false);
    }
    private void processMovies( List<Movie> movies){
        if(movies.isEmpty()){
            processEmptyTasks();
        }else {
            mMoviesView.showRefreshedMovies(movies);
        }
    }
    private void processEmptyTasks(){
        mMoviesView.showNoMovies();
    }

    @Override
    public void loadRefreshedMovies(boolean forceUpdate) {
        loadMovies(forceUpdate||mFirstLoad,true);
        mFirstLoad=false;

    }

    @Override
    public void loadMoreMovies(int movieStartIndex) {
        Log.e(TAG, "movieStartIndex: "+movieStartIndex+",mMovieTotal: "+mMovieTotal);
        if (movieStartIndex>=mMovieTotal){
           processLoadMoreEmptyMovies();
            return;
        }
        mMoviesRetrofitCallback = mIDuobanService.searchHotMovies(movieStartIndex);
        mMoviesRetrofitCallback.enqueue(new Callback<HotMoviesInfo>() {
            @Override
            public void onResponse(Call<HotMoviesInfo> call, Response<HotMoviesInfo> response) {
                Log.d(HomeActivity.TAG, "===> onResponse: Thread.Id = " + Thread.currentThread().getId());
                List<Movie> moreMoviesList = response.body().getMovies();
                //debug
                Log.e(HomeActivity.TAG, "===> Response, size = " + moreMoviesList.size());
                processLoadMoreMovies(moreMoviesList);
            }

            @Override
            public void onFailure(Call<HotMoviesInfo> call, Throwable t) {
                Log.d(HomeActivity.TAG, "===> onFailure: Thread.Id = "
                        + Thread.currentThread().getId() + ", Error: " + t.getMessage());
                processLoadMoreEmptyMovies();
            }
        });


    }

    @Override
    public void cancelRetrofitRequest() {
        Log.e(TAG, "=> cancelRetrofitRequest() isCanceled = "+mMoviesRetrofitCallback.isCanceled());
        if (!mMoviesRetrofitCallback.isCanceled())mMoviesRetrofitCallback.cancel();

    }
    private void processLoadMoreEmptyMovies() {
        mMoviesView.showNoLoadedMoreMovies();
    }
    private void processLoadMoreMovies(List<Movie> movies) {
        if(movies.isEmpty()) {
            processLoadMoreEmptyMovies();
        }else {
            mMoviesView.showLoadedMoreMovies(movies);
        }
    }
}
