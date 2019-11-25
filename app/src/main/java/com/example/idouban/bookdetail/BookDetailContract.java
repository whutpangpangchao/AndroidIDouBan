package com.example.idouban.bookdetail;

import com.example.idouban.BasePresenter;
import com.example.idouban.BaseView;

public interface BookDetailContract {
    interface View extends BaseView<Presenter> {
        void showCollapsingToolbarTitle(String title);

        void showPicassoImage(String largeImagePath);

        void setBookContextoFragment(String bookcontext);

        void setBookAuthortoFragment(String bookauthor);

        void setBookCatalogtoFragment(String bookcatalog);


    }

    interface Presenter extends BasePresenter {
        void loadBookContext();

        void loadBookAuthor();

        void loadBookCatalog();
    }

}
