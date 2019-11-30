package com.example.idouban.movie;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.idouban.HomeActivity;
import com.example.idouban.api.IDoubbanService;
import com.example.idouban.beans.HotMoviesInfo;
import com.example.idouban.beans.Movie;
import rx.subscriptions.CompositeSubscription;

import java.util.List;

import retrofit2.Call;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.google.common.base.Preconditions.checkNotNull;

public class MoviesPresenter implements MoviesContract.Presenter {
    private final static String TAC = MoviesPresenter.class.getSimpleName();
    private final MoviesContract.View mMoviesView;
    private final IDoubbanService mIDuobanService;
    private boolean mFirstLoad = true;
    private int mMovieTotal;
    private Call<HotMoviesInfo> mMoviesRetrofitCallback;
    private Subscription mSubscription;
    private CompositeSubscription mCompositeSubscription;

    public MoviesPresenter(@NonNull IDoubbanService moviesService, @NonNull MoviesContract.View moviesView) {
        mIDuobanService = checkNotNull(moviesService, "IDoubanServie cannot be null!");
        mMoviesView = checkNotNull(moviesView, "moviesView cannot be null");
        Log.e(TAG, "MoviesPresenter : " + ",MoviesPresenter:create" + this);
        mMoviesView.setPresenter(this);
        mCompositeSubscription = new CompositeSubscription();
    }

    private void loadMovies(boolean forceUpdate, final boolean showLoadingUI) {
        Log.e(TAG, "loadMovies: " + "forceUpdate");
        if (showLoadingUI) {
            //MoviesFragment需要显示Loading 界面
            mMoviesView.setRefreshedIndicator(true);
        }
        if (forceUpdate) {
//            Observable<HotMoviesInfo> observable =mIDuobanService.searchHotMoviesWithRxJava(0);
//            Log.e(TAG, "observable: "+observable);
            mCompositeSubscription.add(mIDuobanService.searchHotMoviesWithRxJava(0)
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(()->{
                        if (showLoadingUI){
                            mMoviesView.setRefreshedIndicator(true);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<HotMoviesInfo>() {
                        @Override
                        public void onStart() {
                            Log.e(TAG, "onStart -> mSubscription: "+mSubscription);
                        }

                        @Override
                        public void onCompleted() {
                            Log.d(TAG, "===> onCompleted: 11: Thread.Id =  "+Thread.currentThread().getId());
                            if (showLoadingUI){
                                mMoviesView.setRefreshedIndicator(false);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, "===> onError11 : Thread.Id = "+Thread.currentThread().getId()+", Error: "+e.getMessage());
                            if(showLoadingUI){
                                mMoviesView.setRefreshedIndicator(false);
                            }
                            processEmptyMovies();
                        }

                        @Override
                        public void onNext(HotMoviesInfo hotMoviesInfo) {
                            Log.d(TAG, "===> onNext 11: Thread.Id = "+Thread.currentThread().getId());
                            List<Movie> movieList=hotMoviesInfo.getMovies();
                            mMovieTotal=hotMoviesInfo.getTotal();
                            Log.e(TAG, "===> hotMoviesInfo, size = "+movieList.size()+" showLoadingUI: "+showLoadingUI+", total = "+mMovieTotal);
                            processMovies(movieList);
                        }
                    }));
        }

    }

    @Override
    public void start() {
        loadRefreshedMovies(false);
    }

    private void processMovies(List<Movie> movies) {
        if (movies.isEmpty()) {
            processEmptyMovies();
        } else {
            mMoviesView.showRefreshedMovies(movies);
        }
    }

    private void processEmptyMovies() {
        mMoviesView.showNoMovies();
    }

    @Override
    public void loadRefreshedMovies(boolean forceUpdate) {
        loadMovies(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;

    }

    @Override
    public void loadMoreMovies(int movieStartIndex) {
        Log.e(TAG, "movieStartIndex: " + movieStartIndex + ",mMovieTotal: " + mMovieTotal);
        if (movieStartIndex >= mMovieTotal) {
            processLoadMoreEmptyMovies();
            return;
        }
//        Observable<HotMoviesInfo> observable = DoubanManager.createDoubanService().searchHotMoviesWithRxJava(movieStartIndex);

        mCompositeSubscription.add(mIDuobanService.searchHotMoviesWithRxJava(movieStartIndex)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HotMoviesInfo>() {
                    @Override
                    public void onCompleted() {
                        Log.d(HomeActivity.TAG, "===> onCompleted 22: Thread.Id = " + Thread.currentThread().getId());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(HomeActivity.TAG, "===> onError 22: Thread.Id = " + Thread.currentThread().getId() + ", Error: " + e.getMessage());
                        processLoadMoreEmptyMovies();
                    }

                    @Override
                    public void onNext(HotMoviesInfo hotMoviesInfo) {
                        Log.d(HomeActivity.TAG, "===> onNext22: onNext 22: Thread.Id " + Thread.currentThread().getId());
                        List<Movie> moreMoviesList = hotMoviesInfo.getMovies();
                        Log.e(HomeActivity.TAG, "===> hotMoviesInfo,size = " + moreMoviesList.size());
                        processLoadMoreMovies(moreMoviesList);
                    }
                }));
    }

    @Override
    public void cancelRetrofitRequest() {
//        Log.e(TAG, "=> cancelRetrofitRequest() isCanceled = "+mMoviesRetrofitCallback.isCanceled());
//        if (!mMoviesRetrofitCallback.isCanceled())mMoviesRetrofitCallback.cancel();
        Log.d(HomeActivity.TAG, TAG + "=> cancelRetrofitRequest() isCanceled = " + mMoviesRetrofitCallback.isCanceled());
        if (mMoviesRetrofitCallback != null && !mMoviesRetrofitCallback.isCanceled())
            mMoviesRetrofitCallback.cancel();
    }

    @Override
    public void unSubscribe() {
//        Log.d(HomeActivity.TAG, TAG + "=> unSubscribe() isUnSubscribed = " + mSubscription.isUnsubscribed());
//        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
//            mSubscription.unsubscribe();
//        }
        Log.d(TAG, TAG+"=> unSubscribe all subscribe");
        if(mCompositeSubscription!=null){
            mCompositeSubscription.unsubscribe();
        }

    }

    private void processLoadMoreEmptyMovies() {
        mMoviesView.showNoLoadedMoreMovies();
    }

    private void processLoadMoreMovies(List<Movie> movies) {
        if (movies.isEmpty()) {
            processLoadMoreEmptyMovies();
        } else {
            mMoviesView.showLoadedMoreMovies(movies);
        }
    }
}
