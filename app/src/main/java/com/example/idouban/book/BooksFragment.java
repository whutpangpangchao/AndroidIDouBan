package com.example.idouban.book;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.idouban.HomeActivity;
import com.example.idouban.R;
import com.example.idouban.base.BaseFragment;
import com.example.idouban.base.BaseRecycleViewAdapter;
import com.example.idouban.base.BaseRecycleViewHolder;
import com.example.idouban.beans.Book;
import com.example.idouban.bookdetail.BookDetailActivity;
import com.example.idouban.utils.ConstContent;
import com.example.idouban.utils.EndlessRecyclerViewScrollListener;
import com.example.idouban.utils.ScrollChildSwipeRefreshLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 */
public class BooksFragment extends BaseFragment<Book> implements BooksContract.View {
    private static final String TAG = BooksFragment.class.getSimpleName();
    private BooksContract.Presenter mPresenter;
    private View mNoBooksView;
    private ScrollChildSwipeRefreshLayout mSwipeRefreshLayout;
    private EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;

    public BooksFragment() {
        // Required empty public constructor
    }

    public static BooksFragment newInstance() {
        return new BooksFragment();
    }

    //TODO Fragment已经关联到Activity，这个时候 Activity已经传进来了， 获得Activity的传递的值就可以进行与activity的通信
    // ， 当然也可以使用getActivity(),前提是Fragment已经和宿主Activity关联，并且没有脱离，有且只调用一次。
    @Override
    protected void initVariables() {
        Log.e(TAG, "initVariables:  OnCreate() -> initVariables");
        mAdapterData = new ArrayList<>();
    }

    @Override
    protected void initRecycleView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "initRecycleView: " + "OnCreateView() -> initRecycleView");
        mView = inflater.inflate(R.layout.fragment_books, container, false);
        mSwipeRefreshLayout = mView.findViewById(R.id.book_refresh_layout);
        mRecyclerView = mView.findViewById(R.id.recycler_books);
        mNoBooksView = mView.findViewById(R.id.ll_no_books);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void initRecycleViewAdapter() {
        Log.e(TAG, "initRecycleViewAdapter: OnCreate() -> initRecycleViewAdapter");
        mAdapter = new BaseRecycleViewAdapter<>(new ArrayList<>(0),
                R.layout.recyclerview_book_item,
                BookViewHolder::new
        );
    }

    @Override
    protected void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        mSwipeRefreshLayout.setScrollUpChild(mRecyclerView);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            Log.e(HomeActivity.TAG, "\n\n onRefresh loadRefreshedBooks...");
            mPresenter.loadRefreshedBooks(true);
        });
    }

    @Override
    protected void initEndlessScrollListener() {
         mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.e(TAG, "page: " + page + ", totalItemsCount: " + totalItemsCount);
                mPresenter.loadMoreBooks(totalItemsCount);
            }
        };
        mRecyclerView.addOnScrollListener(mEndlessRecyclerViewScrollListener);
    }

    @Override
    protected void startPresenter() {
        if (mPresenter != null) {
            mPresenter.start();
        }
    }

    // TODO: 2019/11/25  执行该方法时，与Fragment绑定的Activity的onCreate方法已经执行完成并返回，在该方法内可以进行与Activity交互的UI操作，
    //  所以在该方法之前Activity的onCreate方法并未执行完成，如果提前进行交互操作，会引发空指针异常。
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mPresenter != null) {
            mPresenter.start();
        }

    }

    //TODO :展示刷新后的RecyclerView
    @Override
    public void showRefreshedBooks(List<Book> books) {
        if (mAdapterData.size() != 0 && books.get(0).getId().equals(mAdapterData.get(0).getId())) {
            return;
        }
        mAdapterData.addAll(books);
        mAdapter.replaceData(mAdapterData);
        mRecyclerView.setVisibility(View.VISIBLE);
        mNoBooksView.setVisibility(View.GONE);
    }

    @Override
    public void showLoadedMoreBooks(List<Book> books) {
        mAdapterData.addAll(books);
        mAdapter.replaceData(mAdapterData);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showNoBooks() {
        mRecyclerView.setVisibility(View.GONE);
        mNoBooksView.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showNoLoadedMoreBooks() {
        Toast.makeText(getActivity().getApplicationContext(), "No more content...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setRefreshedIndicator(boolean active) {
        if (getView() == null) return;
        Log.e(TAG, "=> loading indicator " + active);
        final SwipeRefreshLayout swipeRefreshLayout = getView().findViewById(R.id.book_refresh_layout);
        swipeRefreshLayout.post(() -> {
            Log.e(HomeActivity.TAG, "swipeRefreshLayout run() active: " + active);
            swipeRefreshLayout.setRefreshing(active);
        });
    }

    @Override
    public void setPresenter(BooksContract.Presenter presenter) {
        mPresenter = presenter;
    }

    static class BookViewHolder extends BaseRecycleViewHolder<Book> implements View.OnClickListener {
        //TODO item内的子控件
        CardView cardView;
        ImageView bookImage;
        TextView bookTitle;
        TextView bookAuthor;
        TextView bookSubTitle;
        TextView bookPubDate;
        TextView bookPages;
        TextView bookPrice;

        public BookViewHolder(View itemView) {
            //TODO view.findViewById操作
            super(itemView);
            cardView = itemView.findViewById(R.id.cardview);
            bookImage = itemView.findViewById(R.id.book_cover);
            bookTitle = itemView.findViewById(R.id.txt_title);
            bookAuthor = itemView.findViewById(R.id.txt_author);
            bookSubTitle = itemView.findViewById(R.id.txt_subTitle);
            bookPubDate = itemView.findViewById(R.id.txt_pubDate);
            bookPrice = itemView.findViewById(R.id.txt_prices);
            bookPages = itemView.findViewById(R.id.txt_pages);

            itemView.setOnClickListener(this);
        }

        @Override
        protected void onBindItem(Book book) {

            Context context = itemView.getContext();
            if (book == null || context == null) return;

            //get the prefix string
            String prefixSubTitle = context.getString(R.string.prefix_subtitle);
            String prefixAuthor = context.getString(R.string.prefix_author);
            String prefixPubDate = context.getString(R.string.prefix_pubdata);
            String prefixPages = context.getString(R.string.prefix_pages);
            String prefixPrice = context.getString(R.string.prefix_price);

            bookTitle.setText(book.getTitle());
            bookAuthor.setText(String.format(prefixAuthor, book.getAuthor()));
            bookSubTitle.setText(String.format(prefixSubTitle, book.getSubtitle()));
            bookPubDate.setText(String.format(prefixPubDate, book.getPubdate()));
            bookPages.setText(String.format(prefixPages, book.getPages()));
            bookPrice.setText(String.format(prefixPrice, book.getPrice()));

            Picasso.with(context)
                    .load(book.getImages().getLarge())
                    .placeholder(context.getResources().getDrawable(R.mipmap.ic_launcher))
                    .into(bookImage);


        }


        @Override
        public void onClick(View v) {
            Log.e(HomeActivity.TAG, "==>Book onClick....Item");

            if (itemContent == null) return;
            if (itemView == null) return;

            Context context = itemView.getContext();
            if (context == null) return;
            Intent intent = new Intent(context, BookDetailActivity.class);
            intent.putExtra(ConstContent.INTENT_EXTRA_BOOK, itemContent);

            if (context instanceof Activity) {
                Activity activity = (Activity) context;

                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, bookImage, "cover").toBundle();
                ActivityCompat.startActivity(activity, intent, bundle);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapterData.clear();
//        mPresenter.cancelRetrofitRequest();
        mPresenter.unSubscribe();
        Log.e(HomeActivity.TAG, TAG + "=> onDestroy()!!!");
    }
}
