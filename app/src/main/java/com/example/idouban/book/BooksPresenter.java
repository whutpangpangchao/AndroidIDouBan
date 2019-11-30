package com.example.idouban.book;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.idouban.api.IDoubbanService;
import com.example.idouban.beans.Book;
import com.example.idouban.beans.BooksInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class BooksPresenter implements BooksContract.Presenter {

    private static final String TAG = BooksPresenter.class.getSimpleName();
    private BooksContract.View mBookView;
    private final IDoubbanService mIDoubbanService;
    private boolean mFirstLoad = true;
    private Call<BooksInfo> mBooksRetrofitCallback;
    private CompositeSubscription mCompositeSubscription;

    //TODO 初始化Presenter
    public BooksPresenter(@NonNull IDoubbanService booksService, @NonNull BooksContract.View bookFragment) {
        mIDoubbanService = booksService;
        mBookView = bookFragment;
        mBookView.setPresenter(this);
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void loadRefreshedBooks(boolean forceUpdate) {
        loadBooks(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    // TODO: 加载更多的书籍信息
    @Override
    public void loadMoreBooks(int start) {
        mCompositeSubscription.add(mIDoubbanService.searchBookWithRxJava("黑客与画家", start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BooksInfo>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "===> Load More Book : onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "===> onError: Thread.Id = " + Thread.currentThread().getId() + ", Error: " + e.getMessage());
                        processLoadMoreEmptyBooks();
                    }

                    @Override
                    public void onNext(BooksInfo booksInfo) {
                        List<Book> loadMoreList = booksInfo.getBooks();
                        Log.d(TAG, "===> Load More : onNext , size = " + loadMoreList.size());
                        processLoadMoreBooks(loadMoreList);
                    }
                })
        );
    }

    @Override
    public void cancelRetrofitRequest() {
        Log.e(TAG, "=> cancelRetrofitRequest() isCanceled = " + mBooksRetrofitCallback.isCanceled());
        if (!mBooksRetrofitCallback.isCanceled()) mBooksRetrofitCallback.cancel();
    }

    @Override
    public void unSubscribe() {
        Log.d(TAG, "=> unSubscribe all subscribe");
        if(mCompositeSubscription!=null){
            mCompositeSubscription.unsubscribe();
        }

    }

    @Override
    public void start() {
        loadRefreshedBooks(false);
    }

    // TODO: 网络请求获取书籍信息
    private void loadBooks(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            mBookView.setRefreshedIndicator(true);
        }
        if (forceUpdate) {
            mCompositeSubscription.add(mIDoubbanService.searchBookWithRxJava("黑客与画家",0)
            .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<BooksInfo>() {
                        @Override
                        public void onCompleted() {
                            Log.d(TAG, "===> Search Book: onNext "+", showLoadingUI: "+showLoadingUI);
                            if(showLoadingUI){
                                mBookView.setRefreshedIndicator(false);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, "===> search Book: onError : Thread.Id = "+Thread.currentThread().getId()+", Error: "+e.getMessage());
                            if(showLoadingUI){
                                mBookView.setRefreshedIndicator(false);
                            }
                            processEmptyBooks();
                        }

                        @Override
                        public void onNext(BooksInfo booksInfo) {
                            List<Book> booksList = booksInfo.getBooks();
                            Log.d(TAG, "===> Search Book: onNext , size = "+booksList.size());
                            processBooks(booksList);

                        }
                    })
            );
        }
    }

    // TODO: 展示书籍信息
    private void processBooks(List<Book> books) {
        if (books.isEmpty()) {
            processEmptyBooks();
        } else {
            mBookView.showRefreshedBooks(books);
        }
    }

    private void processEmptyBooks() {
        mBookView.showNoBooks();
    }

    //TODO: 用于处理加载更多书籍信息
    private void processLoadMoreBooks(List<Book> books) {
        if (books.isEmpty()) processLoadMoreEmptyBooks();
        else mBookView.showLoadedMoreBooks(books);
    }

    //TODO: 用于处理加载更多书籍信息时，书籍信息为空的情况
    private void processLoadMoreEmptyBooks() {
        Log.e(TAG, "Loading Empty books");
        mBookView.showNoLoadedMoreBooks();
    }

}
