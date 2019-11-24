package com.example.idouban.moviedetail;

import com.example.idouban.BasePresenter;
import com.example.idouban.BaseView;

public interface MovieDetailContract {
    interface View extends BaseView<Presenter>{
        void showCollapsingToolbarTitle(String title);
        void showPicassoImage(String largeImagePath);
        void setMovieInfoToFragment(String movieInfo);
        void setMovieAltToFragment(String movieAlt);
    }
    interface Presenter extends BasePresenter{
        void loadMovieInfo();
        void loadMovieAlt();
    }

}
