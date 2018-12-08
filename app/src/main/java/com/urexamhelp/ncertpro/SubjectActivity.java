package com.urexamhelp.ncertpro;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.urexamhelp.ncertpro.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SubjectActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Retrofit retrofit;
    private SubjectInterface api;
    private String class_id;
    private List<Subject> subject_list;
    private ProgressDialog progressDialog;
    int all_done = 0;
    private DrawerLayout mDrawerLayout;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent intent = new Intent(SubjectActivity.this, ChooseGradeActivity.class);
            intent.putExtra("New User", false);
            SubjectActivity.this.startActivity(intent);
        }
        else if (id == R.id.nav_change_class) {
            Intent intent = new Intent(SubjectActivity.this, ChooseGradeActivity.class);
            intent.putExtra("New User", false);
            SubjectActivity.this.startActivity(intent);
        }
        else if (id == R.id.nav_feedback) {
            Intent intent = new Intent(SubjectActivity.this, FeedbackActivity.class);
            SharedPreferences profile_data = getSharedPreferences(Constants.PreferenceFile, 0);
            String phone_num = profile_data.getString(Constants.phone_number, "");

            if (phone_num.equals("")) {
                Toast.makeText(this, "You are not logged in properly", Toast.LENGTH_SHORT).show();
            }
            else {
                intent.putExtra(Constants.phone_number, phone_num);
                SubjectActivity.this.startActivity(intent);
            }
        }
        else if (id == R.id.nav_logout) {
            SharedPreferences profile_data = getSharedPreferences(Constants.PreferenceFile, 0);
            SharedPreferences.Editor editor = profile_data.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(SubjectActivity.this, LaunchActivity.class);
            SubjectActivity.this.startActivity(intent);
            finish();
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("com.package.ACTION_LOGOUT");
            sendBroadcast(broadcastIntent);

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        subject_list = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Intent i = getIntent();
        class_id =i.getStringExtra("class_id");

        // Saving data to the shared preference

        SharedPreferences profile_data = getSharedPreferences(Constants.PreferenceFile, 0);
        SharedPreferences.Editor editor = profile_data.edit();
        editor.putString(Constants.class_id, class_id);
        editor.apply();

        //if (class_id.equals("") ) {
        //    progressDialog.dismiss();
        //    return;
       //}

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ImageView img = (ImageView)  findViewById(R.id.ham_burger);
        img.setOnClickListener(new View.OnClickListener(){


            public void onClick(View v) {
                drawer.openDrawer(Gravity.START);
                }
        });




        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getWindow().setStatusBarColor(Color.TRANSPARENT);



        // Navigation
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        /*View view=navigationView.inflateHeaderView(R.layout.nav_header_main);*/
        TextView WelcomeUserName = (TextView)header.findViewById(R.id.WelcomeUserName);
        String user_name = profile_data.getString(Constants.user_name, "");
        Log.i(Constants.TAG, "UserName is " + user_name);
        WelcomeUserName.setText(user_name);



        // To close all Exam activities when logout pressed
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive","Logout in progress");
                Intent intentAct = new Intent(SubjectActivity.this, LaunchActivity.class);
                SubjectActivity.this.startActivity(intentAct);
                finish();
            }
        }, intentFilter);



        // Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.NCERT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(SubjectInterface.class);

        // API CALL
        Call<ResponseBody> call =  api.getSubject(class_id);
        call.enqueue(
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        try {

                            String res = response.body().string();
                            Log.d("Response",res);
                            JSONObject jsonObject = new JSONObject(res);
                            String status = jsonObject.getString("status");
                            String code = jsonObject.getString("status_code");

                            if (status.equalsIgnoreCase("Success") &&
                                    code.equalsIgnoreCase("200")) {

                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for ( int i = 0; i< jsonArray.length(); i++ ) {
                                    JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
                                    String subject_id = jsonObject1.getString("subject_id");
                                    String subject_name = jsonObject1.getString("subject_name");
                                    int chapter_count = jsonObject1.getInt("chapter_count");
                                    if(subject_id.equalsIgnoreCase("299565") ||
                                            subject_id.equalsIgnoreCase("299561")||subject_name.equalsIgnoreCase("General Knowledge") )
                                        continue;
                                    Subject subject = new Subject(subject_id, subject_name, chapter_count);
                                    subject_list.add(subject);

                                }



                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        update_view();

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                        Log.i(Constants.TAG, "Board: No Response");
//                        Toast.makeText(Exams.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );


    }

    private void update_view() {
        // SET ADAPTER
        ListAdapter myAdapter = new SubjectAdapter(SubjectActivity.this, subject_list);
        ListView SubjectList = (ListView) findViewById(R.id.SubjectList);
        SubjectList.setAdapter(myAdapter);

        SubjectList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent intent = new Intent(SubjectActivity.this, ChapterActivity.class);
                        String subject_id = subject_list.get(position).getSubject_id();

                        String subject_name = subject_list.get(position).getSubject_name();
                        intent.putExtra("subject_id", subject_id);
                        intent.putExtra("subject_name", subject_name);
                        SubjectActivity.this.startActivity(intent);

                    }
                }
        );

        progressDialog.dismiss();

    }
}
