package com.urexamhelp.ncertpro;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.Toast;

import com.urexamhelp.ncertpro.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OTPActivity extends AppCompatActivity {

    //Creating Views
    private EditText editTextPhone;
    private Button buttonSubmit;
    private ProgressDialog progressDialog;
    private String phone;
    private Boolean isEnabled;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


        editTextPhone = (EditText) findViewById(R.id.Phone);
        buttonSubmit = (Button) findViewById(R.id.submitButton);

        //Setting the status bar colour

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor((Color.parseColor("#0983b4")));
        }


        isEnabled = false;

        editTextPhone.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (editTextPhone.getText().toString().length() == 10) {
                            buttonSubmit.setBackgroundResource(R.drawable.grade_button_shape_correct);
                            isEnabled = true;
                            hideSoftKeyboard(OTPActivity.this, buttonSubmit);
                        }


                        else {
                            buttonSubmit.setBackgroundResource(R.drawable.grade_button_shape);
                            isEnabled = false;
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );

        //Adding a listener to button
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Constants.isNetworkAvailable(OTPActivity.this)) {
                    progressDialog = new ProgressDialog(OTPActivity.this);
                    progressDialog.setTitle(Constants.progressTitle);
                    progressDialog.setMessage(Constants.progressText);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    phone = editTextPhone.getText().toString();
                    if (!isEnabled) {
                        Toast.makeText(OTPActivity.this, "Please Enter a Valid 10-digit Number", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    } else
                        sendDataToServer();

                }




            }
        });
    }



    private void sendDataToServer()

    {


        Retrofit.Builder builder = new  Retrofit.Builder()
                .baseUrl(Constants.PROFILE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        OTPService otp =retrofit.create(OTPService.class);
        Call<ResponseBody> call = otp.SendPhoneNo(phone);


        call.enqueue(
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        Toast.makeText(OTPActivity.this, "OTP was sent", Toast.LENGTH_SHORT).show();
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
                                editor.putString(Constants.phone_number, phone);
                                editor.apply();



                                Intent i = new Intent(getApplicationContext(), OTPConfirmActivity.class);
                                i.putExtra("token", token);
                                i.putExtra("phone_number", phone);
                                i.putExtra("message", message);

                                // If existing user then send the class_id also

                                if(message.equalsIgnoreCase("User already exists")){

                                    //  i.putExtra("name", jsonObject.getString("name"));
                                    i.putExtra("class_id", jsonObject.getString("class_id"));
                                    i.putExtra("name",jsonObject.getString("name"));

                                }
                                else if (!message.equalsIgnoreCase("User already exists"))
                                {

                                    i.putExtra("class_id","");
                                    i.putExtra("name","");
                                }
                                startActivity(i);
                                progressDialog.dismiss();
                                finish();

                                //startActivity(i);
                                //progressDialog.dismiss();
                                //finish();


                            } else{
                                Toast.makeText(OTPActivity.this,
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
                        Toast.makeText(OTPActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
        );

    }

    public static void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }



}