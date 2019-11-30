package com.example.idouban.movie;

import com.example.idouban.BasePresenter;
import com.example.idouban.BaseView;
import com.example.idouban.beans.Movie;

import java.util.List;

public interface MoviesContract {
    interface  View extends BaseView<Presenter>{
       void showRefreshedMovies(List<Movie> movies);
       void showLoadedMoreMovies(List<Movie> movies);
       void showNoMovies();
       void showNoLoadedMoreMovies();
       void setRefreshedIndicator(boolean active);
    }
    interface  Presenter extends BasePresenter{
        void loadRefreshedMovies(boolean forceUpdate);
        void loadMoreMovies(int movieStartIndex);
        void cancelRetrofitRequest();
        void unSubscribe();
    }

}
