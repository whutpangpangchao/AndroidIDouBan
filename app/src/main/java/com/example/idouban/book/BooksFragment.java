package com.example.idouban.book;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.idouban.HomeActivity;
import com.example.idouban.R;
import com.example.idouban.beans.Book;
import com.example.idouban.utils.EndlessRecyclerViewScrollListener;
import com.example.idouban.utils.ScrollChildSwipeRefreshLayout;
import com.google.android.material.circularreveal.CircularRevealLinearLayout;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class BooksFragment extends Fragment implements BooksContract.View {
    private static final String TAG = BooksFragment.class.getSimpleName();
    private BooksContract.Presenter mPresenter;
    private RecyclerView mBookRecyclerView;
    private View mNoBooksView;
    private BookAdapter mBookAdapter;
    private List<Book> mAdapterBooksData;

    public BooksFragment() {
        // Required empty public constructor
    }

    public static BooksFragment newInstance() {
        return new BooksFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBookAdapter = new BookAdapter(new ArrayList<>(0), R.layout.recyclerview_book_item);
        mAdapterBooksData = new ArrayList<>();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mPresenter != null) {
            mPresenter.start();
        }

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (mPresenter != null) {
            mPresenter.start();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_books, container, false);
        mBookRecyclerView = view.findViewById(R.id.recycler_books);
        mNoBooksView = view.findViewById(R.id.ll_no_books);
        if (mBookRecyclerView != null) {
            mBookRecyclerView.setHasFixedSize(true);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            mBookRecyclerView.setLayoutManager(layoutManager);
            mBookRecyclerView.setAdapter(mBookAdapter);
            final ScrollChildSwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.book_refresh_layout);
            swipeRefreshLayout.setColorSchemeColors(
                    ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                    ContextCompat.getColor(getActivity(), R.color.colorAccent),
                    ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
            );
            swipeRefreshLayout.setScrollUpChild(mBookRecyclerView);
            swipeRefreshLayout.setOnRefreshListener(() -> {
                Log.e(HomeActivity.TAG, "\n\n onRefresh loadRefreshedBooks...");
                mPresenter.loadRefreshedBooks(true);
            });
            EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    Log.e(TAG, "page: " + page + ", totalItemsCount: " + totalItemsCount);
                    mPresenter.loadMoreBooks(totalItemsCount);
                }
            };
            mBookRecyclerView.addOnScrollListener(endlessRecyclerViewScrollListener);

        }
        return view;
    }

    @Override
    public void showRefreshedBooks(List<Book> books) {
        if (mAdapterBooksData.size() != 0 && books.get(0).getId().equals(mAdapterBooksData.get(0).getId())) {
            return;
        }
        mAdapterBooksData.addAll(books);
        mBookAdapter.replaceData(mAdapterBooksData);
        mBookRecyclerView.setVisibility(View.VISIBLE);
        mNoBooksView.setVisibility(View.GONE);
    }

    @Override
    public void showLoadedMoreBooks(List<Book> books) {
        mAdapterBooksData.addAll(books);
        mBookAdapter.replaceData(mAdapterBooksData);
    }

    @Override
    public void showNoBooks() {
        mBookRecyclerView.setVisibility(View.GONE);
        mNoBooksView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNoLoadedMoreBooks() {
        Toast.makeText(getActivity().getApplicationContext(),"No more content...",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setRefreshedIndicator(boolean active) {
        if (getView()==null)return;;
        Log.e(TAG, "=> loading indicator "+active);
        final SwipeRefreshLayout swipeRefreshLayout=getView().findViewById(R.id.book_refresh_layout);
        swipeRefreshLayout.post(()->{
           Log.e(HomeActivity.TAG,"swipeRefreshLayout run() active: " + active);
           swipeRefreshLayout.setRefreshing(active);
        });

    }

    @Override
    public void setPresenter(BooksContract.Presenter presenter) {
        mPresenter = presenter;
    }

    static class BookAdapter extends RecyclerView.Adapter<BookViewHolder> {

        private List<Book> mBooks;
        @LayoutRes
        private int layoutResId;

        public BookAdapter(@NonNull List<Book> books, @LayoutRes int layoutResId) {
            setList(books);
            this.layoutResId = layoutResId;
        }

        private void setList(List<Book> books) {
            this.mBooks = checkNotNull(books);
        }

        @NonNull
        @Override
        public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
            return new BookViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
            if (holder == null) return;
            holder.updateBook(mBooks.get(position));

        }

        @Override
        public int getItemCount() {
            return mBooks.size();
        }

        public void replaceData(List<Book> books) {
            setList(books);
            notifyDataSetChanged();
        }
    }

    static class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cardView;
        ImageView bookImage;
        TextView bookTitle;
        TextView bookAuthor;
        TextView bookSubTitle;
        TextView bookPubDate;
        TextView bookPages;
        TextView bookPrice;
        Book book;

        public BookViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardview);
            bookImage = (ImageView) itemView.findViewById(R.id.book_cover);
            bookTitle = (TextView) itemView.findViewById(R.id.txt_title);
            bookAuthor = (TextView) itemView.findViewById(R.id.txt_author);
            bookSubTitle = (TextView) itemView.findViewById(R.id.txt_subTitle);
            bookPubDate = (TextView) itemView.findViewById(R.id.txt_pubDate);
            bookPrice = (TextView) itemView.findViewById(R.id.txt_prices);
            bookPages = (TextView) itemView.findViewById(R.id.txt_pages);

            itemView.setOnClickListener(this);
        }

        public void updateBook(Book book) {

            if (book == null) return;
            this.book = book;

            Context context = itemView.getContext();
            if (context == null) return;

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

            if (book == null) return;
            if (itemView == null) return;

            Context context = itemView.getContext();
            if (context == null) return;

//            Intent intent = new Intent(context, BookDetailActivity.class);
//            intent.putExtra(ConstContent.INTENT_EXTRA_BOOK, book);
//
//            if (context instanceof Activity) {
//                Activity activity = (Activity) context;
//
//                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, bookImage, "cover").toBundle();
//                ActivityCompat.startActivity(activity, intent, bundle);
//            }
        }
    }
}
