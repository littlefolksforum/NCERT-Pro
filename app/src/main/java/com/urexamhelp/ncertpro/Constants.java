package com.urexamhelp.ncertpro;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Constants {


     //public static final String BASE_URL = "http://172.31.1.38:8000";
    public static final String BASE_URL = "http://ncert.stage.toppr.io";
    public static final String NCERT_URL = BASE_URL + "/ncert/api/";
    public static final String PROFILE_URL = BASE_URL + "/profile/api/2/";
    public static final String FEEDBACK_URL = BASE_URL + "/feedback/api/";;

    public static final String progressTitle = "Loading";
    public static final String progressText = "Loading";
    public static final String PreferenceFile = "NCERTPreferenceFile";
    public static final String phone_number = "phone_number";
    public static final String user_name = "user_name";
    public static final String class_id = "class_id";
    public static final String location_title = "Where are you From ?";
    public static final String LOCATION_SEARCH_URL = "https://maps.googleapis.com/maps/api/";
    public static final String TYPE_FILTER_LOCATION = "(regions)";
    public static final String COMPONENTS = "country:ind";
    public static final String API_KEY = "AIzaSyCDdT7clO_j2NVo3jhja5tDcOywBN1xIpg";
    public static final int question_per_page = 10;
    public static final String TAG ="SHAGUN";
    public static final String know_you_better_text = "We would like to know you better";

    // public static final String NCERT_URL = "http://ncert.stage.toppr.io/ncert/api/";
   // public static final String  PROFILE_URL = "http://ncert.stage.toppr.io/profile/api/";
    // public static final String FEEDBACK_URL = "http://ncert.stage.toppr.io/feedback/api/";

    public static final String no_internet_message = "Please connect to the Internet";
    public static String OTP_resend_msg = "Can Only Resend OTP Once";
    public static String sending_otp = "Sending another OTP";
    public static final String profile_text = "Profile";
    public static String empty_form = "Please fill out one of the fields";
    public static final String something_went_wrong = "Something Went Wrong !!";



    static final String PHYSICS_COLOR = "#F6C15E";
 static final String CHEMISTRY_COLOR = "#F77C7C";
 static final String MATHS_COLOR = "#5395DC";
 static final String BIOLOGY_COLOR = "#93C57C";
 static final String ENGLISH_COLOR = "#EEB185";
 static final String MENTAL_APPTITUDE_COLOR = "#D149A6";
 static final String HISTORY_COLOR = "#F576A1";
 static final String GEOGRAPHY_COLOR = "#FF8A65";
 static final String CIVICS_COLOR = "#3FC5C8";
 static final String LOGICAL_REASONING_COLOR = "#E7596F";
 static final String SOCIOLOGY_COLOR = "#52CF9E";
 static final String EVS_COLOR = "#5AAC7E";
 static final String SCIENCE_COLOR = "#6D7ED7";
 static final String ECONOMICS_COLOR = "#00A174";
 static final String GENERAL_KNOWLEDGE_COLOR = "#c74ca7";
 static final String ACCOUNTANCY_COLOR = "#3586d4";
 static final String BUSINESS_STUDIES_COLOR = "#cca58f";
 static final String OTHER_COLOR = "#BCCF7E";

 static final String PHYSICS_ST_COLOR = "#FFFBF5";
 static final String CHEMISTRY_ST_COLOR = "#F2EBEC";
 static final String MATHS_ST_COLOR = "#ECF0F2";
 static final String BIOLOGY_ST_COLOR = "#ECF0ED";
 static final String ENGLISH_ST_COLOR = "#E6E6E6";
 static final String MENTAL_APPTITUDE_ST_COLOR = "#F8F0F4";
 static final String HISTORY_ST_COLOR = "#FEF6ED";
 static final String GEOGRAPHY_ST_COLOR = "#EAF8F6";
 static final String CIVICS_ST_COLOR = "#F6F3FC";
 static final String LOGICAL_REASONING_ST_COLOR = "#FCF7F9";
 static final String SOCIOLOGY_ST_COLOR = "#FCF7F9";
 static final String EVS_ST_COLOR = "#F9FDF7";
 static final String SCIENCE_ST_COLOR = "#FFFBF7";
 static final String ECONOMICS_ST_COLOR = "#FFFAF7";
 static final String GENERAL_KNOWLEDGE_ST_COLOR = "#FFFAF7";
 static final String ACCOUNTANCY_ST_COLOR = "#FFFBF5";
 static final String BUSINESS_STUDIES_ST_COLOR = "#FFFBF5";
 static final String OTHER_ST_COLOR = "#FFFBF5";





 public static final String PHYSICS_SUBJECT_NAME = "Physics";
 public static final String CHEMISTRY_SUBJECT_NAME = "Chemistry";
 public static final String MATHS_SUBJECT_NAME = "Maths";
 public static final String BIOLOGY_SUBJECT_NAME = "Biology";
 public static final String ENGLISH_SUBJECT_NAME = "English";
 public static final String MENTAL_APPTITUDE_SUBJECT_NAME = "Mental Apptitude";
 public static final String HISTORY_SUBJECT_NAME = "History";
 public static final String GEOGRAPHY_SUBJECT_NAME = "Geography";
 public static final String CIVICS_SUBJECT_NAME = "Civics";
 public static final String LOGICAL_REASONING_SUBJECT_NAME = "Logical reasoning";
 public static final String SOCIOLOGY_SUBJECT_NAME = "Sociology";
 public static final String EVS_SUBJECT_NAME = "Evs";
 public static final String SCIENCE_SUBJECT_NAME = "Science";
 public static final String ECONOMICS_SUBJECT_NAME = "Economics";
 public static final String SOCIAL_SCIENCE_NAME = "Social Science";
 public static final String EVS_2_NAME = "EVS - II";
 public static final String EVS_1_NAME = "EVS - I";
 public static final String POLITICAL_SCIENCE_NAME = "Political Science";
 public static final String GENERAL_KNOWLEDGE_NAME = "General Knowledge";
 public static final String BUSINESS_STUDIES_NAME = "Business Studies";
 public static final String ACCOUNTANCY_NAME = "Accountancy";





    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        Boolean status = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (!status)
            Toast.makeText(context, Constants.no_internet_message, Toast.LENGTH_SHORT).show();
        return status;
    }

}
