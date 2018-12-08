package com.urexamhelp.ncertpro;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.widget.ScrollView;
import android.widget.TextView;


import com.urexamhelp.ncertpro.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    TopprWebView mWebView;
    ScrollView scrollView;
    private boolean isMathJaxLoaded;
    private View mLoadingView;
    private String[] data;
    private String res;
    private String pyp_test_id;
    private String pyp_test_name;
    private String responseBody;
    private int page_num;
    private int max_page;
    private TextView nextButton;
    private TextView prevButton;
    private TextView pageNumButton;
    private List<String> questionBodies;
    public static List<String> subjectListToPass;
    public static List<String> typeListToPass;
    private List<String> type_names;
    private List<String> subject_names;
    private Handler handler = new Handler();
    private ProgressDialog progressDialog;


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
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(Constants.TAG, "On create started");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Log.i(Constants.TAG, "Progress bar done");

        initialize_lists();
        getData();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#573c8d")));
        getSupportActionBar().setTitle(pyp_test_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#573c8d"));
        }

        nextButton = (TextView) findViewById(R.id.nextButton);
        prevButton = (TextView) findViewById(R.id.prevButton);
        pageNumButton = (TextView) findViewById(R.id.pageNumButton);
        page_num = 1;

        getAPIResponse();

        mLoadingView = findViewById(R.id.loading_view);

        mWebView = (TopprWebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.clearHistory();
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.addJavascriptInterface(new JsObject(), "jsObject");
        mWebView.loadUrl("file:///android_asset/question_bank.html");

        //mWebView.loadUrl("javascript:displayMockQuestions();");

        nextButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (page_num < max_page) {
                            page_num += 1;
                            Log.i(Constants.TAG,"");
                            set_page_number_text();
                            set_response_body();
                            displayQuestions();
                        }
                    }
                }
        );

        prevButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (page_num >1 ) {
                            page_num -= 1;
                            set_page_number_text();
                            set_response_body();
                            displayQuestions();
                        }
                    }
                }
        );

    }


    //starting the other methods

    private void initialize_lists() {
        subjectListToPass = new ArrayList<>();
        typeListToPass = new ArrayList<>();
    }

    private void set_response_body() {

        Log.i(Constants.TAG, "Setting Response Body");

        int start = (page_num-1) * Constants.question_per_page;
        int end = start + Constants.question_per_page;

        Log.i(Constants.TAG, "Starting from: " + start);
        Log.i(Constants.TAG, "Ending at: " + end);

        if (start > questionBodies.size()){
            responseBody = "[]";
            return;
        }

        if (end > questionBodies.size()){
            end = questionBodies.size();
        }

        StringBuilder res = new StringBuilder();
        res.append("[");

        String between = "";
        for (int i = start; i<end; i++) {
            res.append(between);
            res.append(questionBodies.get(i));
            between = ", ";
        }
        res.append("]");
        responseBody = res.toString();

//        Log.i(Constants.TAG, responseBody);

    }

    private void set_page_number_text() {

        String page_number_string = "Page No: "+ page_num + "/" + max_page;
        pageNumButton.setText(page_number_string);

        Log.i(Constants.TAG,"set page bumber text function done");
    }

    private void getData(){

        Log.i(Constants.TAG, "Get data function called");
        Bundle paperData = getIntent().getExtras();
        pyp_test_id = paperData.getString("chapter_id");
        pyp_test_name = paperData.getString("chapter_name");
    }

    public void getAPIResponse() {

        progressDialog.show();
        Log.i(Constants.TAG, "Calling the API for wv");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.NCERT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OTPService api = retrofit.create(OTPService.class);


        Log.i(Constants.TAG, pyp_test_id + "");
        Call<ResponseBody> result = api.getAllQuesionsResponseBody(pyp_test_id, page_num);


        result.enqueue(
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.i(Constants.TAG, "Pos Resp");
                        try {
                            Log.i(Constants.TAG, "Positive Response");
                            if (response.body() != null) {
                                String resBody = response.body().string();
                                questionBodies = get_all_questions_from_question_body(resBody);
                                page_num = 1;
                                set_max_page();
                                set_response_body();
                                set_page_number_text();
                            }
                            displayQuestions();
                        } catch (IOException e) {
                            Log.i(Constants.TAG, "Some error Occured");
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                        Log.i(Constants.TAG, "Negative Response");

                        Log.i(Constants.TAG, "t.getMessage()");
                    }
                }
        );



    }



    private void set_max_page() {
        Log.i("SHAGUN","reached the set max page function");
        max_page = questionBodies.size() / Constants.question_per_page;
        if (questionBodies.size() % Constants.question_per_page != 0)
            max_page += 1;

        Log.i(Constants.TAG, "max_page: " + max_page);

    }

    private List<String> get_all_questions_from_question_body(String resBody) throws JSONException {

        Log.i(Constants.TAG, "get all question method called");

        List<String> stringList = new ArrayList<>();
        subject_names = new ArrayList<>();
        type_names = new ArrayList<>();

        // Main Response
        JSONArray jsonArray = new JSONArray(resBody);
//        Log.i(Constants.TAG,"Fucking "+ jsonArray.toString());
        for (int i=0; i<jsonArray.length(); i++) {
            JSONObject jsonobject = jsonArray.getJSONObject(i);
            String res = jsonobject.toString();

//            Log.i(Constants.TAG,"PRINTING THE JSON OBJECT"+res);
            stringList.add(res);
        }

       Log.i(Constants.TAG,stringList.toString());
//        Log.i(Constants.TAG, "JSON Array Done");


        return stringList;

    }

    private void displayQuestions() {
        if (!isMathJaxLoaded) {
            mLoadingView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    displayQuestions();
                }
            }, 1000);
            return;
        }

        if (mWebView != null) {

            String s = getStringData();
//            String s = "[{\n" +
//                    "            \"passage_image\": \"\",\n" +
//                    "            \"hint_available\": false,\n" +
//                    "            \"passage_footer\": \"\",\n" +
//                    "            \"sequence_no\": 54,\n" +
//                    "            \"assertion\": \"\",\n" +
//                    "            \"id\": 113850,\n" +
//                    "            \"subject\": \"Maths\",\n" +
//                    "            \"hint\": \"\",\n" +
//                    "            \"can_ask_doubt\": false,\n" +
//                    "            \"question\": \"Let a, b, c be positive integers such that $$\\\\displaystyle \\\\frac{b}{a}$$ is an integer. If $$a, b, c$$ are&nbsp;in geometric progression and the arithmetic mean of $$a, b, c$$ is $$b + 2$$, then the value of $$\\\\displaystyle \\\\frac{a^2+a-14}{a+1}$$ is ................<br>\",\n" +
//                    "            \"is_bookmarked\": false,\n" +
//                    "            \"passage\": \"\",\n" +
//                    "            \"n_pages\": 1,\n" +
//                    "            \"question_status\": \"published\",\n" +
//                    "            \"solution_rating\": 0,\n" +
//                    "            \"question_style_orig\": \"integer answer\",\n" +
//                    "            \"question_linked_to_id\": false,\n" +
//                    "            \"already_attempted\": false,\n" +
//                    "            \"solution_id\": 110272,\n" +
//                    "            \"disable_bookmark\": true,\n" +
//                    "            \"correctly_answered\": false,\n" +
//                    "            \"passage_header\": \"\",\n" +
//                    "            \"solution_image\": \"\",\n" +
//                    "            \"reason\": \"\",\n" +
//                    "            \"mx_l1_images\": [],\n" +
//                    "            \"hint_image\": \"\",\n" +
//                    "            \"mx_l2_images\": [],\n" +
//                    "            \"multiple_correct\": false,\n" +
//                    "            \"question_style\": \"blank\",\n" +
//                    "            \"level\": 2,\n" +
//                    "            \"mx_l2\": [],\n" +
//                    "            \"mx_l1\": [],\n" +
//                    "            \"solution_available\": true,\n" +
//                    "            \"solution\": \"$$\\\\frac{b}{a} = \\\\frac{c}{b} = (integer)$$<br/>$$b^2 = ac \\\\Rightarrow c = \\\\frac{b^2}{a}$$<br/>$$\\\\frac{a+b+c}{3} = b +2$$<br/>$$a+b+c = 3b + 6 &#160;\\\\Rightarrow a - 2b + c= 6$$<br/>$$a-2b + \\\\frac{b^2}{a} = 6 \\\\Rightarrow 1 - \\\\frac{2b}{a} + \\\\frac{b^2}{a^2} = \\\\frac{6}{a}$$<br/>$$\\\\displaystyle \\\\left ( \\\\frac{b}{a}- 1 \\\\right )^2 = \\\\frac{6}{a} &#160;\\\\Rightarrow a = 6 \\\\ only$$<br/><br/>\",\n" +
//                    "            \"choices\": [\n" +
//                    "                {\n" +
//                    "                    \"image\": \"\",\n" +
//                    "                    \"label\": \"A\",\n" +
//                    "                    \"id\": 446877,\n" +
//                    "                    \"choice\": \"$$6$$\",\n" +
//                    "                    \"is_right\": \"false\"\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"image\": \"\",\n" +
//                    "                    \"label\": \"B\",\n" +
//                    "                    \"id\": 446876,\n" +
//                    "                    \"choice\": \"$$5$$\",\n" +
//                    "                    \"is_right\": \"false\"\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"image\": \"\",\n" +
//                    "                    \"label\": \"C\",\n" +
//                    "                    \"id\": 446875,\n" +
//                    "                    \"choice\": \"$$4$$\",\n" +
//                    "                    \"is_right\": \"true\"\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"image\": \"\",\n" +
//                    "                    \"label\": \"D\",\n" +
//                    "                    \"id\": 412299,\n" +
//                    "                    \"choice\": \"$$3$$\",\n" +
//                    "                    \"is_right\": \"false\"\n" +
//                    "                }\n" +
//                    "            ],\n" +
//                    "            \"question_linked\": false\n" +
//                    "        },\n" +
//                    "        {\n" +
//                    "            \"passage_image\": \"\",\n" +
//                    "            \"hint_available\": false,\n" +
//                    "            \"passage_footer\": \"\",\n" +
//                    "            \"sequence_no\": 55,\n" +
//                    "            \"assertion\": \"\",\n" +
//                    "            \"id\": 113855,\n" +
//                    "            \"subject\": \"Maths\",\n" +
//                    "            \"hint\": \"\",\n" +
//                    "            \"can_ask_doubt\": false,\n" +
//                    "            \"question\": \"Let $$\\\\vec{a}, \\\\vec {b}, $$ and $$\\\\vec{c}$$ be three non-coplanar unit vectors such that the angle between every pair of them is $$\\\\dfrac{\\\\pi}{3}$$.&#160;If $$\\\\vec{a} \\\\times&#160;\\\\vec{b} + \\\\vec{b} &#160;\\\\times&#160;\\\\vec{c} = p\\\\vec{a} + q\\\\vec{b}+r\\\\vec{c}$$, where $$p, q$$ and $$r$$ are scalars, then the value of $$\\\\dfrac{p^2 +2q^2 +r^2}{q^2} $$ is\",\n" +
//                    "            \"is_bookmarked\": false,\n" +
//                    "            \"passage\": \"\",\n" +
//                    "            \"n_pages\": 1,\n" +
//                    "            \"question_status\": \"published\",\n" +
//                    "            \"solution_rating\": 0,\n" +
//                    "            \"question_style_orig\": \"integer answer\",\n" +
//                    "            \"question_linked_to_id\": false,\n" +
//                    "            \"already_attempted\": false,\n" +
//                    "            \"solution_id\": 110306,\n" +
//                    "            \"disable_bookmark\": true,\n" +
//                    "            \"correctly_answered\": false,\n" +
//                    "            \"passage_header\": \"\",\n" +
//                    "            \"solution_image\": \"\",\n" +
//                    "            \"reason\": \"\",\n" +
//                    "            \"mx_l1_images\": [],\n" +
//                    "            \"hint_image\": \"\",\n" +
//                    "            \"mx_l2_images\": [],\n" +
//                    "            \"multiple_correct\": false,\n" +
//                    "            \"question_style\": \"blank\",\n" +
//                    "            \"level\": 2,\n" +
//                    "            \"mx_l2\": [],\n" +
//                    "            \"mx_l1\": [],\n" +
//                    "            \"solution_available\": true,\n" +
//                    "            \"solution\": \"$$|\\\\vec{a}| = |\\\\vec{b}|= |\\\\vec c| =1$$<br/>$$\\\\vec a \\\\times&#160;\\\\vec b +&#160;\\\\vec b \\\\times&#160;\\\\vec c = p\\\\vec a + q\\\\vec b + r\\\\vec c$$<br/>$$\\\\Rightarrow \\\\vec a \\\\cdot (\\\\vec b \\\\times&#160;\\\\vec c) = p + q (\\\\vec a \\\\cdot&#160;\\\\vec b) + r (\\\\vec a \\\\cdot&#160;\\\\vec c)$$<br/><br/><div>$$\\\\Rightarrow p + \\\\dfrac{q}{2}+ \\\\dfrac{r}{2} = [\\\\vec {a} \\\\vec b&#160;\\\\vec c]$$ &#160; &#160; ........ $$(1)$$<br/>Similarly,</div><div>$$\\\\dfrac{p}{2} + q + \\\\dfrac{r}{2} = 0$$ &#160; &#160; &#160;....... $$(2)$$<br/><br/></div><div>$$\\\\dfrac{p}{2} + \\\\dfrac{q}{2} + r = [\\\\vec a&#160;\\\\vec b&#160;\\\\vec c]$$ &#160; &#160;..... $$(3)$$<br/><br/></div><div>Solving these equations, we get</div><div>$$\\\\Rightarrow p = r = -q$$</div><div><br/>$$ \\\\dfrac{p^2 + 2q^2 + r^2}{q^2} = 4$$</div><div><br/></div><div>Hence, option $$D$$.<br/></div>\",\n" +
//                    "            \"choices\": [\n" +
//                    "                {\n" +
//                    "                    \"image\": \"\",\n" +
//                    "                    \"label\": \"A\",\n" +
//                    "                    \"id\": 440156,\n" +
//                    "                    \"choice\": \"$$4$$\",\n" +
//                    "                    \"is_right\": \"true\"\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"image\": \"\",\n" +
//                    "                    \"label\": \"B\",\n" +
//                    "                    \"id\": 440155,\n" +
//                    "                    \"choice\": \"$$3$$\",\n" +
//                    "                    \"is_right\": \"false\"\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"image\": \"\",\n" +
//                    "                    \"label\": \"C\",\n" +
//                    "                    \"id\": 440154,\n" +
//                    "                    \"choice\": \"$$2$$\",\n" +
//                    "                    \"is_right\": \"false\"\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"image\": \"\",\n" +
//                    "                    \"label\": \"D\",\n" +
//                    "                    \"id\": 412316,\n" +
//                    "                    \"choice\": \"$$1$$\",\n" +
//                    "                    \"is_right\": \"false\"\n" +
//                    "                }\n" +
//                    "            ],\n" +
//                    "            \"question_linked\": false\n" +
//                    "        },\n" +
//                    "        {\n" +
//                    "            \"passage_image\": \"\",\n" +
//                    "            \"hint_available\": false,\n" +
//                    "            \"passage_footer\": \"\",\n" +
//                    "            \"sequence_no\": 56,\n" +
//                    "            \"assertion\": \"\",\n" +
//                    "            \"id\": 113861,\n" +
//                    "            \"subject\": \"Maths\",\n" +
//                    "            \"hint\": \"\",\n" +
//                    "            \"can_ask_doubt\": false,\n" +
//                    "            \"question\": \"The slope of the tangent to the curve $$(y - x^5)^2 = x(1+x^2)^2$$ at the point $$(1, 3)$$ is ..................\",\n" +
//                    "            \"is_bookmarked\": false,\n" +
//                    "            \"passage\": \"\",\n" +
//                    "            \"n_pages\": 1,\n" +
//                    "            \"question_status\": \"published\",\n" +
//                    "            \"solution_rating\": 0,\n" +
//                    "            \"question_style_orig\": \"integer answer\",\n" +
//                    "            \"question_linked_to_id\": false,\n" +
//                    "            \"already_attempted\": false,\n" +
//                    "            \"solution_id\": 110295,\n" +
//                    "            \"disable_bookmark\": true,\n" +
//                    "            \"correctly_answered\": false,\n" +
//                    "            \"passage_header\": \"\",\n" +
//                    "            \"solution_image\": \"\",\n" +
//                    "            \"reason\": \"\",\n" +
//                    "            \"mx_l1_images\": [],\n" +
//                    "            \"hint_image\": \"\",\n" +
//                    "            \"mx_l2_images\": [],\n" +
//                    "            \"multiple_correct\": false,\n" +
//                    "            \"question_style\": \"blank\",\n" +
//                    "            \"level\": 2,\n" +
//                    "            \"mx_l2\": [],\n" +
//                    "            \"mx_l1\": [],\n" +
//                    "            \"solution_available\": true,\n" +
//                    "            \"solution\": \"$$2 (y-x^5) \\\\displaystyle \\\\left ( \\\\dfrac{dy}{dx} - 5x^4\\\\right )$$<br/>$$=1(x+x^2)^2 + (x) (2(1+x^2)(2x))$$<br/>Now put $$x=1, y = 3$$ and $$\\\\displaystyle \\\\dfrac{dy}{dx} = m$$<br/>$$2(3-1) (m-5) =1 (4) + (1) (4)(2)$$<br/>$$ m -5 = \\\\displaystyle \\\\dfrac{12}{4}$$<br/>$$m = 5 + 3 = 8$$<br/>$$\\\\displaystyle \\\\frac{dy}{dx} = m = 8$$<br/>\",\n" +
//                    "            \"choices\": [\n" +
//                    "                {\n" +
//                    "                    \"image\": \"\",\n" +
//                    "                    \"label\": \"A\",\n" +
//                    "                    \"id\": 412337,\n" +
//                    "                    \"choice\": \"8\",\n" +
//                    "                    \"is_right\": \"true\"\n" +
//                    "                }\n" +
//                    "            ],\n" +
//                    "            \"question_linked\": false\n" +
//                    "        },\n" +
//                    "        {\n" +
//                    "            \"passage_image\": \"\",\n" +
//                    "            \"hint_available\": false,\n" +
//                    "            \"passage_footer\": \"\",\n" +
//                    "            \"sequence_no\": 57,\n" +
//                    "            \"assertion\": \"\",\n" +
//                    "            \"id\": 113866,\n" +
//                    "            \"subject\": \"Maths\",\n" +
//                    "            \"hint\": \"\",\n" +
//                    "            \"can_ask_doubt\": false,\n" +
//                    "            \"question\": \"The value of $$\\\\displaystyle \\\\int_0^1 4x^3 \\\\left \\\\{ \\\\frac{d^2}{dx^2} (1-x^2)^5 \\\\right \\\\} dx$$ is?\",\n" +
//                    "            \"is_bookmarked\": false,\n" +
//                    "            \"passage\": \"\",\n" +
//                    "            \"n_pages\": 1,\n" +
//                    "            \"question_status\": \"published\",\n" +
//                    "            \"solution_rating\": 0,\n" +
//                    "            \"question_style_orig\": \"integer answer\",\n" +
//                    "            \"question_linked_to_id\": false,\n" +
//                    "            \"already_attempted\": false,\n" +
//                    "            \"solution_id\": 110296,\n" +
//                    "            \"disable_bookmark\": true,\n" +
//                    "            \"correctly_answered\": false,\n" +
//                    "            \"passage_header\": \"\",\n" +
//                    "            \"solution_image\": \"\",\n" +
//                    "            \"reason\": \"\",\n" +
//                    "            \"mx_l1_images\": [],\n" +
//                    "            \"hint_image\": \"\",\n" +
//                    "            \"mx_l2_images\": [],\n" +
//                    "            \"multiple_correct\": false,\n" +
//                    "            \"question_style\": \"blank\",\n" +
//                    "            \"level\": 2,\n" +
//                    "            \"mx_l2\": [],\n" +
//                    "            \"mx_l1\": [],\n" +
//                    "            \"solution_available\": true,\n" +
//                    "            \"solution\": \"<div>$$\\\\displaystyle \\\\displaystyle \\\\int_0^1 4\\\\underset{I}{x^3} \\\\frac{d^2}{dx^2} (1-\\\\underset{II}{x^2})^5 dx$$</div><div><div>$$= \\\\displaystyle \\\\left [ 4x^3 \\\\frac{d}{dx} (1-x^2)^5 \\\\right ]_0^1 - \\\\displaystyle \\\\int_0^1 12x^2 \\\\frac{d}{dx} (1 - x^2)^5 dx$$</div><div>$$=\\\\displaystyle \\\\left [ 4x^3 \\\\times 5(1-x^2)^4 (-2x) \\\\right ]_0^1 - 12 \\\\left [ \\\\left [ x^2 (1-x^2)^5 \\\\right ]_0^1 - \\\\displaystyle \\\\int_0^1 2x (1-x^2)^5 dx \\\\right ]$$</div></div><div><span>$$=0-0-12 [0-0] + 12 \\\\displaystyle \\\\int_0^1 2x (1-x^2)^5 dx$$</span><br/></div><div>$$=12 \\\\times \\\\displaystyle \\\\left [ - \\\\frac{(1-x^2)^6}{6} \\\\right ]_0^1$$</div><div>$$=12 \\\\left [ 0 + \\\\dfrac{1}{6} \\\\right ] = 2$$</div>\",\n" +
//                    "            \"choices\": [\n" +
//                    "                {\n" +
//                    "                    \"image\": \"\",\n" +
//                    "                    \"label\": \"A\",\n" +
//                    "                    \"id\": 412354,\n" +
//                    "                    \"choice\": \"2\",\n" +
//                    "                    \"is_right\": \"true\"\n" +
//                    "                }\n" +
//                    "            ],\n" +
//                    "            \"question_linked\": false\n" +
//                    "        },\n" +
//                    "        {\n" +
//                    "            \"passage_image\": \"\",\n" +
//                    "            \"hint_available\": false,\n" +
//                    "            \"passage_footer\": \"\",\n" +
//                    "            \"sequence_no\": 58,\n" +
//                    "            \"assertion\": \"\",\n" +
//                    "            \"id\": 113869,\n" +
//                    "            \"subject\": \"Maths\",\n" +
//                    "            \"hint\": \"\",\n" +
//                    "            \"can_ask_doubt\": false,\n" +
//                    "            \"question\": \"The largest value of the non-negative integer $$a$$ for which&#160; &#160;$$\\\\displaystyle \\\\lim_{x \\\\rightarrow 1} \\\\displaystyle \\\\left \\\\{ \\\\dfrac{-ax + \\\\sin (x-1)+ a}{x+\\\\sin (x-1)-1} \\\\right \\\\}^{\\\\dfrac{1-x}{1-\\\\sqrt{x}}} = \\\\dfrac{1}{4} $$ is ................\",\n" +
//                    "            \"is_bookmarked\": false,\n" +
//                    "            \"passage\": \"\",\n" +
//                    "            \"n_pages\": 1,\n" +
//                    "            \"question_status\": \"published\",\n" +
//                    "            \"solution_rating\": 0,\n" +
//                    "            \"question_style_orig\": \"integer answer\",\n" +
//                    "            \"question_linked_to_id\": false,\n" +
//                    "            \"already_attempted\": false,\n" +
//                    "            \"solution_id\": 110290,\n" +
//                    "            \"disable_bookmark\": true,\n" +
//                    "            \"correctly_answered\": false,\n" +
//                    "            \"passage_header\": \"\",\n" +
//                    "            \"solution_image\": \"\",\n" +
//                    "            \"reason\": \"\",\n" +
//                    "            \"mx_l1_images\": [],\n" +
//                    "            \"hint_image\": \"\",\n" +
//                    "            \"mx_l2_images\": [],\n" +
//                    "            \"multiple_correct\": false,\n" +
//                    "            \"question_style\": \"blank\",\n" +
//                    "            \"level\": 2,\n" +
//                    "            \"mx_l2\": [],\n" +
//                    "            \"mx_l1\": [],\n" +
//                    "            \"solution_available\": true,\n" +
//                    "            \"solution\": \"$$\\\\displaystyle \\\\lim_{x \\\\rightarrow 1} \\\\left ( \\\\displaystyle \\\\frac{-ax + sin (x-1)+a}{x+sin (x-1)-1} \\\\right )^{\\\\displaystyle \\\\frac{1-x}{1-\\\\sqrt{x}}} = \\\\displaystyle \\\\frac{1}{4}$$<br/><br/><div>$$\\\\displaystyle \\\\lim_{x<br/>\\\\rightarrow 1} \\\\displaystyle \\\\left ( \\\\displaystyle \\\\frac{\\\\displaystyle \\\\frac{sin <br/>(x-1)}{(x-1)}-a}{\\\\displaystyle \\\\frac{sin(x-1)}{(x-1)}+1}\\\\right )^{1+\\\\sqrt{x}} = <br/>\\\\displaystyle \\\\frac{1}{4} \\\\Rightarrow &#160;\\\\left (\\\\displaystyle &#160;\\\\frac{1-a}{2} \\\\right)^2 =\\\\displaystyle &#160;\\\\frac{1}{4}$$<br/><br/></div><div>$$\\\\Rightarrow a = 0, &#160;a= 2$$<br/>But at $$a = 2, \\\\dfrac{-ah + sinh}{h + sinh}$$ tends to negative value<span>,<br/>So correct answer is $$a = 0$$.</span><br/></div>\",\n" +
//                    "            \"choices\": [\n" +
//                    "                {\n" +
//                    "                    \"image\": \"\",\n" +
//                    "                    \"label\": \"A\",\n" +
//                    "                    \"id\": 412363,\n" +
//                    "                    \"choice\": \"0\",\n" +
//                    "                    \"is_right\": \"true\"\n" +
//                    "                }\n" +
//                    "            ],\n" +
//                    "            \"question_linked\": false\n" +
//                    "        },\n" +
//                    "        {\n" +
//                    "            \"passage_image\": \"\",\n" +
//                    "            \"hint_available\": false,\n" +
//                    "            \"passage_footer\": \"\",\n" +
//                    "            \"sequence_no\": 59,\n" +
//                    "            \"assertion\": \"\",\n" +
//                    "            \"id\": 113872,\n" +
//                    "            \"subject\": \"Maths\",\n" +
//                    "            \"hint\": \"\",\n" +
//                    "            \"can_ask_doubt\": false,\n" +
//                    "            \"question\": \"Let $$f: [0, 4\\\\pi] \\\\rightarrow [0, \\\\pi]$$ be defined by $$f(x) = cos^{-1} (cos &nbsp;x)$$. The number of points $$x \\\\in [0, 4\\\\pi]$$ satisfying the equation $$f(x) = \\\\dfrac{10-x}{10}$$ is ................\",\n" +
//                    "            \"is_bookmarked\": false,\n" +
//                    "            \"passage\": \"\",\n" +
//                    "            \"n_pages\": 1,\n" +
//                    "            \"question_status\": \"published\",\n" +
//                    "            \"solution_rating\": 0,\n" +
//                    "            \"question_style_orig\": \"integer answer\",\n" +
//                    "            \"question_linked_to_id\": false,\n" +
//                    "            \"already_attempted\": false,\n" +
//                    "            \"solution_id\": 110286,\n" +
//                    "            \"disable_bookmark\": true,\n" +
//                    "            \"correctly_answered\": false,\n" +
//                    "            \"passage_header\": \"\",\n" +
//                    "            \"solution_image\": \"https://haygot.s3.amazonaws.com/questions/110286_113872_ans.jpg\",\n" +
//                    "            \"reason\": \"\",\n" +
//                    "            \"mx_l1_images\": [],\n" +
//                    "            \"hint_image\": \"\",\n" +
//                    "            \"mx_l2_images\": [],\n" +
//                    "            \"multiple_correct\": false,\n" +
//                    "            \"question_style\": \"blank\",\n" +
//                    "            \"level\": 2,\n" +
//                    "            \"mx_l2\": [],\n" +
//                    "            \"mx_l1\": [],\n" +
//                    "            \"solution_available\": true,\n" +
//                    "            \"solution\": \"$$f:[0, 4 \\\\pi] &nbsp;\\\\rightarrow [0, \\\\pi&nbsp;], f(x) = cos^{-1} (cos &nbsp;x)$$<br>$$\\\\Rightarrow $$ point A, B, C satisfy $$f (x) = \\\\frac{10-x}{10}$$<br>Hence, 3 points<br>\",\n" +
//                    "            \"choices\": [\n" +
//                    "                {\n" +
//                    "                    \"image\": \"\",\n" +
//                    "                    \"label\": \"A\",\n" +
//                    "                    \"id\": 412372,\n" +
//                    "                    \"choice\": \"3\",\n" +
//                    "                    \"is_right\": \"true\"\n" +
//                    "                }\n" +
//                    "            ],\n" +
//                    "            \"question_linked\": false\n" +
//                    "        },\n" +
//                    "        {\n" +
//                    "            \"passage_image\": \"\",\n" +
//                    "            \"hint_available\": false,\n" +
//                    "            \"passage_footer\": \"\",\n" +
//                    "            \"sequence_no\": 60,\n" +
//                    "            \"assertion\": \"\",\n" +
//                    "            \"id\": 113873,\n" +
//                    "            \"subject\": \"Maths\",\n" +
//                    "            \"hint\": \"\",\n" +
//                    "            \"can_ask_doubt\": false,\n" +
//                    "            \"question\": \"For a point $$P$$ in the plane, let $$d_1$$ (P) and $$d_2$$ (P) be the distances of the point P from the lines $$x-y = 0$$ and $$x+y = 0$$ respectively. The area of the region R consisting of all points P lying in the first quadrant of the plane and satisfying $$2 \\\\leq d_1 (P) + d_2 (P) \\\\leq 4$$, is&nbsp;\",\n" +
//                    "            \"is_bookmarked\": false,\n" +
//                    "            \"passage\": \"\",\n" +
//                    "            \"n_pages\": 1,\n" +
//                    "            \"question_status\": \"published\",\n" +
//                    "            \"solution_rating\": 0,\n" +
//                    "            \"question_style_orig\": \"integer answer\",\n" +
//                    "            \"question_linked_to_id\": false,\n" +
//                    "            \"already_attempted\": false,\n" +
//                    "            \"solution_id\": 110301,\n" +
//                    "            \"disable_bookmark\": true,\n" +
//                    "            \"correctly_answered\": false,\n" +
//                    "            \"passage_header\": \"\",\n" +
//                    "            \"solution_image\": \"https://haygot.s3.amazonaws.com/questions/110301_113873_ans.jpg\",\n" +
//                    "            \"reason\": \"\",\n" +
//                    "            \"mx_l1_images\": [],\n" +
//                    "            \"hint_image\": \"\",\n" +
//                    "            \"mx_l2_images\": [],\n" +
//                    "            \"multiple_correct\": false,\n" +
//                    "            \"question_style\": \"blank\",\n" +
//                    "            \"level\": 2,\n" +
//                    "            \"mx_l2\": [],\n" +
//                    "            \"mx_l1\": [],\n" +
//                    "            \"solution_available\": true,\n" +
//                    "            \"solution\": \"$$2 \\\\leq d_1 (p) + d_2 (p) &#160;\\\\leq 4$$<br/>For $$P(\\\\alpha, \\\\beta), \\\\alpha &gt; \\\\beta$$<br/>$$\\\\Rightarrow 2 \\\\sqrt{2} &#160;\\\\leq 2\\\\alpha &#160;\\\\leq 4\\\\sqrt{2}$$<br/>$$\\\\sqrt{2} \\\\leq \\\\alpha \\\\leq 2 \\\\sqrt{2}$$<br/>$$\\\\Rightarrow$$ &#160;Area &#160; of &#160;region $$= \\\\displaystyle \\\\left ( (2 \\\\sqrt{2})^2 - (\\\\sqrt{2})^2 \\\\right )$$<br/>&#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160; &#160;&#160;&#160; &#160; $$= 8 -2 = 6$$ sq. &#160;units<br/>\",\n" +
//                    "            \"choices\": [\n" +
//                    "                {\n" +
//                    "                    \"image\": \"\",\n" +
//                    "                    \"label\": \"A\",\n" +
//                    "                    \"id\": 439750,\n" +
//                    "                    \"choice\": \"$$16$$\",\n" +
//                    "                    \"is_right\": \"false\"\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"image\": \"\",\n" +
//                    "                    \"label\": \"B\",\n" +
//                    "                    \"id\": 439749,\n" +
//                    "                    \"choice\": \"$$10$$\",\n" +
//                    "                    \"is_right\": \"false\"\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"image\": \"\",\n" +
//                    "                    \"label\": \"C\",\n" +
//                    "                    \"id\": 439748,\n" +
//                    "                    \"choice\": \"$$6$$\",\n" +
//                    "                    \"is_right\": \"true\"\n" +
//                    "                },\n" +
//                    "                {\n" +
//                    "                    \"image\": \"\",\n" +
//                    "                    \"label\": \"D\",\n" +
//                    "                    \"id\": 412373,\n" +
//                    "                    \"choice\": \"$$4$$\",\n" +
//                    "                    \"is_right\": \"false\"\n" +
//                    "                }\n" +
//                    "            ],\n" +
//                    "            \"question_linked\": false\n" +
//                    "        }]";

            Log.i(Constants.TAG, s);

            byte[] data = new byte[0];
            try {
                data = s.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String base64 = Base64.encodeToString(data, Base64.DEFAULT);
            mWebView.loadUrl(String.format("javascript:displayQuestions('%1$s');", base64));
            mWebView.scrollTo(0, 0);
        }
        progressDialog.dismiss();

    }

    private String getStringData(){
        if (responseBody != null){
//            Log.i(Constants.TAG, responseBody);
//            Log.i(Constants.TAG, responseBody);
            return responseBody;
        }else {
            Log.i(Constants.TAG, "responseBody Empty");
        }
        return "";
    }

    private class JsObject {

        @JavascriptInterface
        public void mathjax_done() {
            isMathJaxLoaded = true;
        }

        @JavascriptInterface
        public void submitAnswer(String questionId, String choices) {
            callbackSubmitAnswer(questionId, choices);
        }

        @JavascriptInterface
        public void addBookmark(String questionId) {
            //SegmentUtils.sendTapEvent("chapter.question_sets",
            //        SegmentElementConstants.ELEMENT_BOOKMARK,"");
            //callbackBookmark(questionId, true);
        }

        @JavascriptInterface
        public void removeBookmark(String questionId) {
            //unBookMark(questionId);
        }

        @JavascriptInterface
        public void showConcepts(String questionId) {
            //showConceptsOfQuestion(questionId);
        }

        @JavascriptInterface
        public void showLectures(String questionId) {
            //showVideosOfQuestion(questionId);
        }

        @JavascriptInterface
        public void askDoubt(String questionId) {
            //askDoubts(questionId);
        }

        @JavascriptInterface
        public void showToast(String message) {
            //showToastMessage(message);
        }

        @JavascriptInterface
        public void displayImage(String url) {
            if(!TextUtils.isEmpty(url)) {
                //ConceptDisplayImageActivity.startActivity(new WeakReference<Activity>(getActivity()), url);
            }
        }


        @JavascriptInterface
        public void showNotebooks(String id, boolean isBookmarked){
            //showNotebooksList(id,isBookmarked);
        }

    }

    private void callbackSubmitAnswer(String questionId, String choices) {
        String s = "{is_correctly_answered: false, right_answers: [1656028]}";
        //String encoded = new String( Base64.encodeBase64("Test".getBytes()));

        byte[] data = new byte[0];
        try {
            data = s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);

        onAnswerSubmitted(questionId, base64, null);
        Log.d("<--------->", base64);
        //mWebView.loadUrl("javascript:onAnswerSubmitted('" + questionId + "', '" + base64  + "');");
    }

    private void onAnswerSubmitted(String base64, String questionId, String questionInfo) {
        //mWebView.loadUrl("javascript:onAnswerSubmitted('" + questionId + "', '" + base64 + "', '" + questionInfo+ "');");
    }


}



