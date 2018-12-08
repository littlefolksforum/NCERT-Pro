package com.urexamhelp.ncertpro;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.urexamhelp.ncertpro.R;

import java.util.List;

public class SubjectAdapter extends ArrayAdapter<Subject> {


    private Subject subject;
    String imgUrl;

    public SubjectAdapter(@NonNull Context context, List<Subject> resource) {
        super(context, R.layout.custom_suject_list, resource);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {



        LayoutInflater myInflator = LayoutInflater.from(getContext());
        View resultRowView = myInflator.inflate(R.layout.custom_suject_list, parent, false);

        subject = getItem(position);


        ImageView subjectImg = (ImageView) resultRowView.findViewById(R.id.imageView);
        TextView chapterCountTV = (TextView) resultRowView.findViewById(R.id.chapterCountTV);
        TextView subjectNameTV = (TextView) resultRowView.findViewById(R.id.subjectNameTV);

        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Proxima-Nova-Semibold.ttf");
        chapterCountTV.setTypeface(tf);
        subjectNameTV.setTypeface(tf);



        // Set the Number
        String num;
        int position_written = position + 1;
        if (position_written < 10) num = "0" + position_written;
        else num = "" + position_written;


        //Set the subject Name and the image
        String subjectNameText = subject.getSubject_name();
        subjectNameTV.setText(subjectNameText);
        RelativeLayout lLayout = (RelativeLayout) resultRowView.findViewById(R.id.imageViewLayout);
        Drawable background = lLayout.getBackground();
        background.setColorFilter(new
                PorterDuffColorFilter(Color.parseColor(getSubjectColor(subjectNameText)), PorterDuff.Mode.SRC_ATOP));
        lLayout.setBackground(background);

        imgUrl = "http://android-assets.toppr.com/icons/subjects/size_36/drawable-xxhdpi/ic_" + subjectNameText.replace(" ", "-").toLowerCase()+ ".png";

        Glide.with(getContext()).load(imgUrl)
                //.thumbnail(0.5f)
                //.crossFade()
                //.diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(subjectImg);


        // Set the Chapter Count
        String chapter_count;
        if (subject.getChapter_count() == 1)
            chapter_count = subject.getChapter_count() + " Chapters";
        else chapter_count = subject.getChapter_count() + " Chapters";
        chapterCountTV.setText(chapter_count);

        return resultRowView;
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


}
