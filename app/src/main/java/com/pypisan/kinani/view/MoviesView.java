package com.pypisan.kinani.view;


import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.pypisan.kinani.R;
import com.pypisan.kinani.adapter.RecentAdapter;
import com.pypisan.kinani.api.RequestModule;
import com.pypisan.kinani.model.AnimeModel;
import com.pypisan.kinani.model.AnimeRecentModel;
import com.pypisan.kinani.storage.AnimeManager;
import com.pypisan.kinani.storage.Constant;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoviesView extends Fragment implements RecentAdapter.SelectListener {

    private ArrayList<AnimeModel> animeList;
    private RecyclerView recyclerView;
    private RecentAdapter adapterMovies;
    private ShimmerFrameLayout containerMovies;
    private int pageNumber;
    private boolean loading;
    private boolean lastPage;
    private int firstVisibleItem, totalItemCount;
    private Parcelable recyclerViewState;

    public MoviesView() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.movies_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        animeList = new ArrayList<>();
        containerMovies = view.findViewById(R.id.shimmer_movies_layout);
        containerMovies.startShimmer();
        pageNumber = 1;
        lastPage=false;
        loading = false;
        insertDataToCard(String.valueOf(pageNumber));

        //      initialization recycler

        recyclerView = view.findViewById(R.id.movie_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapterMovies.getItemViewType(position)){
                    case 0:
                        return 1;
                    case 1:
                        return 3;
                    default:
                        return -1;
                }
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(false);

//        Item Declaration
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(1), false));
        adapterMovies = new RecentAdapter(getContext(), animeList, this);

//        infinite Scroller
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                totalItemCount = gridLayoutManager.getItemCount();
                firstVisibleItem = gridLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!lastPage && !loading && firstVisibleItem == totalItemCount-1) {
                    recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
                    pageNumber +=1;
                    loading=true;
                    insertDataToCard(String.valueOf(pageNumber));
                }
            }
        });

    }

    private void insertDataToCard(String pageNum) {
//      Add the cards data and display them
//      fetching data
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.baseUrl)
                .addConverterFactory(GsonConverterFactory
                        .create())
                .build();

        RequestModule moviesList = retrofit.create(RequestModule.class);
        Call<AnimeRecentModel> call = moviesList.getMovies(Constant.key, pageNum);
        if (Integer.parseInt(pageNum)>1) {
            adapterMovies.addNullData();
        }
        call.enqueue(new Callback<AnimeRecentModel>() {
            @Override
            public void onResponse(Call<AnimeRecentModel> call, Response<AnimeRecentModel> response) {
                AnimeRecentModel resource = response.body();
                boolean status = resource.getSuccess();
                if (status) {
                    List<AnimeRecentModel.datum> data = resource.getData();
                    AnimeModel model;
                    for (AnimeRecentModel.datum animes : data) {
                        model = new AnimeModel(animes.getImageLink(),
                                animes.getAnimeDetailLink(),
                                animes.getTitle(),
                                animes.getReleased(),"anime","");
                        animeList.add(model);
                    }
                    adapterMovies.notifyItemInserted(resource.getResultSize());
                    if (Integer.parseInt(pageNum)>1) {
                        adapterMovies.removeNull((Integer.parseInt(pageNum)-1)*30);
                    }
                    if (resource.getResultSize()<30){
                        lastPage = true;
                    }
                    loading=false;
                    containerMovies.stopShimmer();
                    containerMovies.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(adapterMovies);
                    recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

                } else {
//                    Toast.makeText(this, "Response not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AnimeRecentModel> call, Throwable t) {
//            Log.d("Hey3", "Response code is : 400" + t.getMessage());
            }
        });
    }

    @Override
    public void onItemClicked(String title, String detail, String image) {
        AnimeManager animeManager = new AnimeManager(getContext());
        animeManager.open();
        animeManager.insertRecent(detail, title, image,"anime");
        animeManager.close();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("image", image);
        Fragment fragment = SummaryView.newInstance();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentView, fragment, "summary_view");
        transaction.addToBackStack(null);
        transaction.commit();

    }

    private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private final int spanCount;
        private final int spacing;
        private final boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;
            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;
                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1);
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }
}