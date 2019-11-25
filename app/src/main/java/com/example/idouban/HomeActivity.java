package com.example.idouban;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.idouban.api.DoubanManager;
import com.example.idouban.beans.Book;
import com.example.idouban.book.BooksContract;
import com.example.idouban.book.BooksFragment;
import com.example.idouban.book.BooksPresenter;
import com.example.idouban.movie.MoviesContract;
import com.example.idouban.movie.MoviesFragment;
import com.example.idouban.movie.MoviesPresenter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    public static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
       // new Thread(() -> Log.e(TAG, "run: "+"this is a runnable"));
        //Init Layout UI
        ViewPager viewPager =  findViewById(R.id.douban_view_pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.douban_sliding_tabs);

        if(tabLayout != null) {
            tabLayout.addTab(tabLayout.newTab());
            tabLayout.addTab(tabLayout.newTab());
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    private void setupViewPager(ViewPager viewPager){
        DoubanPagerAdapter pagerAdapter = new DoubanPagerAdapter(getSupportFragmentManager());
        MoviesFragment moviesFragment = MoviesFragment.newInstance();
        BooksFragment booksFragment=BooksFragment.newInstance();
        Log.e(TAG, "setupViewPager, moviesFragment = " + moviesFragment);
        Log.e(TAG, "setupViewPager, bookFragment = " + booksFragment);
        pagerAdapter.addFragment(moviesFragment, "电影");
        pagerAdapter.addFragment(booksFragment,"书籍");
        viewPager.setAdapter(pagerAdapter);
        createPresenter(moviesFragment,booksFragment);
    }
    private void createPresenter(MoviesContract.View moviesFragment, BooksContract.View booksFragment){
        Log.e(TAG, "createPresenter,fragmentView = "+moviesFragment);
        Log.e(TAG, "createPresenter,fragmentView = "+booksFragment);
        MoviesPresenter moviesPresenter=new MoviesPresenter(DoubanManager.createDoubanService(),moviesFragment);
        BooksPresenter booksPresenter=new BooksPresenter(DoubanManager.createDoubanService(),booksFragment);
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
