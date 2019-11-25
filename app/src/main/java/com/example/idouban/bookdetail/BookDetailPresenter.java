package com.example.idouban.bookdetail;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;

import com.example.idouban.beans.Book;

public class BookDetailPresenter implements BookDetailContract.Presenter {
    private static final String TAG = BookDetailPresenter.class.getSimpleName();
    private Book mBook;
    private BookDetailContract.View mBookDetailView;

    public BookDetailPresenter(Book book, BookDetailContract.View bookDetailView) {
        this.mBook = book;
        this.mBookDetailView = bookDetailView;
        this.mBookDetailView.setPresenter(this);
    }

    @Override
    public void start() {
        showBookDetail();
    }

    private void showBookDetail() {
        mBookDetailView.showCollapsingToolbarTitle(mBook.getTitle());
        mBookDetailView.showPicassoImage(mBook.getImages().getLarge());
        Log.e(TAG, "showPicassoImage: "+mBook.getImages().getLarge());
    }

    @Override
    public void loadBookContext() {
       mBookDetailView.setBookContextoFragment("内容简介"+mBook.getSummary());
    }

    @Override
    public void loadBookAuthor() {
        mBookDetailView.setBookAuthortoFragment("作者简介"+mBook.getAuthor_intro());
    }

    @Override
    public void loadBookCatalog() {
        mBookDetailView.setBookCatalogtoFragment("目录"+mBook.getCatalog());
    }
}
