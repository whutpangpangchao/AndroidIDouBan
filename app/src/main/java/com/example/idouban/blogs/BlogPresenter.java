package com.example.idouban.blogs;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.idouban.HomeActivity;
import com.example.idouban.api.IDoubbanService;
import com.example.idouban.beans.Blog;
import com.example.idouban.beans.BlogsInfo;
import com.example.idouban.utils.ConstContent;
import com.google.errorprone.annotations.DoNotMock;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.*;

public class BlogPresenter implements BlogContract.Presenter {

    private static final String TAG=BlogPresenter.class.getSimpleName();
    private BlogContract.View mBlogsView;
    private IDoubbanService mBlogsService;
    private boolean mFirstLoad = true;
    private CompositeSubscription mCompositeSubscription;

    public BlogPresenter(@NonNull IDoubbanService blogsService, @NonNull BlogContract.View blogsView){
        Log.e(TAG, TAG+"===> BlogPresenter");
        mBlogsService = checkNotNull(blogsService, "BlogsService cannot be null!");
        mBlogsView = checkNotNull(blogsView, "BlogsView cannot be null!");
        mBlogsView.setPresenter(this);
        mCompositeSubscription= new CompositeSubscription();

    }


    @Override
    public void loadRefreshedBlogs(boolean forceUpdate) {
        loadBlogs(forceUpdate||mFirstLoad,true);
        mFirstLoad=false;

    }

    @Override
    public void loadMoreBlogs(int blogStartIndex) {

    }

    @Override
    public void unSubscribe() {

    }

    @Override
    public void start() {
        loadRefreshedBlogs(false);

    }

    private void loadBlogs(boolean forceUpdate, final boolean showLoadingUI){
        Log.e(TAG, "===> loadBlogs.  forceUpdate: " + forceUpdate + ", showLoadingUI: " + showLoadingUI + "\n");

        if (forceUpdate) {
            mCompositeSubscription.add(mBlogsService.getBlogWithRxJava(ConstContent.API_BLOG_WEBSITE)
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(() -> {
                        if (showLoadingUI) {
                            mBlogsView.setRefreshedIndicator(true);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<BlogsInfo>() {
                        @Override
                        public void onCompleted() {
                            Log.d(HomeActivity.TAG, TAG + "===> onCompleted() " + " showLoadingUI: " + showLoadingUI);
                            //获取数据成功，Loading UI消失
                            if(showLoadingUI) {
                                mBlogsView.setRefreshedIndicator(false);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(HomeActivity.TAG, TAG + "===> onError: " + e.getMessage());

                            //获取数据成功，Loading UI消失
                            if(showLoadingUI) {
                                mBlogsView.setRefreshedIndicator(false);
                            }
                            processEmptyBlogs();
                        }

                        @Override
                        public void onNext(BlogsInfo blogsInfo) {
                            List<Blog> blogList = blogsInfo.getBlog();
                            Log.d(HomeActivity.TAG, TAG + "===> onNext " + ", blogList size: " + blogList.size());

                            //debug
                            //Log.e(HomeActivity.TAG, TAG + "===> BlogsInfo, size = " + blogList.size());

                            processBlogs(blogList);
                        }
                    })

            );
        }
    }


    private void processBlogs(List <Blog>blogs){
        if(blogs.isEmpty()){
            processEmptyBlogs();
        }else{
            mBlogsView.showRefreshedBlogs(blogs);
        }


    }

    private void processEmptyBlogs(){
        mBlogsView.showNoBlogs();
    }

}
