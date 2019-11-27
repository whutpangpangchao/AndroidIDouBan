package com.example.idouban.base;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseViewPagerAdapter extends FragmentPagerAdapter {

    protected final List<Fragment> mFragments = new ArrayList<>();
    protected final List<String> mFragmentTitles = new ArrayList<>();

    public BaseViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public abstract void addFragment(Fragment fragment, String title);

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
        return mFragmentTitles.get(position);
    }
}
