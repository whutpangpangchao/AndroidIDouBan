package com.example.idouban.blogs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.idouban.HomeActivity;
import com.example.idouban.R;
import com.example.idouban.base.BaseFragment;
import com.example.idouban.base.BaseRecycleViewAdapter;
import com.example.idouban.base.BaseRecycleViewHolder;
import com.example.idouban.beans.Blog;
import com.example.idouban.moviewebsite.WebViewActivity;
import com.example.idouban.utils.ConstContent;
import com.example.idouban.utils.ScrollChildSwipeRefreshLayout;

import java.util.List;
import java.util.ArrayList;

public class BlogFragment extends BaseFragment<Blog> implements BlogContract.View {
    private static final String TAG=BlogFragment.class.getSimpleName();
    private BlogContract.Presenter mPresenter;
    private View mNoBlogView;

    public BlogFragment(){}
    public static BlogFragment newInstance(){ return new BlogFragment();}




    @Override
    public void setPresenter(BlogContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void initVariables() {
        mAdapterData=new ArrayList<>();
    }

    @Override
    protected void initRecycleViewAdapter() {
        mAdapter = new BaseRecycleViewAdapter<>(new ArrayList<>(0),
                R.layout.recyclerview_blog_item,
                BlogViewHolder::new);

    }

    @Override
    protected void initRecycleView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "===> initRecycleView");
        mView=inflater.inflate(R.layout.fragment_blog,container,false);
        mRecyclerView=mView.findViewById(R.id.recycler_blog);
        mNoBlogView=mView.findViewById(R.id.ll_no_blog);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager= new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initSwipeRefreshLayout() {
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout = mView.findViewById(R.id.blog_refresh_layout);

        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );

        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(mRecyclerView);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.e(HomeActivity.TAG, "\n\n onRefresh load Refreshed Blog...");
            mPresenter.loadRefreshedBlogs(true);
        });

    }

    @Override
    protected void initEndlessScrollListener() {

    }

    @Override
    protected void startPresenter() {
        if(mPresenter!=null){
            mPresenter.start();
        }

    }

    @Override
    public void showRefreshedBlogs(List<Blog> blogs) {
        Log.e(TAG, "showRefreshBlogs blogs list " + blogs.size());
        //If the refreshed data is a part of mAdapterBooksData, don't operate mBookAdapter
        if(mAdapterData.size() != 0
                && blogs.get(0).getId() == mAdapterData.get(0).getId()) {
            return;
        }
        mAdapterData.addAll(blogs);
        mAdapter.replaceData(mAdapterData);
        mRecyclerView.setVisibility(View.VISIBLE);
        mNoBlogView.setVisibility(View.GONE);
    }

    @Override
    public void showLoadedMoreBlogs(List<Blog> blogs) {

    }

    @Override
    public void showNoBlogs() {

    }

    @Override
    public void showNoLoadedMoreBlogs() {

    }

    @Override
    public void setRefreshedIndicator(boolean active) {
        if(getView() == null) return;

        Log.e(TAG, "===> setRefreshedIndicator() loading indicator: " + active);
        final SwipeRefreshLayout swipeRefreshLayout = getView().findViewById(R.id.blog_refresh_layout);

        // Make sure setRefreshing() is called after the layout is done with everything else.
        swipeRefreshLayout.post(() -> {
            Log.e(TAG, "swipeRefreshLayout run() active: " + active);
            swipeRefreshLayout.setRefreshing(active);
        });
    }

    private static class BlogViewHolder extends BaseRecycleViewHolder<Blog> implements View.OnClickListener{

        private TextView mBlogTitle;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            mBlogTitle=itemView.findViewById(R.id.txt_blog_title);
            mBlogTitle.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Context context=itemView.getContext();
            if(itemContent==null||context==null) return;
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(ConstContent.INTENT_EXTRA_WEBSITE_URL,itemContent.getUrl());
            context.startActivity(intent);

        }

        @Override
        protected void onBindItem(Blog blog) {
            Context context=itemView.getContext();
            if(blog==null||context==null) return;
            mBlogTitle.setText(blog.getName());
        }
    }
}
