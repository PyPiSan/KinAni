package com.pypisan.kinani.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pypisan.kinani.R;
import com.pypisan.kinani.storage.Constant;

public class UserPageView extends Fragment {

    private Boolean aboutClick = false;
    private Boolean notificationClick = false;

    public UserPageView() {
        // Required empty public constructor
    }
    public static UserPageView newInstance() {
        return new UserPageView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.user_page_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView userIcon = view.findViewById(R.id.user_icon);
        TextView userName = view.findViewById(R.id.user_name);
        TextView appName = view.findViewById(R.id.appAbout);
        ImageButton backButton = view.findViewById(R.id.back_button);
        TextView logOutButton = view.findViewById(R.id.logout);
        TextView deleteButton = view.findViewById(R.id.delete);
        TextView about = view.findViewById(R.id.about);
        CardView aboutCard = view.findViewById(R.id.about_card);
        TextView aboutText = view.findViewById(R.id.about_text);
        TextView notification = view.findViewById(R.id.notification);
        CardView notificationCard = view.findViewById(R.id.notifications_card);
        TextView notificationText = view.findViewById(R.id.notification_text);
        userIcon.setImageResource(Constant.logo);
        userName.setText(String.format("Hi, %s",Constant.userName));
        appName.setText(String.format("KinAni v%s",Constant.versionName));

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!aboutClick){
                about.setBackground(getResources().getDrawable(R.drawable.round_fill_layout));
                about.setCompoundDrawablesWithIntrinsicBounds(R.drawable.info_24, 0, R.drawable.keyboard_arrow_down_24, 0);
                aboutText.setText(Constant.about);
                aboutCard.setVisibility(View.VISIBLE);
                aboutClick = true;
                }else{
                    about.setCompoundDrawablesWithIntrinsicBounds(R.drawable.info_outline_24, 0, R.drawable.keyboard_arrow_right_24, 0);
                    about.setBackground(getResources().getDrawable(R.drawable.round_layout));
                    aboutCard.setVisibility(View.GONE);
                    aboutClick = false;
                }

            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!notificationClick){
                    notification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.notifications, 0, R.drawable.keyboard_arrow_down_24, 0);
                    notification.setBackground(getResources().getDrawable(R.drawable.round_fill_layout));
                    notificationText.setText(Constant.message);
                    notificationCard.setVisibility(View.VISIBLE);
                    notificationClick = true;
                }else{
                    notification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.notifications_outline_24, 0, R.drawable.keyboard_arrow_right_24, 0);
                    notification.setBackground(getResources().getDrawable(R.drawable.round_layout));
                    notificationCard.setVisibility(View.GONE);
                    notificationClick = false;
                }

            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new HomeView();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentView, fragment)
                        .commit();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutButton.setBackground(getResources().getDrawable(R.drawable.round_fill_layout));
                updateUser("logout");
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteButton.setBackground(getResources().getDrawable(R.drawable.round_fill_layout));
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

    private void updateUser(String flag){
    }
}