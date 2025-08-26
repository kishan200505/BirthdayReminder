package com.example.birthdayreminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class BirthdayAdapter extends ArrayAdapter<Birthday> {
    private List<Birthday> originalBirthdays;
    private List<Birthday> filteredBirthdays;

    public BirthdayAdapter(Context context, List<Birthday> birthdays) {
        super(context, 0, birthdays);
        this.originalBirthdays = new ArrayList<>(birthdays);
        this.filteredBirthdays = birthdays;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_birthday, parent, false);
        }

        Birthday birthday = getItem(position);
        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        TextView dateTextView = convertView.findViewById(R.id.dateTextView);

        if (birthday != null) {
            nameTextView.setText(birthday.getName());
            dateTextView.setText(String.format("%02d/%02d", birthday.getMonth(), birthday.getDay()));
        } else {
            nameTextView.setText("");
            dateTextView.setText("");
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Birthday> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(originalBirthdays);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Birthday birthday : originalBirthdays) {
                        if (birthday.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(birthday);
                        }
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredBirthdays.clear();
                filteredBirthdays.addAll((List<Birthday>) results.values);
                notifyDataSetChanged();
            }
        };
    }
}