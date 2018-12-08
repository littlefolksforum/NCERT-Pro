package com.urexamhelp.ncertpro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.urexamhelp.ncertpro.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocationActivity extends AppCompatActivity {

    private TextView locationTV;
    private LocationAdapter locationAdapter;
    private RelativeLayout LocationRL;

    private String phone_number;
    private String user_name;
    private String class_id;
    private Boolean new_user;
    private TextView toolbar_title;

    private Button submitButton;

    private String StringToSearch;
    private EditText locationET;
    private ListView locationLV;
    private List<String> all_predictions;
    private HashMap<String, String> hashMap;
    private HashMap<String, String> hashMapDescription;
    private HashMap<String, String> hashMapId;

    private int selected_button_position = -1;
    private String selected_button_place_id = "";
    private String main_loc;
    private String address;

    private Boolean isClickedChange;
    private FirebaseAnalytics mFirebaseAnalytics;

    private Timer timer = new Timer();
    private final long DELAY = 300; // in ms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


        submitButton = (Button) findViewById(R.id.submitButton);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        submitButton.setText("SUBMIT");
        toolbar_title.setText(Constants.location_title);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor((Color.parseColor("#0983b4")));
        }

        locationET = (EditText) findViewById(R.id.locationET);
        locationLV = (ListView) findViewById(R.id.locationLV);

        final Intent i = getIntent();
        phone_number = i.getStringExtra(Constants.phone_number);
        class_id = i.getStringExtra(Constants.class_id);
        user_name = i.getStringExtra(Constants.user_name);

        isClickedChange = false;

        submitButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (Constants.isNetworkAvailable(LocationActivity.this)) {

                            if (can_activate_button()) {
                                // Register
                                sendDatatoServer(user_name, class_id, phone_number, address, selected_button_place_id);
                            }
                            else {
                                Toast.makeText(LocationActivity.this, "Please Select your location", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                }
        );

        locationET.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        set_adapter_value(new ArrayList<String>(), new HashMap<String, String>(), 0);
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(timer != null)
                            timer.cancel();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (!isClickedChange) {
                                    StringToSearch = locationET.getText().toString();
                                    Log.i(Constants.TAG, "Calling google Api for :" + StringToSearch);
                                    get_API_response(StringToSearch);
                                }
                                isClickedChange = false;

                            }

                        }, DELAY);
                    }
                }
        );

        locationLV.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        isClickedChange = true;
                        Log.i(Constants.TAG, "List View Clicked at: " + position);
                        selected_button_position = position;
                        if (all_predictions.size() > selected_button_position) {
                            main_loc = all_predictions.get(selected_button_position);
                            address = hashMapDescription.get(main_loc);
                            selected_button_place_id = hashMapId.get(main_loc);
                            locationET.setText(main_loc);
                            set_adapter_value(all_predictions, hashMap, selected_button_position);
                        }
                    }
                }
        );


    }

    private boolean can_activate_button() {
        if (selected_button_position == -1 || all_predictions.size() == 0 && selected_button_position < all_predictions.size()) {
            deactivate_button();
            return false;
        }
        else {
            activate_button();
            return true;
        }
    }

    private void activate_button() {
        submitButton.setBackgroundColor(Color.parseColor("#0055a9"));
        submitButton.setTextColor(Color.parseColor("#ffffff"));
    }

    private void deactivate_button() {
        submitButton.setBackgroundColor(Color.parseColor("#dcdcdc"));
        submitButton.setTextColor(Color.parseColor("#000000"));
    }

    private void set_adapter_value(List<String> all_predictions, HashMap<String, String> hashMap, int selected_button_position) {
        locationAdapter = new LocationAdapter(LocationActivity.this, all_predictions, hashMap, selected_button_position);
        locationLV.setAdapter(locationAdapter);
        can_activate_button();
    }

    private void get_API_response(String stringToSearch) {
        all_predictions = new ArrayList<>();
        hashMap = new HashMap<>();
        hashMapDescription = new HashMap<>();
        hashMapId = new HashMap<>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(OTPService.LOCATION_SEARCH_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OTPService api = retrofit.create(OTPService.class);

        Call<ResponseBody> call = api.getPlaces(Constants.API_KEY, Constants.TYPE_FILTER_LOCATION, stringToSearch, Constants.COMPONENTS);

        call.enqueue(
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        Log.i(Constants.TAG, "Positive Response");
                        String res = null;
                        try {
                            Log.i(Constants.TAG, String.valueOf(response));
                            res = response.body().string();
//                            Log.i(Constants.TAG, res);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(res);
                            String status = jsonObject.getString("status");
                            Log.i(Constants.TAG, "Status: " + status);
                            if (status.equalsIgnoreCase("OK")) {
                                JSONArray predictions = jsonObject.getJSONArray("predictions");

                                Log.i(Constants.TAG, "Got the Predictions");
                                selected_button_position = -1;
                                for (int i = 0; i < predictions.length(); i++) {
                                    JSONObject jsonObject1 = predictions.getJSONObject(i);

                                    // Structured Format
                                    JSONObject structured_format = jsonObject1.getJSONObject("structured_formatting");
                                    String main_text = structured_format.getString("main_text");
                                    String secondary_text = "";
                                    if (structured_format.has("secondary_text"))
                                        secondary_text = structured_format.getString("secondary_text");

                                    all_predictions.add(main_text);
                                    hashMap.put(main_text, secondary_text);

                                    // Description and Id
                                    String description = jsonObject1.getString("description");
                                    String place_id = jsonObject1.getString("place_id");
                                    hashMapDescription.put(main_text, description);
                                    hashMapId.put(main_text, place_id);
                                    if (selected_button_place_id.equalsIgnoreCase(place_id)) {
                                        selected_button_position = i;
                                    }
                                }
                                set_adapter_value(all_predictions, hashMap, selected_button_position);
                            }
                            else if (status.equalsIgnoreCase("INVALID_REQUEST")) {
                                all_predictions = new ArrayList<>();
                                hashMap = new HashMap<>();
                                set_adapter_value(all_predictions, hashMap, 0);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(LocationActivity.this, Constants.something_went_wrong, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }


    private void sendDatatoServer(final String user_name, final String class_id, final String phone_number, String address, String address_id) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.PROFILE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OTPService api = retrofit.create(OTPService.class);

        HashMap <String, String> map = new HashMap<>();

        map.put("8528", "8");
        map.put("8529", "9");
        map.put("8594", "5");
        map.put("8595", "6");
        map.put("8596", "7");
        map.put("8535", "10");
        map.put("8737", "11");
        map.put("8738", "12");

        Log.i("SHAGUN", "Calling API");
        Call<ResponseBody> call = api.RegisterUser(user_name, phone_number, map.get(class_id), address, address_id, 2);
        call.enqueue(
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String res = null;
                        Log.i("SHAGUN", "Pos Response");

                        try {
                            ResponseBody responseBody1 = response.body();
                            if (responseBody1 != null)
                                res = responseBody1.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(res);

                            Log.i("SHAGUN", "jsonObject");

                            if (jsonObject.getString("status").equalsIgnoreCase("success") &&
                                    jsonObject.getString("status_code").equalsIgnoreCase("200")) {

                                Log.i("SHAGUN", "pos status code");
                                SharedPreferences profile_data = getSharedPreferences(Constants.PreferenceFile, 0);
                                SharedPreferences.Editor editor = profile_data.edit();
                                editor.putString(Constants.user_name, user_name);
                                editor.putString(Constants.phone_number, phone_number);
                                editor.putString(Constants.class_id, class_id);

                                Log.i("Subhajit", "Editing Shared Preference");

                                boolean commit = editor.commit();

                                Intent intent = new Intent(LocationActivity.this, SubjectActivity.class);
                                intent.putExtra("class_id", class_id);
                                LocationActivity.this.startActivity(intent);
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
                        Log.i("SHAGUN", t.getMessage());
                    }
                }
        );

    }


}
