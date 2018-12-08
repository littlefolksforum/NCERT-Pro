package com.urexamhelp.ncertpro;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.urexamhelp.ncertpro.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class FeedbackActivity extends AppCompatActivity {

    EditText titleET;
    EditText desET;
    String phone_num;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.sendButton:
                if (Constants.isNetworkAvailable(FeedbackActivity.this)) {
                    String title = titleET.getText().toString();
                    String text = desET.getText().toString();
                    if (!(title.equals("") && text.equals("")) && !phone_num.equals("")) {
                        send_data_to_server(title, text, phone_num);
                    } else {
                        Toast.makeText(this, Constants.empty_form, Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_exam_menu1, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


        Bundle paperData = getIntent().getExtras();
        phone_num = paperData.getString(Constants.phone_number);

        // Set From Text
        TextView fromTV = (TextView) findViewById(R.id.fromTV);
        String from_full_text = "From: " + phone_num;
        fromTV.setText(from_full_text);

        titleET = (EditText) findViewById(R.id.titleText);
        desET = (EditText) findViewById(R.id.textText);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#00ace6"));

        getSupportActionBar().setTitle("Feedback");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setting the status bar colour

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor((Color.parseColor("#0983b4")));
        }


    }

    private void send_data_to_server(String title, String text, String phone_num) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(OTPService.FEEDBACK_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OTPService api = retrofit.create(OTPService.class);

        Call<ResponseBody> call = api.send_feedback(title, text, phone_num);
        call.enqueue(
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String res = null;
                        try {
                            ResponseBody responseBody1 = response.body();
                            if (responseBody1 != null)
                                res = responseBody1.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(res);
                            if (jsonObject.getString("status").equalsIgnoreCase("success") &&
                                    jsonObject.getString("status_code").equalsIgnoreCase("200")) {

                                Toast.makeText(FeedbackActivity.this, "Thank you for the feedback", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            } else {
                                Toast.makeText(FeedbackActivity.this, Constants.something_went_wrong, Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            Log.d("Exception", e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(FeedbackActivity.this, Constants.something_went_wrong, Toast.LENGTH_SHORT).show();
                        Log.i(Constants.TAG, t.getMessage());
                    }
                }
        );
    }
}
