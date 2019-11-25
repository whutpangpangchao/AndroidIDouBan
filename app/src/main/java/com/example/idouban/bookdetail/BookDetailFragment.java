package com.example.idouban.bookdetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.idouban.R;
import com.example.idouban.utils.ConstContent;

public class BookDetailFragment extends Fragment {
    private static final String TAG = BookDetailFragment.class.getSimpleName();
    public BookDetailFragment(){}
    public static BookDetailFragment createInstance(String info,int type){
        BookDetailFragment bookDetailFragment=new BookDetailFragment();
        Bundle args =new Bundle();
        args.putString(ConstContent.INTENT_EXTRA_FRAGMENT_INFO,info);
        args.putInt(ConstContent.INTENT_EXTRA_FRAGMENT_TYPE,type);
        bookDetailFragment.setArguments(args);
        return bookDetailFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_book_detail,container,false);
        TextView textInfo=view.findViewById(R.id.tvInfo);
        textInfo.setText(getArguments().getString(ConstContent.INTENT_EXTRA_FRAGMENT_INFO));
        return view;
    }
}
