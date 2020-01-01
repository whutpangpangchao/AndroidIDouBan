package com.example.idouban.blogs;

import com.example.idouban.BasePresenter;
import com.example.idouban.BaseView;
import com.example.idouban.beans.Blog;
import java.util.List;


public interface BlogContract {
    interface View extends BaseView<Presenter> {
        void showRefreshedBlogs(List<Blog> blogs);
        void showLoadedMoreBlogs(List<Blog> blogs);
        void showNoBlogs();
        void showNoLoadedMoreBlogs();
        void setRefreshedIndicator(boolean active);


    }
    interface Presenter extends BasePresenter{
        void loadRefreshedBlogs(boolean forceUpdate);
        void loadMoreBlogs(int blogStartIndex);
        void unSubscribe();
    }

}
