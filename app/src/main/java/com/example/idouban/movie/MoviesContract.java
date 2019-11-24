package com.example.idouban.movie;

import com.example.idouban.BasePresenter;
import com.example.idouban.BaseView;
import com.example.idouban.beans.Movie;

import java.util.List;

public interface MoviesContract {
    interface  View extends BaseView<Presenter>{
        void showMovies(List<Movie> movies);
        void showNoMovies();
        void setLoadingIndicator(boolean active);
    }
    interface  Presenter extends BasePresenter{
        void loadMovies(boolean forceUpdate);
    }

}
