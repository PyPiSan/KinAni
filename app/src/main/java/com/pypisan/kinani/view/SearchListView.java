package com.pypisan.kinani.view;

import static android.app.appsearch.AppSearchResult.RESULT_OK;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pypisan.kinani.R;
import com.pypisan.kinani.adapter.SearchViewAdapter;
import com.pypisan.kinani.api.RequestModule;
import com.pypisan.kinani.model.AnimeModel;
import com.pypisan.kinani.model.AnimeRecentModel;
import com.pypisan.kinani.storage.AnimeManager;
import com.pypisan.kinani.storage.Constant;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchListView extends Fragment implements SearchViewAdapter.SelectListener {

    // Add RecyclerView member
    private ArrayList<AnimeModel> animeSearchList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapterSearch;

    private BottomNavigationView bottomAppBar;
    private EditText editText;
    private ImageView ivClearText;
    private ImageButton voice_search_button;

    private boolean loaderState;
    private ProgressBar progressBar;
    private static final int REQ_CODE_SPEECH_INPUT = 0;
    private String showType;
    public SearchListView() {
        // Required empty public constructor
    }

    public static SearchListView newInstance() {
        return new SearchListView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.search_list_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loaderState = false;
        showType= "anime";
        bottomAppBar = getActivity().findViewById(R.id.bottomAppBar);
        bottomAppBar.setVisibility(View.GONE);
        voice_search_button = view.findViewById(R.id.search_bar_voice_icon);
        editText = view.findViewById(R.id.search_bar_edit_text);
        ivClearText = view.findViewById(R.id.iv_clear_text);
        ImageButton backButton = view.findViewById(R.id.back_button);
        progressBar = view.findViewById(R.id.loadSearch);

//      initialization recycler

        recyclerView = view.findViewById(R.id.search_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(false);

//      adapterSearch = new SearchViewAdapter(animeSearchList, getContext(), new SearchListView()::onItemClicked);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String searchString = String.valueOf(editText.getText());
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                if (!searchString.equals("")) {
                    if (v != null) {
                        try {
                            InputMethodManager imm = (InputMethodManager) getActivity()
                                    .getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        } catch (Exception ignored) {
                        }
                    }
                    if (!loaderState) {
                        if (animeSearchList != null && animeSearchList.size() > 0) {
                            adapterSearch.notifyItemRangeRemoved(0, animeSearchList.size());
                            recyclerView.setAdapter(null);
                            bottomAppBar.setVisibility(View.GONE);
                            loaderState = false;
                        }
                        progressBar.setVisibility(View.VISIBLE);
                        loaderState = true;
                    }
                    insertDataToCard(searchString, showType);
                } else {
                    Toast.makeText(getContext(), "Please Enter the Title", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
                }
            }
        );
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });

        /*hide/show clear button in search view*/
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    ivClearText.setVisibility(View.GONE);
                    voice_search_button.setVisibility(View.VISIBLE);
                } else {
                    ivClearText.setVisibility(View.VISIBLE);
                    voice_search_button.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        Voice Search Listener
        voice_search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }

        });

        ivClearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                if (animeSearchList != null && animeSearchList.size()>0) {
                    adapterSearch.notifyItemRangeRemoved(0, animeSearchList.size());
                    recyclerView.setAdapter(null);
                    bottomAppBar.setVisibility(View.GONE);
                    loaderState = false;
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

//  Insert data to card
    private void insertDataToCard(String searchString, String searchDb) {
//      Add the cards data and display them
        String url = Constant.baseUrl;
        if (searchDb.equals("drama")){
            url = Constant.baseDramaUrl;
        }
        animeSearchList = new ArrayList<>();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestModule animeRecent = retrofit.create(RequestModule.class);
        Call<AnimeRecentModel> call = animeRecent.searchAnime(Constant.key,searchString);

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
                                animes.getTitle(), animes.getReleased(), showType, animes.getReleaseStatus());
                        animeSearchList.add(model);

                    }
                    progressBar.setVisibility(View.GONE);
                    loaderState = false;
                    if (animeSearchList.size()!=0){
                    recyclerView.setVisibility(View.VISIBLE);
                    adapterSearch = new SearchViewAdapter(animeSearchList, getContext(), SearchListView.this::onItemClicked);
                    recyclerView.setAdapter(adapterSearch);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    bottomAppBar.setVisibility(View.VISIBLE);
                    }else{
                        Toast.makeText(getContext(), "Try with different name", Toast.LENGTH_LONG).show();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Content Not Found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AnimeRecentModel> call, Throwable t) {
                Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onItemClicked(String title, String detail, String image) {
        AnimeManager animeManager = new AnimeManager(getContext());
        animeManager.open();
        animeManager.insertRecent(detail, title, image, showType);
        animeManager.close();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("image", image);
        bundle.putString("type",showType);
        Fragment fragment = new SummaryView();
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentView, fragment, "summary_view")
                        .addToBackStack(null)
                        .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        bottomAppBar.setVisibility(View.VISIBLE);
//        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    editText.setText(result.get(0));
                }
                break;
            }

        }
    }
}