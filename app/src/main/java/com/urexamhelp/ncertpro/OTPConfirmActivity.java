package com.urexamhelp.ncertpro;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.urexamhelp.ncertpro.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OTPConfirmActivity extends AppCompatActivity {

    private EditText editTextConfirmOtp;
    private Button buttonVerify;
    private ProgressDialog progressDialog;
    private String message;
    private String token;
    private String phone_number;
    private Boolean isEnabled;
    private String  user_name_from_server;
    private String class_id_from_server;
    private TextView phoneNumberTV;
    private ImageButton editPhoneNumber;
    private TextView resendOTP;
    private Boolean isOtpResent = false;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpconfirm);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


        isEnabled = false;

        Intent i = getIntent();

        token = i.getStringExtra("token");
        phone_number = i.getStringExtra("phone_number");
        message = i.getStringExtra("message");
        user_name_from_server = i.getStringExtra("name");
        class_id_from_server = i.getStringExtra("class_id");


        editTextConfirmOtp = findViewById(R.id.editTextOtp);
        buttonVerify = findViewById(R.id.buttonVerify);

        //Setting the status bar colour

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor((Color.parseColor("#0983b4")));
        }


        phoneNumberTV = (TextView) findViewById(R.id.phoneNumberChange);
        editPhoneNumber = (ImageButton) findViewById(R.id.phoneNumberChangeIB);
        resendOTP = (TextView) findViewById(R.id.resendOTP);


        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isNetworkAvailable()) {

                    String otp = editTextConfirmOtp.getText().toString();
                    if (isEnabled) {
                        sendDatatoServer(otp, token, phone_number);
                    } else {
                        Toast.makeText(OTPConfirmActivity.this, "Please Enter the 6-digit OTP sent to " + phone_number, Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        editTextConfirmOtp.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (editTextConfirmOtp.getText().toString().length() == 6) {
                            isEnabled = true;
                            buttonVerify.setBackgroundResource(R.drawable.grade_button_shape_correct);
                            hideSoftKeyboard(OTPConfirmActivity.this, buttonVerify);
                        }
                        else {
                            isEnabled = false;
                            buttonVerify.setBackgroundResource(R.drawable.grade_button_shape);
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );

        String phone_numberText = "+91-" + phone_number;
        phoneNumberTV.setText(phone_numberText);
        editPhoneNumber.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OTPConfirmActivity.this, OTPActivity.class);
                        OTPConfirmActivity.this.startActivity(intent);
                        finish();
                    }
                }
        );
        resendOTP.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isOtpResent) {
                            Toast.makeText(OTPConfirmActivity.this, Constants.sending_otp, Toast.LENGTH_SHORT).show();
                            sendResendOTPRequestToServer(phone_number);
                            isOtpResent = true;
                        }
                        else
                            Toast.makeText(OTPConfirmActivity.this, Constants.OTP_resend_msg, Toast.LENGTH_SHORT).show();
                    }
                }
        );


    }


    private void sendDatatoServer(final String otp, final String token,final String phone_number)

    {
        progressDialog = new ProgressDialog(OTPConfirmActivity.this);
        progressDialog.setTitle(Constants.progressTitle);
        progressDialog.setMessage(Constants.progressText);
        progressDialog.setCancelable(false);
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.PROFILE_URL + "otp_confirmation/";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Log.d("Response", response);
                        // If all data is received stop getting data

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equalsIgnoreCase("success") &&
                                    jsonObject.getString("status_code").equalsIgnoreCase("200")) {

                                if(message.equalsIgnoreCase("New User")) {

                                    // Go to ChooseGrade  activity

                                    Toast.makeText(OTPConfirmActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(OTPConfirmActivity.this, ChooseGradeActivity.class);
                                    i.putExtra("phone_number", phone_number);
                                    i.putExtra("New User", true);
                                    startActivity(i);
                                    progressDialog.dismiss();
                                    finish();

                                }

                                else {
                                    // Go to Subject Activity

                                    HashMap <String, String> map = new HashMap<>();

                                    map.put("8","8528");
                                    map.put("9","8529");
                                    map.put("5","8594");
                                    map.put("6","8595");
                                    map.put("7","8596");
                                    map.put("10","8535");
                                    map.put("11","8737");
                                    map.put("12","8738");

                                    SharedPreferences profile_data = getSharedPreferences(Constants.PreferenceFile, 0);
                                    final String class_id = profile_data.getString(Constants.class_id,"");

                                    Intent intent = new Intent(OTPConfirmActivity.this, SubjectActivity.class);

                                    if(!class_id.equals("")){
                                    intent.putExtra("class_id", map.get(class_id));}

                                    else {
                                        intent.putExtra("class_id", map.get(class_id_from_server));
                                        SharedPreferences.Editor editor = profile_data.edit();
                                        editor.putString(Constants.class_id, map.get(class_id_from_server));
                                        editor.putString(Constants.user_name,user_name_from_server);
                                        editor.apply();
                                    }
                                    Toast.makeText(OTPConfirmActivity.this, "Welcome Back", Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                    progressDialog.dismiss();
                                    finish();

                                }

                            } else {

                                Toast.makeText(OTPConfirmActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                Log.d("Response", "Failure");
                            }

                        } catch (Exception e) {
                            Log.i(Constants.TAG, "Exception: " +e.getMessage());
                            progressDialog.dismiss();
                        }


                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(OTPConfirmActivity.this, "Something Went Wrong !!", Toast.LENGTH_SHORT).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("otp", otp);

                return params;
            }
        };
        queue.add(postRequest);

    }

    private void sendResendOTPRequestToServer(final String phone_num) {
        progressDialog = new ProgressDialog(OTPConfirmActivity.this);
        progressDialog.setTitle(Constants.progressTitle);
        progressDialog.setMessage(Constants.progressText);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Retrofit.Builder builder = new  Retrofit.Builder()
                .baseUrl(Constants.PROFILE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        OTPService otp =retrofit.create(OTPService.class);
        Call<ResponseBody> call = otp.SendPhoneNo(phone_num);


        call.enqueue(
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                        try {

                            String res = response.body().string();
                            Log.d("Response",res);
                            JSONObject jsonObject = new JSONObject(res);
                            String status = jsonObject.getString("status");
                            String code = jsonObject.getString("status_code");

                            if (status.equalsIgnoreCase("Success") &&
                                    code.equalsIgnoreCase("200")){


                                // Get User type(New or Existing) and name

                                String message = jsonObject.getString("message");
                                String token = jsonObject.getString("token");

                                // Saving data to the shared preference

                                SharedPreferences profile_data = getSharedPreferences(Constants.PreferenceFile, 0);
                                SharedPreferences.Editor editor = profile_data.edit();
                                editor.putString(Constants.phone_number, phone_num);
                                editor.apply();

                                OTPConfirmActivity.this.token = token;
                                OTPConfirmActivity.this.phone_number = phone_num;
                                OTPConfirmActivity.this.message = message;


                                // If existing user then send the class_id also

                                if(message.equalsIgnoreCase("User already exists")){

                                    //  i.putExtra("name", jsonObject.getString("name"));
                                    OTPConfirmActivity.this.class_id_from_server = jsonObject.getString("class_id");
                                    OTPConfirmActivity.this.user_name_from_server =jsonObject.getString("name");

                                }

                                Toast.makeText(OTPConfirmActivity.this, "OTP resent", Toast.LENGTH_SHORT).show();

                                progressDialog.dismiss();


                            } else{
                                Toast.makeText(OTPConfirmActivity.this,
                                        "Something went wrong!!!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(OTPConfirmActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
        );


    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        Boolean status = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (!status)
            Toast.makeText(OTPConfirmActivity.this, Constants.no_internet_message, Toast.LENGTH_SHORT).show();
        return status;
    }

    public static void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

}




