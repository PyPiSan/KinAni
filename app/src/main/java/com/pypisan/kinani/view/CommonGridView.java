package com.pypisan.kinani.view;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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

public class CommonGridView extends Fragment implements RecentAdapter.SelectListener {


    private ShimmerFrameLayout shimmerContainer;
    private ArrayList<AnimeModel> animeList;
    private RecyclerView recyclerView;

    private int pageNumber;
    private boolean loading;
    private boolean lastPage;
    private int firstVisibleItem, totalItemCount;

    private RecentAdapter commonAdapter;
    private Parcelable recyclerViewState;
    private AnimeManager animeManager;
    private String tag;
    private AdView bannerAdTop;

    public CommonGridView() {
        // Required empty public constructor
    }
    public static CommonGridView newInstance() {
        return new CommonGridView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.common_grid_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        animeList = new ArrayList<>();
        tag = getArguments().getString("view");
        bannerAdTop =  view.findViewById(R.id.banner);
        shimmerContainer = view.findViewById(R.id.shimmer_common_layout);
        shimmerContainer.startShimmer();
        lastPage=false;
        loading = false;

        recyclerView = view.findViewById(R.id.common_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (commonAdapter.getItemViewType(position)){
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
        commonAdapter = new RecentAdapter(getContext(), animeList, this);
// Data initialization
        pageNumber = 1;
        if (tag.equals("watchlist")){
            animeManager = new AnimeManager(getContext());
            animeManager.open();
            Cursor cursor = animeManager.readAllDataLiked();
            if (cursor.getCount() != 0){
                AnimeModel model;
                while (cursor.moveToNext()) {
                    model = new AnimeModel(cursor.getString(3),
                            cursor.getString(1), cursor.getString(2), "",
                            cursor.getString(4),"");
                    animeList.add(model);
                }
                loading=false;
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(commonAdapter);
                animeManager.close();
                lastPage = true;
                if (Constant.isFree){
                bannerAdTop.loadAd(new AdRequest.Builder().build());
                }
            }

        }else{insertDataToCard(String.valueOf(pageNumber), tag);}

//      initialization recycler

//        Item Declaration

//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(4), true))
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
                    insertDataToCard(String.valueOf(pageNumber),tag);
                }
            }
        });
    }

    @Override
    public void onItemClicked(String title, String detail, String image) {
        animeManager = new AnimeManager(getContext());
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

    private void insertDataToCard(String pageNum, String tag) {
//        Add the cards data and display them
        Call<AnimeRecentModel> call;
//        fetching data
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        
        if (tag.equals("trending")) {
            RequestModule animeTrend = retrofit.create(RequestModule.class);
            call = animeTrend.getTrending(Constant.key, pageNum);
        } else {
            RequestModule animeRecommend = retrofit.create(RequestModule.class);
            call = animeRecommend.getRecommend(Constant.key,pageNum);
        }
        if (Integer.parseInt(pageNum)>1) {
            commonAdapter.addNullData();
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
                        model = new AnimeModel(animes.getImageLink(), animes.getAnimeDetailLink(),
                                animes.getTitle(), animes.getReleased(),"anime","");
                        animeList.add(model);

                    }
                }
                commonAdapter.notifyItemInserted(resource.getResultSize());
                if (Integer.parseInt(pageNum)>1) {
                    commonAdapter.removeNull((Integer.parseInt(pageNum)-1)*30);
                }
                if (resource.getResultSize()<30){
                    lastPage = true;
                }
                loading=false;
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(commonAdapter);
                recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                if (Constant.isFree){
                    bannerAdTop.loadAd(new AdRequest.Builder().build());
                }
            }

            @Override
            public void onFailure(Call<AnimeRecentModel> call, Throwable t) {
//                Log.d("Hey3", "Response code is : 400" + t.getMessage());
            }
        });
    }
}