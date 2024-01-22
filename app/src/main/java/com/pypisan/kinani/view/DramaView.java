package com.pypisan.kinani.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
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

public class DramaView extends Fragment implements RecentAdapter.SelectListener {

    private ArrayList<AnimeModel> dramaList;
    private RecyclerView recyclerView;
    private RecentAdapter adapterDrama;
    private ShimmerFrameLayout containerDrama;

    private LottieAnimationView errorPage;
    private int pageNumber;
    private boolean lastPage, loading = false;
    private int firstVisibleItem, totalItemCount;
    private Parcelable recyclerViewState;

    public DramaView() {
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
        return inflater.inflate(R.layout.liked_view, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//      errorPage = view.findViewById(R.id.animationView);
        dramaList = new ArrayList<>();
        containerDrama = view.findViewById(R.id.shimmer_drama_layout);
        containerDrama.startShimmer();
        pageNumber = 1;
        insertDataToCard(String.valueOf(pageNumber));


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
                    insertDataToCard(String.valueOf(pageNumber));
                }
            }
        });
    }

//       Fetching Liked list from DB

//    private void likedList(){
//        dramaList = new ArrayList<>();
//        animeManager = new AnimeManager(getContext());
//        animeManager.open();
//        Cursor cursor = animeManager.readAllDataLiked();
//        if (cursor.getCount() == 0){
//            Toast.makeText(getContext(), "Nothing to show", Toast.LENGTH_SHORT).show();
//            animeManager.close();
//        }else {
//            AnimeModel model;
////            Log.d("C1", "anime list is " + cursor.getCount());
//            while (cursor.moveToNext()) {
//                model = new AnimeModel(cursor.getString(3), cursor.getString(1), cursor.getString(2), "");
//                dramaList.add(model);
////                Log.d("C4", "anime list is " + animeList.size());
//                errorPage.setVisibility(View.GONE);
//                recyclerView.setVisibility(View.VISIBLE);
//            }
//            animeManager.close();
//        }
//    }

    private void insertDataToCard(String pageNum) {
//      Add the cards data and display them
//      fetching data
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.baseDramaUrl)
                .addConverterFactory(GsonConverterFactory
                        .create())
                .build();

        RequestModule moviesList = retrofit.create(RequestModule.class);
        Call<AnimeRecentModel> call = moviesList.getMovies(Constant.key, pageNum);
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
                    for (AnimeRecentModel.datum animes : data) {
                        model = new AnimeModel(animes.getImageLink(),
                                animes.getAnimeDetailLink(),
                                animes.getTitle(),
                                animes.getReleased());
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
                    recyclerView.setVisibility(View.VISIBLE);
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