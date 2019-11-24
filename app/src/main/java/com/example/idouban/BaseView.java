package com.example.idouban;

import com.example.idouban.moviedetail.MovieDetailPresenter;

public interface BaseView<T> {
    void setPresenter(T presenter);

}
