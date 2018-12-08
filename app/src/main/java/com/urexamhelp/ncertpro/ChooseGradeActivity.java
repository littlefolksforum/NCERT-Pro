package com.urexamhelp.ncertpro;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.urexamhelp.ncertpro.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChooseGradeActivity extends AppCompatActivity {

    private EditText Name;
    private TextView button5,button6,button7,button8,button9,button10,button11,button12;
    private Button buttonSend;
    private String class_id;
    private String phone_number;
    private String user_name;
    private Boolean new_user;
    private TextView toolbar_title;


    List<TextView> buttonList;
    private String orig_text;
    private TextView orig_button;
    private String new_text;
    private TextView new_button;

    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_grade);


        //Add firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);



        //Getting Phone Number from the shared preferences

        SharedPreferences profile_data = getSharedPreferences(Constants.PreferenceFile, 0);
        final String phone_num = profile_data.getString(Constants.phone_number,"");

        //Getting the intent data from the previous Activity

        final Intent i = getIntent();
        phone_number = i.getStringExtra("phone_number");
        phone_number=phone_num;
        new_user = i.getBooleanExtra("New User", false);

        //

        buttonSend = (Button) findViewById(R.id.SendButton);
        Name = (EditText) findViewById(R.id.NameEdit);
        class_id = "None";
        user_name = "";
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);


        //Setting the status bar colour

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor((Color.parseColor("#0983b4")));
        }




        buttonList = new ArrayList<>();
        button5 = (TextView) findViewById(R.id.Class05);
        button6 = (TextView) findViewById(R.id.Class06);
        button7 = (TextView) findViewById(R.id.Class07);
        button8 = (TextView) findViewById(R.id.Class08);
        button9 = (TextView) findViewById(R.id.Class09);
        button10 = (TextView) findViewById(R.id.Class10);
        button11 = (TextView) findViewById(R.id.Class11);
        button12 = (TextView) findViewById(R.id.Class12);
        buttonSend = (Button) findViewById(R.id.SendButton);


        button5.setText(Html.fromHtml("5<small><sup>th</sup></small>"));
        button6.setText(Html.fromHtml("6<small><sup>th</sup></small>"));
        button7.setText(Html.fromHtml("7<small><sup>th</sup></small>"));
        button8.setText(Html.fromHtml("8<small><sup>th</sup></small>"));
        button9.setText(Html.fromHtml("9<small><sup>th</sup></small>"));
        button10.setText(Html.fromHtml("10<small><sup>th</sup></small>"));
        button11.setText(Html.fromHtml("11<small><sup>th</sup></small>"));
        button12.setText(Html.fromHtml("12<small><sup>th</sup></small>"));

        buttonList.add(button5);
        buttonList.add(button6);
        buttonList.add(button7);
        buttonList.add(button8);
        buttonList.add(button9);
        buttonList.add(button10);
        buttonList.add(button11);
        buttonList.add(button12);


        for (TextView button: buttonList) {
            button.setTag(false);
        }
        for (final TextView button: buttonList) {
            button.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            setSelectedButton(button);
                        }
                    }
            );
        }

        new_text = "";
        orig_text = "";
        if (!new_user) {
            buttonSend.setText("Update");
            toolbar_title.setText(Constants.profile_text);

            profile_data = getSharedPreferences(Constants.PreferenceFile, 0);
            class_id = profile_data.getString(Constants.class_id,"");

            get_user_data();
            set_user_data();
        }
        else {
            buttonSend.setText("NEXT");
            toolbar_title.setText(Constants.know_you_better_text);
            orig_button = button10;
            new_button = orig_button;
            setSelectedButton(button10);

        }




        Name.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        new_text = Name.getText().toString();
                        activate_update_button();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );

        buttonSend.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (Constants.isNetworkAvailable(ChooseGradeActivity.this)) {

                            {

                                int flag = 0;

                                user_name = Name.getText().toString();

                                // Check if user name
                                if (user_name.equals(""))
                                    Toast.makeText(ChooseGradeActivity.this, "Please Enter your Full Name", Toast.LENGTH_SHORT).show();
                                else
                                    flag++;

                                // Check if class selected
                                if (class_id.equals("None"))
                                    Toast.makeText(ChooseGradeActivity.this, "Please Select a class", Toast.LENGTH_SHORT).show();
                                else
                                    flag++;

                                Log.i(Constants.TAG, String.valueOf(flag));


                                if (flag == 2) {

                                    if (new_user) {
                                        Log.i(Constants.TAG, "it is new_user");
                                        sendDataToLocationActivity(user_name, class_id, phone_number);
                                    } else {
                                        Log.i(Constants.TAG, "it is  not new_user");
                                        sendDatatoServer(user_name, class_id, phone_number);
                                    }
                                }
                            }
                        }
                    }
                }
        );

    }

    private void setSelectedButton(TextView button) {
        reset_all_button();
        button.setBackgroundResource(R.drawable.selected_grade_button);
        button.setTextColor(Color.parseColor("#ffffff"));
        button.setTag(true);
        class_id = get_user_class(button);
        new_button = button;
        hideSoftKeyboard(ChooseGradeActivity.this, button);
        activate_update_button();
    }
    public static void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    private void activate_update_button() {
        Log.i(Constants.TAG, "In the activate update Button");
        if (orig_button != null && orig_text != null && new_button != null ) {
            Log.i(Constants.TAG, "Nothing is Null");
            if (!new_text.equals("") && (new_button != orig_button || !new_text.equals(orig_text))) {
                buttonSend.setBackgroundColor(Color.parseColor("#0055a9"));
                buttonSend.setTextColor(Color.parseColor("#ffffff"));
                Log.i(Constants.TAG, "Setting Background Colour");
            }
            else {
                buttonSend.setBackgroundColor(Color.parseColor("#dcdcdc"));
                buttonSend.setTextColor(Color.parseColor("#000000"));
            }
        }
        else
            Log.i(Constants.TAG, "Something is Null");
    }



    private String get_user_class(TextView button) {
        switch (button.getId()) {
            case R.id.Class05:
                return "8594";
            case R.id.Class06:
                return "8595";
            case R.id.Class07:
                return "8596";
            case R.id.Class08:
                return "8528";
            case R.id.Class09:
                return "8529";
            case R.id.Class10:
                return "8535";
            case R.id.Class11:
                return "8737";
            case R.id.Class12:
                return "8738";
            default:
                return "None";
        }
    }

    private void reset_all_button() {

        for (TextView button: buttonList) {
            if ((boolean) button.getTag()) {
                reset_button(button);
            }
        }
    }
    private void reset_button(TextView button) {
        button.setTextColor(Color.parseColor("#000000"));
        button.setBackgroundResource(R.drawable.choose_grade_button);
        button.setTag(false);
    }

    private void get_user_data() {
        SharedPreferences profile_data = getSharedPreferences(Constants.PreferenceFile, 0);
        String class_id = profile_data.getString(Constants.class_id, "");
        user_name = profile_data.getString(Constants.user_name, "");

        if (class_id.equals("")  || user_name.equals("")) {
            //Getting data from  server
            getUserDataFromServer(phone_number);
        }
    }

    private void set_user_data() {
       //Setting user data
        Name.setText(user_name);

        orig_text = user_name;
        new_text = user_name;

        Log.i(Constants.TAG, class_id + "");
        Log.i(Constants.TAG, "Hello");

        switch (class_id) {
            case "8594":
                orig_button = button5;
                setSelectedButton(button5);
                return;
            case "8595":
                orig_button = button6;
                setSelectedButton(button6);
                return;
            case "8596":
                orig_button = button7;
                setSelectedButton(button7);
                return;
            case "8528":
                orig_button = button8;
                setSelectedButton(button8);
                return;
            case "8529":
                orig_button = button9;
                setSelectedButton(button9);
                return;
            case "8535":
                orig_button = button10;
                setSelectedButton(button10);
                return;
            case "8737":
                orig_button = button11;
                setSelectedButton(button11);
                return;
            case "8738":
                orig_button = button12;
                setSelectedButton(button12);
                return;

        }
    }

    private void sendDataToLocationActivity(String user_name, String user_class, String phone_number) {

        Intent intent = new Intent(ChooseGradeActivity.this, LocationActivity.class);
        intent.putExtra("user_name", user_name);
        intent.putExtra("class_id", user_class); ///made some edit here
        intent.putExtra("phone_number" ,phone_number);
        ChooseGradeActivity.this.startActivity(intent);
        finish();


    }

    private void getUserDataFromServer(final String phone_number) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.PROFILE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OTPService api = retrofit.create(OTPService.class);

        Call<ResponseBody> call = api.profile(phone_number);

        call.enqueue(
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        String res = null;
                        try {
                            res = response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            JSONObject jsonObject = new JSONObject(res);
                            if (jsonObject.getString("status").equalsIgnoreCase("success") &&
                                    jsonObject.getString("status_code").equalsIgnoreCase("200")) {

                                user_name = jsonObject.getString("name");
                                class_id = jsonObject.getString("class_id");


                                SharedPreferences profile_data = getSharedPreferences(Constants.PreferenceFile, 0);
                                SharedPreferences.Editor editor = profile_data.edit();
                                editor.putString(Constants.user_name, user_name);
                                editor.putString(Constants.phone_number, phone_number);
                                editor.putString(Constants.class_id, class_id);
                                Log.i(Constants.TAG,class_id);
                                boolean commit = editor.commit();


                            }
                            else {
                                Log.d("Response", "Failure");
                            }

                        } catch (Exception e) {
                            Log.d("Exception", e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                }
        );

    }


    private void sendDatatoServer( final String user_name, final String class_id, final String phone_number) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.PROFILE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OTPService api = retrofit.create(OTPService.class);


        HashMap<String, String> map = new HashMap<>();

        map.put("8528", "8");
        map.put("8529", "9");
        map.put("8594", "5");
        map.put("8595", "6");
        map.put("8596", "7");
        map.put("8535", "10");
        map.put("8737", "11");
        map.put("8738", "12");


        Log.i(Constants.TAG,"going on");

        Call<ResponseBody> call = api.UpdateUser(user_name, phone_number, map.get(class_id));
        Log.i(Constants.TAG,"API end point working");
        Log.i(Constants.TAG,user_name);
        Log.i(Constants.TAG,phone_number);
        Log.i(Constants.TAG,class_id);

        call.enqueue(
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        String res = null;
                        try {
                            ResponseBody responseBody1 = response.body();
                            Log.i(Constants.TAG,"On Response");

                            if (responseBody1 != null)
                            {
                                res = responseBody1.string();}
                                Log.i(Constants.TAG,res);
                        }

                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {

                            Log.i(Constants.TAG,"On Response XXXXX");

                            JSONObject jsonObject = new JSONObject(res);
                            if (jsonObject.getString("status").equalsIgnoreCase("success") &&
                                    jsonObject.getString("status_code").equalsIgnoreCase("200"))

                            {


                                Log.i(Constants.TAG,"Working statsus");

                                SharedPreferences profile_data = getSharedPreferences(Constants.PreferenceFile, 0);
                                SharedPreferences.Editor editor = profile_data.edit();
                                editor.putString(Constants.user_name, user_name);
                                editor.putString(Constants.phone_number, phone_number);
                                editor.putString(Constants.class_id, class_id);

                                boolean commit = editor.commit();
                                if (commit)
                                    Log.i(Constants.TAG, "Data saved to SP");
                                else
                                    Log.i(Constants.TAG, "Could not save data to SP");


                                Intent intent = new Intent(ChooseGradeActivity.this, SubjectActivity.class);
                                intent.putExtra("class_id", class_id);
                                ChooseGradeActivity.this.startActivity(intent);
                                finish();
                            } else {
                                Log.d("Response", "Failure");
                            }

                        } catch (Exception e) {
                            Log.d("Exception", e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                }
        );
    }


}
