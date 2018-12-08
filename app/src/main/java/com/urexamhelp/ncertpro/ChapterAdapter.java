package com.urexamhelp.ncertpro;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.urexamhelp.ncertpro.R;

import java.util.List;

public class ChapterAdapter extends ArrayAdapter<Chapter>{
    private Chapter chapter;
    private String subject_name;
    public ChapterAdapter(@NonNull Context context, List<Chapter> resource,String subject_name) {
        super(context, R.layout.custom_chapter_list, resource);
        this.subject_name=subject_name;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Log.i(Constants.TAG, "" + position);

        LayoutInflater myInflator = LayoutInflater.from(getContext());
        View resultRowView = myInflator.inflate(R.layout.custom_chapter_list, parent, false);

        chapter = getItem(position);

        TextView chapterNum = (TextView) resultRowView.findViewById(R.id.numberTV);
        TextView questionCountTV = (TextView) resultRowView.findViewById(R.id.questionCountTV);
        TextView chapterNameTV = (TextView) resultRowView.findViewById(R.id.chapterNameTV);

        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Proxima-Nova-Semibold.ttf");
        chapterNum.setTypeface(tf);
        questionCountTV.setTypeface(tf);
        chapterNameTV.setTypeface(tf);



        // Set the Number
        String num;
        int position_written = position + 1;
        if (position_written < 10) num = "0" + position_written;
        else num = "" + position_written;
        chapterNum.setText(num);
        chapterNum.setTextColor(Color.parseColor(getSubjectColor(subject_name)));



        // Set the Chapter Name
        assert chapter != null;
        String subjectNameText = chapter.getChapter_name();
        chapterNameTV.setText(subjectNameText);


        // Set the Question Count
        String question_count;
        if (chapter.getQuestion_count() == 1) question_count = chapter.getQuestion_count() + " Questions";
        else question_count = chapter.getQuestion_count() + " Questions";
        questionCountTV.setText(question_count);

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
