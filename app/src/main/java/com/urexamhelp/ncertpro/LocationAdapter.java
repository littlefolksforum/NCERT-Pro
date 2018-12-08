package com.urexamhelp.ncertpro;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.urexamhelp.ncertpro.R;

import java.util.HashMap;
import java.util.List;

public class LocationAdapter extends ArrayAdapter<String> {

    private HashMap<String, String> hashMap = new HashMap<>();
    private int selected_button_position;
    private String selected_button_place_id;
    private HashMap<String, String> hashMapId;

    public LocationAdapter(@NonNull Context context, @NonNull List<String> objects, HashMap<String, String> hashMap, int selected_button_position) {
        super(context, R.layout.location_search_row, objects);
        this.hashMap = hashMap;
        this.selected_button_position = selected_button_position;
        this.selected_button_place_id = selected_button_place_id;
        this.hashMapId = hashMapId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater myInflator = LayoutInflater.from(getContext());
        View resultRowView = myInflator.inflate(R.layout.location_search_row, parent, false);

        String main_text = getItem(position);
        String secondary_text = hashMap.get(main_text);

        TextView mainTV = (TextView) resultRowView.findViewById(R.id.main);
        TextView secondaryTV = (TextView) resultRowView.findViewById(R.id.submain);
        RadioButton radioButton = (RadioButton) resultRowView.findViewById(R.id.radioButton);

        mainTV.setText(main_text);
        secondaryTV.setText(secondary_text);

//        if (position == selected_button_position || hashMapId.get(main_text).equalsIgnoreCase(selected_button_place_id)) {
        if (position == selected_button_position) {
            radioButton.setChecked(true);
        }
        else {
            radioButton.setChecked(false);
        }

        return resultRowView;

    }
}
