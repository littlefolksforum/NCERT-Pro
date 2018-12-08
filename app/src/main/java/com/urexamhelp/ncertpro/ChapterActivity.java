package com.urexamhelp.ncertpro;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class ChapterActivity extends AppCompatActivity {
    private Retrofit retrofit;
    private ChapterInterface api;
    private String subject_name;
    private String subject_id;
    private List<Chapter> chapterList;
    private ProgressDialog progressDialog;
    private TextView toolbar_title;
    private ImageView toolbar_back_button;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        chapterList = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Intent i = getIntent();
        subject_id= i. getStringExtra("subject_id");
        subject_name=i.getStringExtra("subject_name");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(getSubjectColor(subject_name)));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setText(subject_name);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Semibold.ttf");
        toolbar_title.setTypeface(tf);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(manipulateColor(Color.parseColor(getSubjectColor(subject_name)), 0.8f));
        }



        
        TextView textView = new TextView(getApplicationContext());
        textView.setText("Chapters");
        textView.setTextColor(Color.parseColor(getSubjectColor(subject_name)));
        textView.setTextSize(30);
        textView.setPadding(30,30,30,30);

        ListView listView = (ListView)findViewById(R.id.chapterList);
        listView.addHeaderView(textView);
        textView.setTypeface(tf);


        RelativeLayout relativeLayout= (RelativeLayout)findViewById(R.id.chapter_layout);
        relativeLayout.setBackgroundColor(Color.parseColor(getSubjectStatusBarColor(subject_name)));


        //Calling the API

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.NCERT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(ChapterInterface.class);

        // API CALL
        Call<ResponseBody> call =  api.getChapter(subject_id);
        call.enqueue(
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        //chapterList = response.body();
                        //update_view();


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

                                    int question_count = jsonObject1.getInt("question_count");

                                    if(question_count>0) {
                                        String chapter_id = jsonObject1.getString("chapter_id");
                                        String chapter_name = jsonObject1.getString("chapter_name");
                                        Chapter chapter = new Chapter(chapter_id, chapter_name, question_count);
                                        chapterList.add(chapter);
                                    }
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
        ListAdapter myAdapter = new ChapterAdapter(ChapterActivity.this, chapterList, subject_name);
        ListView chapterList1 = (ListView) findViewById(R.id.chapterList);
        chapterList1.setAdapter(myAdapter);


        chapterList1.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        position -= 1;
                        if (position <0) {
                            return;
                        }
                        Log.i(Constants.TAG, "This is called coding  "+  String.valueOf(position));
                        Intent intent = new Intent(ChapterActivity.this, MainActivity.class);
                        String chapter_id = chapterList.get(position).getChapter_id();
                        String chapter_name = chapterList.get(position).getChapter_name();
                        intent.putExtra("chapter_id", chapter_id);
                        intent.putExtra("chapter_name", chapter_name);
                        ChapterActivity.this.startActivity(intent);

                        Log.i(Constants.TAG,"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+chapter_id);

                    }
                }
        );

        progressDialog.dismiss();

    }
    public static String getSubjectColor(String name) {

        if (name.equalsIgnoreCase(Constants.PHYSICS_SUBJECT_NAME)) {
            return Constants.PHYSICS_COLOR;
        } else if (name.equalsIgnoreCase(Constants.CHEMISTRY_SUBJECT_NAME)) {
            return Constants.CHEMISTRY_COLOR;
        } else if (name.equalsIgnoreCase(Constants.MATHS_SUBJECT_NAME)) {
            return Constants.MATHS_COLOR;
        } else if (name.equalsIgnoreCase(Constants.BIOLOGY_SUBJECT_NAME)) {
            return Constants.BIOLOGY_COLOR;
        } else if (name.equalsIgnoreCase(Constants.ENGLISH_SUBJECT_NAME)) {
            return Constants.ENGLISH_COLOR;
        } else if (name.equalsIgnoreCase(Constants.CIVICS_SUBJECT_NAME)) {
            return Constants.CIVICS_COLOR;
        } else if (name.equalsIgnoreCase(Constants.GEOGRAPHY_SUBJECT_NAME)) {
            return Constants.GEOGRAPHY_COLOR;
        } else if (name.equalsIgnoreCase(Constants.HISTORY_SUBJECT_NAME)) {
            return Constants.HISTORY_COLOR;
        } else if (name.equalsIgnoreCase(Constants.MENTAL_APPTITUDE_SUBJECT_NAME)) {
            return Constants.MENTAL_APPTITUDE_COLOR;
        } else if (name.equalsIgnoreCase(Constants.LOGICAL_REASONING_SUBJECT_NAME)) {
            return Constants.LOGICAL_REASONING_COLOR;
        } else if (name.equalsIgnoreCase(Constants.SOCIOLOGY_SUBJECT_NAME)) {
            return Constants.SOCIOLOGY_COLOR;
        } else if (name.equalsIgnoreCase(Constants.SCIENCE_SUBJECT_NAME)) {
            return Constants.SCIENCE_COLOR;
        } else if (name.equalsIgnoreCase(Constants.EVS_SUBJECT_NAME)) {
            return Constants.EVS_COLOR;
        } else if (name.equalsIgnoreCase(Constants.ECONOMICS_SUBJECT_NAME)) {
            return Constants.ECONOMICS_COLOR;
        } else if (name.equalsIgnoreCase(Constants.ACCOUNTANCY_NAME)) {
            return Constants.ACCOUNTANCY_COLOR;
        } else if (name.equalsIgnoreCase(Constants.BUSINESS_STUDIES_NAME)) {
            return Constants.BUSINESS_STUDIES_COLOR;
        } else if (name.equalsIgnoreCase(Constants.GENERAL_KNOWLEDGE_NAME)) {
            return Constants.GENERAL_KNOWLEDGE_COLOR;
        } else if (name.equalsIgnoreCase(Constants.POLITICAL_SCIENCE_NAME)) {
            return Constants.CIVICS_COLOR;
        } else if (name.equalsIgnoreCase(Constants.EVS_1_NAME)) {
            return Constants.EVS_COLOR;
        } else if (name.equalsIgnoreCase(Constants.EVS_2_NAME)) {
            return Constants.EVS_COLOR;
        } else if (name.equalsIgnoreCase(Constants.SOCIAL_SCIENCE_NAME)) {
            return Constants.EVS_COLOR;
        }

        return Constants.OTHER_COLOR;
    }

    public static String getSubjectStatusBarColor(String name) {

        if (name.equalsIgnoreCase(Constants.PHYSICS_SUBJECT_NAME)) {
            return Constants.PHYSICS_ST_COLOR;
        } else if (name.equalsIgnoreCase(Constants.CHEMISTRY_SUBJECT_NAME)) {
            return Constants.CHEMISTRY_ST_COLOR;
        } else if (name.equalsIgnoreCase(Constants.MATHS_SUBJECT_NAME)) {
            return Constants.MATHS_ST_COLOR;
        } else if (name.equalsIgnoreCase(Constants.BIOLOGY_SUBJECT_NAME)) {
            return Constants.BIOLOGY_ST_COLOR;
        } else if (name.equalsIgnoreCase(Constants.ENGLISH_SUBJECT_NAME)) {
            return Constants.ENGLISH_ST_COLOR;
        } else if (name.equalsIgnoreCase(Constants.CIVICS_SUBJECT_NAME)) {
            return Constants.CIVICS_ST_COLOR;
        } else if (name.equalsIgnoreCase(Constants.GEOGRAPHY_SUBJECT_NAME)) {
            return Constants.GEOGRAPHY_ST_COLOR;
        } else if (name.equalsIgnoreCase(Constants.HISTORY_SUBJECT_NAME)) {
            return Constants.HISTORY_ST_COLOR;
        } else if (name.equalsIgnoreCase(Constants.MENTAL_APPTITUDE_SUBJECT_NAME)) {
            return Constants.MENTAL_APPTITUDE_ST_COLOR;
        } else if (name.equalsIgnoreCase(Constants.LOGICAL_REASONING_SUBJECT_NAME)) {
            return Constants.LOGICAL_REASONING_ST_COLOR;
        } else if (name.equalsIgnoreCase(Constants.SOCIOLOGY_SUBJECT_NAME)) {
            return Constants.SOCIOLOGY_ST_COLOR;
        } else if (name.equalsIgnoreCase(Constants.SCIENCE_SUBJECT_NAME)) {
            return Constants.SCIENCE_ST_COLOR;
        } else if (name.equalsIgnoreCase(Constants.EVS_SUBJECT_NAME)) {
            return Constants.EVS_ST_COLOR;
        } else if (name.equalsIgnoreCase(Constants.ECONOMICS_SUBJECT_NAME)) {
            return Constants.ECONOMICS_ST_COLOR;
        } else if (name.equalsIgnoreCase(Constants.ACCOUNTANCY_NAME)) {
            return Constants.ACCOUNTANCY_ST_COLOR;
        } else if (name.equalsIgnoreCase(Constants.BUSINESS_STUDIES_NAME)) {
            return Constants.BUSINESS_STUDIES_ST_COLOR;
        } else if (name.equalsIgnoreCase(Constants.GENERAL_KNOWLEDGE_NAME)) {
            return Constants.GENERAL_KNOWLEDGE_ST_COLOR;
        } else if (name.equalsIgnoreCase(Constants.POLITICAL_SCIENCE_NAME)) {
            return Constants.CIVICS_ST_COLOR;
        } else if (name.equalsIgnoreCase(Constants.EVS_1_NAME)) {
            return Constants.EVS_ST_COLOR;
        } else if (name.equalsIgnoreCase(Constants.EVS_2_NAME)) {
            return Constants.EVS_ST_COLOR;
        } else if (name.equalsIgnoreCase(Constants.SOCIAL_SCIENCE_NAME)) {
            return Constants.EVS_ST_COLOR;
        }

        return Constants.OTHER_ST_COLOR;
    }


    public static int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r,255),
                Math.min(g,255),
                Math.min(b,255));
    }



}
