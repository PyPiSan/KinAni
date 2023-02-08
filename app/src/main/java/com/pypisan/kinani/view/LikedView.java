package com.pypisan.kinani.view;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
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

import com.pypisan.kinani.R;
import com.pypisan.kinani.adapter.RecentAdapter;
import com.pypisan.kinani.model.AnimeModel;
import com.pypisan.kinani.storage.AnimeBase;
import com.pypisan.kinani.storage.AnimeManager;

import java.util.ArrayList;

public class LikedView extends Fragment implements RecentAdapter.SelectListener {

    private ArrayList<AnimeModel> animeList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private AnimeManager animeManager;

    public LikedView() {
        // Required empty public constructor
    }

    public static LikedView newInstance() {
        return new LikedView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.liked_view, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        Data Initialization
        likedList();

//      initialization recycler

        recyclerView = view.findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setHasFixedSize(true);


//        Item Declaration

//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(4), true));
        adapter = new RecentAdapter(getContext(), animeList, this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
            }

//       Fetching Liked list from DB

    private void likedList(){
        animeList = new ArrayList<>();
        animeManager = new AnimeManager(getContext());
        animeManager.open();
        Cursor cursor = animeManager.readAllDataLiked();
        if (cursor.getCount() == 0){
            Toast.makeText(getContext(), "Nothing to show", Toast.LENGTH_SHORT).show();
        }else {
            AnimeModel model;
//            Log.d("C1", "anime list is " + cursor.getCount());
            while (cursor.moveToNext()) {
                model = new AnimeModel(cursor.getString(3), cursor.getString(1), cursor.getString(2), "");
                animeList.add(model);
//                Log.d("C4", "anime list is " + animeList.size());
            }
        }
    }

    // convert dp to pixels
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onItemClicked(String title, String detail, String image) {
        animeManager = new AnimeManager(getContext());
        animeManager.open();
        animeManager.insertRecent(detail, title, image);
        animeManager.close();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
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