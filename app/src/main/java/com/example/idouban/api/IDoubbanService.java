package com.example.idouban.api;

import com.example.idouban.beans.BooksInfo;
import com.example.idouban.beans.HotMoviesInfo;
import rx.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IDoubbanService {
    String BASE_URL="https://api.douban.com/v2/";
    @GET("movie/in_theaters?apikey=0df993c66c0c636e29ecbb5344252a4a")
    Call<HotMoviesInfo> searchHotMovies(@Query("start") int startIndex);
    @GET("book/search?apikey=0df993c66c0c636e29ecbb5344252a4a")
    Call<BooksInfo>searchBooks(@Query("q") String name,@Query("start") int index);
    @GET("movie/in_theaters?apikey=0df993c66c0c636e29ecbb5344252a4a")
    Observable<HotMoviesInfo> searchHotMoviesWithRxJava(@Query("start") int startIndex);
}
