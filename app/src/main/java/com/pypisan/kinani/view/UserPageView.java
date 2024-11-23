package com.pypisan.kinani.view;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pypisan.kinani.R;
import com.pypisan.kinani.api.ReportIssue;
import com.pypisan.kinani.api.RequestModule;
import com.pypisan.kinani.api.UserUpdate;
import com.pypisan.kinani.model.UserModel;
import com.pypisan.kinani.storage.AnimeManager;
import com.pypisan.kinani.storage.Constant;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserPageView extends Fragment {

    private Boolean aboutClick = false;
    private Boolean notificationClick = false;
    private ImageView userIcon;
    private Dialog myDialog;
    private TextView changeIcon,logOutButton,deleteButton,reportIssue;
    private ProgressBar loader;

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
        userIcon = view.findViewById(R.id.user_icon);
        TextView userName = view.findViewById(R.id.user_name);
        TextView appName = view.findViewById(R.id.appAbout);
        ImageButton backButton = view.findViewById(R.id.back_button);
        logOutButton = view.findViewById(R.id.logout);
        deleteButton = view.findViewById(R.id.delete);
        changeIcon = view.findViewById(R.id.change_icon);
        reportIssue = view.findViewById(R.id.issue);
        TextView privacy = view.findViewById(R.id.privacy);
        TextView about = view.findViewById(R.id.about);
        CardView aboutCard = view.findViewById(R.id.about_card);
        TextView aboutText = view.findViewById(R.id.about_text);
        TextView notification = view.findViewById(R.id.notification);
        CardView notificationCard = view.findViewById(R.id.notifications_card);
        TextView notificationText = view.findViewById(R.id.notification_text);
        TextView savedVideosButton = view.findViewById(R.id.downloads);
        userIcon.setImageResource(Constant.logo);
        userName.setText(String.format("Hi, %s",Constant.userName));
        appName.setText(String.format("KinAni v%s",Constant.versionName));
        myDialog = new Dialog(view.getContext());
        myDialog.setContentView(R.layout.user_icon_update_dailog);
        myDialog.setCancelable(false);
        loader = myDialog.findViewById(R.id.updateLoader);
        if (Constant.isMessage){
            notification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.notifications_outline_24, 0, R.drawable.new_msg, 0);
        }
        about.setOnClickListener(v -> {
            if (!aboutClick){
            about.setBackground(getResources().getDrawable(R.drawable.round_fill_layout));
            about.setCompoundDrawablesWithIntrinsicBounds(R.drawable.info_24, 0, R.drawable.keyboard_arrow_down_24, 0);
            aboutText.setText(Constant.about);
            aboutCard.setVisibility(View.VISIBLE);
            aboutClick = true;
            }else{
                about.setCompoundDrawablesWithIntrinsicBounds(R.drawable.info_outline_24, 0, R.drawable.keyboard_arrow_right_24, 0);
                about.setBackground(getResources().getDrawable(R.drawable.round_layout_user));
                aboutCard.setVisibility(View.GONE);
                aboutClick = false;
            }

        });
        notification.setOnClickListener(v -> {
            if (!notificationClick){
                notification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.notifications, 0, R.drawable.keyboard_arrow_down_24, 0);
                notification.setBackground(getResources().getDrawable(R.drawable.round_fill_layout));
                notificationText.setText(Constant.message);
                notificationCard.setVisibility(View.VISIBLE);
                notificationClick = true;
                if (Constant.isMessage){
                    AnimeManager animeManager = new AnimeManager(getContext());
                    animeManager.open();
                    animeManager.updateMessage(Constant.uid,Constant.message);
                    animeManager.close();
                    Constant.isMessage = false;
                }
            }else{
                notification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.notifications_outline_24, 0, R.drawable.keyboard_arrow_right_24, 0);
                notification.setBackground(getResources().getDrawable(R.drawable.round_layout_user));
                notificationCard.setVisibility(View.GONE);
                notificationClick = false;
            }

        });
        backButton.setOnClickListener(v -> returnToHome());

        logOutButton.setOnClickListener(v -> {
            logOutButton.setBackground(getResources().getDrawable(R.drawable.round_fill_layout));
            myDialog.show();
            loader.setVisibility(View.VISIBLE);
            updateUser("logout");
        });

        deleteButton.setOnClickListener(v -> {
            deleteButton.setBackground(getResources().getDrawable(R.drawable.round_fill_layout));
            myDialog.show();
            LinearLayout confirmation = myDialog.findViewById(R.id.confirm_layout);
            Button yes = myDialog.findViewById(R.id.yesButton);
            Button no = myDialog.findViewById(R.id.noButton);
            confirmation.setVisibility(View.VISIBLE);
            no.setOnClickListener(v12 -> {
                confirmation.setVisibility(View.GONE);
                myDialog.cancel();
                deleteButton.setBackground(getResources().getDrawable(R.drawable.round_layout_user));
            });
            yes.setOnClickListener(v1 -> {
                confirmation.setVisibility(View.GONE);
                loader.setVisibility(View.VISIBLE);
                updateUser("delete");
            });
        });

        savedVideosButton.setOnClickListener(v -> {
            Fragment fragment = new SavedVideos();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentView, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        changeIcon.setOnClickListener(v -> {
            changeIcon.setBackground(getResources().getDrawable(R.drawable.round_fill_layout));
            showDialog(v,"icon");
        });

        privacy.setOnClickListener(v -> {
            privacy.setBackground(getResources().getDrawable(R.drawable.round_fill_layout));
            new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.privacyUrl));
                startActivity(browserIntent);
                privacy.setBackground(getResources().getDrawable(R.drawable.round_layout_user));
            }
        }, 300);

        });
        reportIssue.setOnClickListener(v -> {
            reportIssue.setBackground(getResources().getDrawable(R.drawable.round_fill_layout));
            showDialog(v,"issue");

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
    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void updateUser(String flag){
        UserUpdate userUpdate;
        if (flag.equals("logout")){
            userUpdate = new UserUpdate(Constant.uid, 0,false, false,true);
        }else if(flag.equals("icon")){
            userUpdate = new UserUpdate(Constant.uid, Constant.logo,true, false, true);
        }else if(flag.equals("delete")){
            userUpdate = new UserUpdate(Constant.uid, 0,true, true, true);
        }
        else{
            userUpdate = new UserUpdate(Constant.uid, Constant.logo,true, false, true);
        }
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constant.userUrl)
                .addConverterFactory(GsonConverterFactory.create()).build();
        RequestModule updateUserData = retrofit.create(RequestModule.class);
        Call<UserModel> call = updateUserData.updateUser(Constant.key,userUpdate);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                boolean statusFlag = false;
                UserModel resource = response.body();
                if (response.code() == 200) {
                    statusFlag = resource.getUserStatus();
                }
                if (statusFlag && flag.equals("logout")){
                    Constant.loggedInStatus = false;
                    loader.setVisibility(View.GONE);
                    myDialog.cancel();
                    returnToHome();
                    Toast.makeText(getContext(),"Log Out Successful ", Toast.LENGTH_LONG).show();
                } else if (statusFlag && flag.equals("icon")) {
                    userIcon.setImageResource(Constant.logo);
                    loader.setVisibility(View.GONE);
                    myDialog.cancel();
                    changeIcon.setBackground(getResources().getDrawable(R.drawable.round_layout_user));
                    Toast.makeText(getContext(),"Icon Changed Successful ", Toast.LENGTH_SHORT).show();
                }else if (statusFlag && flag.equals("delete")) {
                    Constant.loggedInStatus = false;
                    loader.setVisibility(View.GONE);
                    myDialog.cancel();
                    returnToHome();
                    Toast.makeText(getContext(),"Account Deleted Successful ", Toast.LENGTH_SHORT).show();
                }else if (!statusFlag && flag.equals("logout")){
                    loader.setVisibility(View.GONE);
                    myDialog.cancel();
                    logOutButton.setBackground(getResources().getDrawable(R.drawable.round_layout_user));
                    Toast.makeText(getContext(),"Log Out Failed", Toast.LENGTH_SHORT).show();
                }else if (!statusFlag && flag.equals("icon")){
                    loader.setVisibility(View.GONE);
                    myDialog.cancel();
                    changeIcon.setBackground(getResources().getDrawable(R.drawable.round_layout_user));
                    Toast.makeText(getContext(),"Icon Change Failed", Toast.LENGTH_SHORT).show();
                }else if (!statusFlag && flag.equals("delete")){
                    loader.setVisibility(View.GONE);
                    myDialog.cancel();
                    deleteButton.setBackground(getResources().getDrawable(R.drawable.round_layout_user));
                    Toast.makeText(getContext(),"Account Delete Failed", Toast.LENGTH_SHORT).show();
                }else {
                    loader.setVisibility(View.GONE);
                    myDialog.cancel();
                    Toast.makeText(getContext(),"Update Failed", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                loader.setVisibility(View.GONE);
                myDialog.cancel();
                Toast.makeText(getContext(),"Try Again", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendReport(String subject, String title, String message){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constant.userUrl)
                .addConverterFactory(GsonConverterFactory.create()).build();
        RequestModule reportData = retrofit.create(RequestModule.class);
        Call<UserModel> call = reportData.reportIssue(Constant.key,
                new ReportIssue(subject,title,message));

        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                boolean statusFlag = false;
                UserModel resource = response.body();
                if (response.code() == 200) {
                    statusFlag = resource.getUserStatus();
                }
                Log.d("hi","hi "+resource.getMessage());
                if (statusFlag){
                    loader.setVisibility(View.GONE);
                    myDialog.cancel();
                    reportIssue.setBackground(getResources().getDrawable(R.drawable.round_layout_user));
                    Toast.makeText(getContext(),resource.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }else {
                    loader.setVisibility(View.GONE);
                    myDialog.cancel();
                    reportIssue.setBackground(getResources().getDrawable(R.drawable.round_layout_user));
                    Toast.makeText(getContext(),"Error "+resource.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                loader.setVisibility(View.GONE);
                myDialog.cancel();
                reportIssue.setBackground(getResources().getDrawable(R.drawable.round_layout_user));
                Toast.makeText(getContext(),"Try Again", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void returnToHome(){
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }

    private void showDialog(View view, String viewType){
        if (viewType.equals("icon")){
        GridView gridIconView = myDialog.findViewById(R.id.grid_icon_view);
        LinearLayout gridIconLinearView = myDialog.findViewById(R.id.grid_linear_layout);
        Button cancelButton = myDialog.findViewById(R.id.cancelUserIconButton);
        gridIconView.setAdapter(new ImageAdapter(view.getContext()));
        gridIconLinearView.setVisibility(View.VISIBLE);
        myDialog.show();
        cancelButton.setOnClickListener(v -> {
            gridIconLinearView.setVisibility(View.GONE);
            myDialog.cancel();
            changeIcon.setBackground(getResources().getDrawable(R.drawable.round_layout_user));
        });

        gridIconView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Constant.logo=Constant.userIconImage[position];
                loader.setVisibility(View.VISIBLE);
                gridIconLinearView.setVisibility(View.GONE);
                updateUser("icon");
            }
        });
        }else{
            Button cancelIssueButton = myDialog.findViewById(R.id.cancelIssueButton);
            LinearLayout issueBox = myDialog.findViewById(R.id.issueBox);
            Button sendButton = myDialog.findViewById(R.id.sendButton);
            TextView subject = myDialog.findViewById(R.id.et_subject);
            TextView title = myDialog.findViewById(R.id.et_title);
            TextView message = myDialog.findViewById(R.id.et_message);
            issueBox.setVisibility(View.VISIBLE);
            myDialog.show();
            cancelIssueButton.setOnClickListener(v -> {
                issueBox.setVisibility(View.GONE);
                myDialog.cancel();
                reportIssue.setBackground(getResources().getDrawable(R.drawable.round_layout_user));
            });

            sendButton.setOnClickListener(v -> {
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager)getActivity().
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                String subjectValue = String.valueOf(subject.getText());
                String titleValue = String.valueOf(title.getText());
                String messageValue = String.valueOf(message.getText());
                if (subjectValue.equals("")|| subjectValue.length()<10){
                    Toast.makeText(getContext(),"Subject should not less than 10 char",
                            Toast.LENGTH_LONG).show();
                } else if (messageValue.equals("")||messageValue.length()<15) {
                    Toast.makeText(getContext(),"Message should not less than 15 char",
                            Toast.LENGTH_LONG).show();
                }else {
                    loader.setVisibility(View.VISIBLE);
                    issueBox.setVisibility(View.GONE);
                    sendReport(subjectValue, titleValue, messageValue);
                }
                subject.setText("");
                title.setText("");
                message.setText("");
            });
        }
    }

//    for Icon adapter
    
    private static class ImageAdapter extends BaseAdapter {
        private Context mContext;
        @Override
        public int getCount() {
            return Constant.userIconImage.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(5, 5, 5, 5);
            }
            else
            {
                imageView = (ImageView) convertView;
            }
            imageView.setImageResource(Constant.userIconImage[position]);
            return imageView;
        }

        public ImageAdapter(Context c){
            mContext = c;
        }
    }
}