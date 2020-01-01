package com.example.idouban;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.idouban.aboutme.AboutFragment;
import com.example.idouban.api.DoubanManager;
import com.example.idouban.base.BaseActivity;
import com.example.idouban.blogs.BlogContract;
import com.example.idouban.blogs.BlogFragment;
import com.example.idouban.blogs.BlogPresenter;
import com.example.idouban.book.BooksContract;
import com.example.idouban.book.BooksFragment;
import com.example.idouban.book.BooksPresenter;
import com.example.idouban.movie.MoviesContract;
import com.example.idouban.movie.MoviesFragment;
import com.example.idouban.movie.MoviesPresenter;
import com.example.idouban.utils.CircleTransformation;
import com.example.idouban.utils.ConstContent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity {

    public static final String TAG = HomeActivity.class.getSimpleName();
    private ViewPager mViewPager;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private LinearLayout mLinearLayout;

    @Override
    protected void initVariables() {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);
        initFAB();
        initDrawerLayout();
        initAndSetupNavigation();
        setupNavigationHeader();
        mViewPager = findViewById(R.id.douban_view_pager);
        setupViewPager(mViewPager);
        initTabLayout();
        initOthersFragment();

    }

    private void initFAB() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

    private void initDrawerLayout() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerToggle.syncState();
        mDrawerLayout.addDrawerListener(drawerToggle);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initAndSetupNavigation() {
        mNavigationView = findViewById(R.id.navigation_view);
        mLinearLayout = findViewById(R.id.tab_container);
        mNavigationView.setNavigationItemSelectedListener(item -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Log.e(TAG, "===> getFragments.size = " + getSupportFragmentManager().getFragments().size());
            switch (item.getItemId()) {
                case R.id.navigation_item_movies:
                case R.id.navigation_item_book:
                    if (mLinearLayout.getVisibility() == View.GONE) {
                        mLinearLayout.setVisibility(View.VISIBLE);
                    }
                    getSupportFragmentManager().getFragments().forEach(fragment -> {
                        if (fragment.getTag().equals(ConstContent.MENU_BLOG) || fragment.getTag().equals(ConstContent.MENU_ABOUT)) {
                            transaction.hide(fragment);
                        } else {
                            transaction.show(fragment);
                        }
                    });
                    break;
                case R.id.navigation_item_blog:
                case R.id.navigation_item_about:
                    if (mLinearLayout.getVisibility() == View.VISIBLE) {
                        mLinearLayout.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
            switch (item.getItemId()) {
                case R.id.navigation_item_movies:
                    mViewPager.setCurrentItem(ConstContent.TAB_INDEX_MOVIES);
                    break;
                case R.id.navigation_item_book:
                    mViewPager.setCurrentItem(ConstContent.TAB_INDEX_BOOK);
                    break;
                case R.id.navigation_item_about:
                case R.id.navigation_item_blog:
                    getSupportFragmentManager().getFragments().forEach(fragment -> {
                        String fragmentTag = fragment.getTag();
                        if ((item.getItemId() == R.id.navigation_item_blog && fragmentTag.equals(ConstContent.MENU_BLOG)) ||
                                (item.getItemId() == R.id.navigation_item_about && fragmentTag.equals(ConstContent.MENU_ABOUT))) {
                            transaction.show(fragment);
                        } else {
                            transaction.hide(fragment);
                        }

                    });
                    break;
                case R.id.navigation_item_login:
                case R.id.navigation_item_logout:
                default:
                    break;

            }
            transaction.commit();
            item.setCheckable(true);
            mDrawerLayout.closeDrawers();
            return true;
        });

    }

    private void setupNavigationHeader(){
        View headView = mNavigationView.inflateHeaderView(R.layout.navigation_header);
        ImageView  profileView= headView.findViewById(R.id.img_profile_photo);
        TextView profileName= headView.findViewById(R.id.txt_profile_name);

        int size = getResources().getDimensionPixelOffset(R.dimen.profile_avatar_size);
        int width = getResources().getDimensionPixelOffset(R.dimen.profile_avatar_border);
        int color= getResources().getColor(R.color.color_profile_photo_border);

        Picasso.with(this)
                .load(R.mipmap.dayuhaitang)
                .resize(size,size)
                .transform(new CircleTransformation(width, color))
                .into(profileView);
        if (profileView !=null){
            profileView.setOnClickListener(view -> {
                Log.e(TAG, "===> onClick....!");
                mDrawerLayout.closeDrawers();
                mNavigationView.getMenu().getItem(0).setCheckable(true);
            });
        }

    }

    private void initOthersFragment() {
        //init blog fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        BlogFragment jianshuFragment = BlogFragment.newInstance();
        AboutFragment aboutFragment = AboutFragment.newInstance();

        transaction.add(R.id.frame_container, jianshuFragment, ConstContent.MENU_BLOG);
        transaction.add(R.id.frame_container, aboutFragment, ConstContent.MENU_ABOUT);
        transaction.commit();

        createOtherPresenter(jianshuFragment);

    }

    private void createOtherPresenter(BlogContract.View blogFragment) {
        Log.d(TAG, "createOtherPresenter");
        new BlogPresenter(DoubanManager.createDoubanService(), blogFragment);
    }

    private void initTabLayout() {
        TabLayout tabLayout = findViewById(R.id.douban_sliding_tabs);
        if (tabLayout != null) {
            tabLayout.addTab(tabLayout.newTab());
            tabLayout.addTab(tabLayout.newTab());
            tabLayout.setupWithViewPager(mViewPager);
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        DoubanPagerAdapter pagerAdapter = new DoubanPagerAdapter(getSupportFragmentManager());
        MoviesFragment moviesFragment = MoviesFragment.newInstance();
        BooksFragment booksFragment = BooksFragment.newInstance();
        Log.e(TAG, "setupViewPager, moviesFragment = " + moviesFragment);
        Log.e(TAG, "setupViewPager, bookFragment = " + booksFragment);
        pagerAdapter.addFragment(moviesFragment, "电影");
        pagerAdapter.addFragment(booksFragment, "书籍");
        viewPager.setAdapter(pagerAdapter);
        createPresenter(moviesFragment, booksFragment);
    }

    private void createPresenter(MoviesContract.View moviesFragment, BooksContract.View booksFragment) {
        Log.e(TAG, "createPresenter,fragmentView = " + moviesFragment);
        Log.e(TAG, "createPresenter,fragmentView = " + booksFragment);
        MoviesPresenter moviesPresenter = new MoviesPresenter(DoubanManager.createDoubanService(), moviesFragment);
        BooksPresenter booksPresenter = new BooksPresenter(DoubanManager.createDoubanService(), booksFragment);
    }

    static class DoubanPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFrgmentTitles = new ArrayList<>();

        public DoubanPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFrgmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFrgmentTitles.get(position);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
