package com.example.idouban.book;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.idouban.api.IDoubbanService;
import com.example.idouban.beans.Book;
import com.example.idouban.beans.BooksInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.*;

public class BooksPresenter implements BooksContract.Presenter {

    private static final String TAG = BooksPresenter.class.getSimpleName();
    private BooksContract.View mBookView;
    private final IDoubbanService mIDoubbanService;
    private boolean mFirstLoad = true;

    public BooksPresenter(@NonNull IDoubbanService booksService, @NonNull BooksContract.View bookFragment) {
        mIDoubbanService = booksService;
        mBookView = bookFragment;
        mBookView.setPresenter(this);
    }

    @Override
    public void loadRefreshedBooks(boolean forceUpdate) {
        loadBooks(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    @Override
    public void loadMoreBooks(int start) {
        mIDoubbanService.searchBooks("黑客与画家", start).enqueue(new Callback<BooksInfo>() {
            @Override
            public void onResponse(Call<BooksInfo> call, Response<BooksInfo> response) {
                List<Book> loadMoreList = response.body().getBooks();
                Log.e(TAG, "===> Load More Book：Response,size = " + loadMoreList.size());
                processLoadMoreBooks(response.body().getBooks());
            }

            @Override
            public void onFailure(Call<BooksInfo> call, Throwable t) {
                Log.e(TAG, "==> onFailure : Thread.Id = " + Thread.currentThread().getId() + ", Error: " + t.getMessage());
                processLoadMoreEmptyBooks();
            }
        });
    }

    @Override
    public void start() {
        loadRefreshedBooks(false);
    }

    private void loadBooks(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            mBookView.setRefreshedIndicator(true);
        }
        if (forceUpdate) {
            mIDoubbanService.searchBooks("黑客与画家", 0).enqueue(new Callback<BooksInfo>() {
                @Override
                public void onResponse(Call<BooksInfo> call, Response<BooksInfo> response) {
                    List<Book> bookList = response.body().getBooks();
                    Log.e(TAG, "===> Search Book: Response, size = " + bookList.size() + "showLoadingUI: " + showLoadingUI);
                    if (showLoadingUI) {
                        mBookView.setRefreshedIndicator(false);
                    }
                    processBooks(response.body().getBooks());
                }

                @Override
                public void onFailure(Call<BooksInfo> call, Throwable t) {
                    Log.e(TAG, "===> onFailure:Thread.Id = " + Thread.currentThread().getId() + ",Error:" + t.getMessage());
                    if (showLoadingUI) {
                        mBookView.setRefreshedIndicator(false);
                    }
                    processEmptyTasks();
                }
            });

        }
    }

    private void processBooks(List<Book> books) {
        if (books.isEmpty()) {
            processEmptyTasks();
        } else {
            mBookView.showRefreshedBooks(books);
        }
    }

    private void processEmptyTasks() {
        mBookView.showNoBooks();
    }

    private void processLoadMoreBooks(List<Book> books) {
        if (books.isEmpty()) processLoadMoreEmptyBooks();
        else mBookView.showLoadedMoreBooks(books);
    }

    private void processLoadMoreEmptyBooks() {
        Log.e(TAG, "Loading Empty books");
        mBookView.showNoLoadedMoreBooks();
    }

}
