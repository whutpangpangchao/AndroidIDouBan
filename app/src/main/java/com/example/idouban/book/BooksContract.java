package com.example.idouban.book;

import com.example.idouban.BasePresenter;
import com.example.idouban.BaseView;
import com.example.idouban.beans.Book;
import java.util.List;

public interface BooksContract {
    interface View extends BaseView<Presenter> {
        void showBooks(List<Book>books);
        void showNoBooks();
        void setLoadingIndicator(boolean active);
    }
    interface Presenter extends BasePresenter{
        void loadBooks(boolean forceUpdate);
    }
}

