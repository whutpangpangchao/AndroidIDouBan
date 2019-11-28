package com.example.idouban.bookdetail;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.idouban.R;
import com.example.idouban.base.BaseActivity;
import com.example.idouban.beans.Book;
import com.example.idouban.utils.ConstContent;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class BookDetailActivity extends BaseActivity implements BookDetailContract.View {
    private static final String TAG = BookDetailActivity.class.getSimpleName();
    private BookDetailContract.Presenter mPresenter;
    private String mBookContext;
    private String mBookAuthor;
    private String mBookCatalog;
    private ViewPager mViewPager;

    @Override
    protected void initVariables() {
        new BookDetailPresenter((Book) getIntent().getSerializableExtra(ConstContent.INTENT_EXTRA_BOOK), this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_book_detail);
        mViewPager = findViewById(R.id.book_viewpager);
        setupViewPager(mViewPager);
        initTab();
    }

    private void initTab() {
        TabLayout tabLayout = findViewById(R.id.book_sliding_tabs);
        if (tabLayout != null) {
            tabLayout.addTab(tabLayout.newTab());
            tabLayout.addTab(tabLayout.newTab());
            tabLayout.addTab(tabLayout.newTab());
            tabLayout.setupWithViewPager(mViewPager);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.start();

    }

    private void setupViewPager(ViewPager viewPager) {
        mPresenter.loadBookContext();
        mPresenter.loadBookAuthor();
        mPresenter.loadBookCatalog();
        BookDetailPagerAdapter adapter = new BookDetailPagerAdapter(getSupportFragmentManager());
        BookDetailFragment bookContextDetailFragment = BookDetailFragment.createInstance(mBookContext, ConstContent.TYPE_BOOK_CONTXET);
        BookDetailFragment bookAuthorDetailFragment = BookDetailFragment.createInstance(mBookAuthor, ConstContent.TYPE_BOOK_AUTHOR);
        BookDetailFragment bookCatalogDetailFragment = BookDetailFragment.createInstance(mBookCatalog, ConstContent.TYPE_BOOK_CATALOG);
        adapter.addFragment(bookContextDetailFragment, "内容简介");
        adapter.addFragment(bookAuthorDetailFragment, "作者简介");
        adapter.addFragment(bookCatalogDetailFragment, "目录");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void showCollapsingToolbarTitle(String title) {
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.book_collapsing_toolbar);
        collapsingToolbarLayout.setTitle(title);

    }

    @Override
    public void showPicassoImage(String largeImagePath) {
        ImageView bookImage = findViewById(R.id.book_image);
        Picasso.with(bookImage.getContext())
                .load(largeImagePath)
                .into(bookImage);

    }

    @Override
    public void setBookContextoFragment(String book_context) {
        mBookContext = book_context;
    }

    @Override
    public void setBookAuthortoFragment(String book_author) {
        mBookAuthor = book_author;
    }

    @Override
    public void setBookCatalogtoFragment(String book_catalog) {
        mBookCatalog = book_catalog;
    }

    @Override
    public void setPresenter(BookDetailContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);

    }

    static class BookDetailPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        private BookDetailPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
