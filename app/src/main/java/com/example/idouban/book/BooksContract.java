package com.example.idouban.book;

import com.example.idouban.BasePresenter;
import com.example.idouban.BaseView;
import com.example.idouban.beans.Book;
import java.util.List;

public interface BooksContract {
    interface View extends BaseView<Presenter> {
        void showRefreshedBooks(List<Book>books);
        void showLoadedMoreBooks(List<Book> books);
        void showNoBooks();
        void showNoLoadedMoreBooks();
        void setRefreshedIndicator(boolean active);
    }
    interface Presenter extends BasePresenter{
       void loadRefreshedBooks(boolean forceUpdate);
       void loadMoreBooks(int start);
       void cancelRetrofitRequest();
    }
}

