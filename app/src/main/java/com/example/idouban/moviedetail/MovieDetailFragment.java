package com.example.idouban.moviedetail;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.idouban.ConstContent;
import com.example.idouban.R;
import com.example.idouban.moviewebsite.WebViewActivity;

import static com.example.idouban.HomeActivity.TAG;

public class MovieDetailFragment  extends Fragment implements View.OnClickListener {
    private String mUrl;
    public MovieDetailFragment(){}
    public static MovieDetailFragment createInstance(String info,int type){
        MovieDetailFragment movieDetailFragment=new MovieDetailFragment();
        Bundle args=new Bundle();
        args.putString(ConstContent.INTENT_EXTRA_FRAGMENT_INFO,info);
        args.putInt(ConstContent.INTENT_EXTRA_FRAGMENT_TYPE,type);
        movieDetailFragment.setArguments(args);
        return movieDetailFragment;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvInfo:
                Log.e(TAG, "onClick: "+"===> website click!!!!");
                Intent intent=new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra(ConstContent.INTENT_EXTRA_WEBSITE_URL,mUrl);
                startActivity(intent);
                break;
                default:break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_movie_detail,container,false);
        TextView textInfo=view.findViewById(R.id.tvInfo);
        textInfo.setText(getArguments().getString(ConstContent.INTENT_EXTRA_FRAGMENT_INFO));
        if(ConstContent.TYPE_MOVIE_WEBSITE==getArguments().getInt(ConstContent.INTENT_EXTRA_FRAGMENT_TYPE)){
            textInfo.setOnClickListener(this);
            mUrl=textInfo.getText().toString();
        }
        return view;
    }
}
