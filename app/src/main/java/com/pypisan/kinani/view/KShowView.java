package com.pypisan.kinani.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.pypisan.kinani.storage.Constant;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class KShowView extends Fragment implements RecentAdapter.SelectListener {

    private ArrayList<AnimeModel> dramaList;
    private RecyclerView recyclerView;
    private RecentAdapter adapterDrama;
    private ShimmerFrameLayout containerDrama;
    private int pageNumber;
    private String viewType = "movies";
    private boolean lastPage, loading = false;
    private int firstVisibleItem, totalItemCount;
    private Parcelable recyclerViewState;
    private LinearLayout viewSelector;

    public KShowView() {
//    Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//      Inflate the layout for this fragment
        return inflater.inflate(R.layout.kshow_view, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dramaList = new ArrayList<>();
        containerDrama = view.findViewById(R.id.shimmer_drama_layout);
        containerDrama.startShimmer();
        viewSelector = view.findViewById(R.id.viewSelector);
        TextView trendingDramaButton = view.findViewById(R.id.trendingDramaButton);
        TextView moviesDramaButton = view.findViewById(R.id.moviesDramaButton);
        TextView topDramaButton = view.findViewById(R.id.topDramaButton);
        pageNumber = 1;
        insertDataToCard(String.valueOf(pageNumber), viewType);
        trendingDramaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewType = "trending";
                containerDrama.setVisibility(View.VISIBLE);
                containerDrama.startShimmer();
                dramaList.clear();
                adapterDrama.notifyItemRangeRemoved(0,(pageNumber)*30);
                recyclerView.setAdapter(null);
                pageNumber = 1;
                insertDataToCard(String.valueOf(pageNumber), viewType);
            }
        });

        moviesDramaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewType = "movies";
                containerDrama.setVisibility(View.VISIBLE);
                containerDrama.startShimmer();
                dramaList.clear();
                adapterDrama.notifyItemRangeRemoved(0,(pageNumber)*30);
                recyclerView.setAdapter(null);
                pageNumber = 1;
                insertDataToCard(String.valueOf(pageNumber), viewType);
            }
        });

        topDramaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewType = "top";
                containerDrama.setVisibility(View.VISIBLE);
                containerDrama.startShimmer();
                dramaList.clear();
                adapterDrama.notifyItemRangeRemoved(0,(pageNumber)*30);
                recyclerView.setAdapter(null);
                pageNumber = 1;
                insertDataToCard(String.valueOf(pageNumber), viewType);
            }
        });


//      initialization recycler

        recyclerView = view.findViewById(R.id.drama_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapterDrama.getItemViewType(position)){
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


//      Item Declaration
        adapterDrama = new RecentAdapter(getContext(), dramaList, this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapterDrama);

//      infinite Scroller
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
                    insertDataToCard(String.valueOf(pageNumber), viewType);
                }
            }
        });
    }

//       Fetching Liked list from DB

    private void insertDataToCard(String pageNum, String viewName) {
//      Add the cards data and display them
//      fetching data
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.baseDramaUrl)
                .addConverterFactory(GsonConverterFactory
                        .create())
                .build();
        RequestModule contentList = retrofit.create(RequestModule.class);
        Call<AnimeRecentModel> call;
        switch (viewName) {
            case "movies":
                call = contentList.getMovies(Constant.key, pageNum);
                break;
            case "trending":
                call = contentList.getTrending(Constant.key, pageNum);
                break;
            case "top":
                call = contentList.getRecommend(Constant.key, pageNum);
                break;
            default:
                call = contentList.getMovies(Constant.key, pageNum);
                break;
        }
        if (Integer.parseInt(pageNum)>1) {
            adapterDrama.addNullData();
        }
        call.enqueue(new Callback<AnimeRecentModel>() {
            @Override
            public void onResponse(Call<AnimeRecentModel> call, Response<AnimeRecentModel> response) {
                AnimeRecentModel resource = response.body();
                boolean status = resource.getSuccess();
                if (status) {
                    List<AnimeRecentModel.datum> data = resource.getData();
                    AnimeModel model;
                    for (AnimeRecentModel.datum drama : data) {
                        model = new AnimeModel(drama.getImageLink(),
                                drama.getAnimeDetailLink(),
                                drama.getTitle(),
                                drama.getReleased());
                        dramaList.add(model);
                    }
                    adapterDrama.notifyItemInserted(resource.getResultSize());
                    if (Integer.parseInt(pageNum)>1) {
                        adapterDrama.removeNull((Integer.parseInt(pageNum)-1)*30);
                    }
                    if (resource.getResultSize()<30){
                        lastPage = true;
                    }
                    loading=false;
                    containerDrama.stopShimmer();
                    containerDrama.setVisibility(View.GONE);
//                    recyclerView.setVisibility(View.VISIBLE);
                    viewSelector.setVisibility(View.VISIBLE);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(adapterDrama);
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
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("type", "drama");
        Fragment fragment = SummaryView.newInstance();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentView, fragment, "summary_view");
        transaction.addToBackStack(null);
        transaction.commit();
    }
}