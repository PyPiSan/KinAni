package com.pypisan.kinani.utils;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pypisan.kinani.R;
import com.pypisan.kinani.adapter.HomeViewAdapter;
import com.pypisan.kinani.adapter.SearchViewAdapter;
import com.pypisan.kinani.api.RequestModule;
import com.pypisan.kinani.model.AnimeModel;
import com.pypisan.kinani.model.AnimeRecentModel;
import com.pypisan.kinani.view.SearchListView;
import com.pypisan.kinani.view.SummaryView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchableActivity extends AppCompatActivity {

    private static final int REQ_CODE_SPEECH_INPUT = 0;
    private EditText editText;
    private ImageView ivClearText;
    private ImageButton backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        ImageButton voice_search_button = (ImageButton) findViewById(R.id.search_bar_voice_icon);
        editText = (EditText) findViewById(R.id.search_bar_edit_text);
        ivClearText = findViewById(R.id.iv_clear_text);
        backButton = findViewById(R.id.back_button);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Search view generate..
                String searchString = String.valueOf(editText.getText());
                searchClicked(searchString);
            }

        });
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
        //Voice Search Listener
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
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * Showing google speech input dialog
     */
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
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    editText.setText(result.get(0));
                    //TODO AVVIARE ATTIVITA' CON TESTO RESULT.GET(0)
                }
                break;
            }

        }
    }

    private void searchClicked(String searchString) {
        Bundle bundle = new Bundle();
        bundle.putString("searchString", searchString);
        Fragment fragment = SearchListView.newInstance();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.searchFragmentView, fragment, "search_list_view_fragment");
        transaction.addToBackStack(null);
        transaction.commit();
    }


}