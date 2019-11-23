package com.example.idouban.api;

import com.example.idouban.movie.HotMoviesInfo;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IDoubbanService {
    String BASE_URL="http://api.douban.com/v2/";
    @GET("movie/in_theaters?apikey=0df993c66c0c636e29ecbb5344252a4a")
    Call<HotMoviesInfo> searchHotMovies();
}
