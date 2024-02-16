package com.pypisan.kinani.view;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class TrendingView extends Fragment implements RecentAdapter.SelectListener {

    // Add RecyclerView member
    private ArrayList<AnimeModel> animeList;
    private RecentAdapter adapter;

    public TrendingView() {
        // Required empty public constructor
    }

    public static TrendingView newInstance() {
        return new TrendingView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.trending_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//      Data initialization

        insertDataToCard();

//      initialization recycler

        RecyclerView recyclerView = view.findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setHasFixedSize(false);

//      Item Declaration
        adapter = new RecentAdapter(getContext(), animeList, this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        }

//  Insert data to card
    private void insertDataToCard() {
        String pageNum = "1";
//  Add the cards data and display them
//  fetching data
    animeList = new ArrayList<>();
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constant.baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    RequestModule animeTrending = retrofit.create(RequestModule.class);
    Call<AnimeRecentModel> call = animeTrending.getTrending(Constant.key,pageNum);

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
                            animes.getTitle(), animes.getReleased(),"anime");
                    animeList.add(model);
                    adapter.notifyDataSetChanged();

                }
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
        animeManager.insertRecent(detail, title, image, "anime");
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
}