package com.project.ams.automatedmess;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by surajpulloor on 24/3/18.
 */

public class CustomerOrder_ItemSelectionAdapter extends ArrayAdapter {

    //to reference the Activity
    private Activity context;

    //to store the list of countries
    private ArrayList<MenuItem> menuItems = new ArrayList<>();

    public CustomerOrder_ItemSelectionAdapter(@NonNull Activity context, ArrayList<MenuItem> items) {
        super(context, R.layout.item_selection_listview);

        this.context = context;
        this.menuItems = items;
    }

    @Override
    public int getCount() {
        return menuItems.size();
    }

    @Override
    public Object getItem(int position) {
        return menuItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.item_selection_listview, null,true);

        // this code gets references to objects in the listview_row.xml file
        TextView itemNoView = rowView.findViewById(R.id.itemNoTextViewID);
        TextView categoryNameView = rowView.findViewById(R.id.itemNameTextViewID);
        TextView foodTypeView = rowView.findViewById(R.id.itemPriceTextViewID);
        NumberPicker np = rowView.findViewById(R.id.itemNumberPicker);

        //Populate NumberPicker values from minimum and maximum value range
        //Set the minimum value of NumberPicker
        np.setMinValue(0);
        //Specify the maximum value/number of NumberPicker
        np.setMaxValue(10);

        // Set the default value for the number picker
        np.setValue(1);

        //Gets whether the selector wheel wraps when reaching the min/max value.
        np.setWrapSelectorWheel(true);

        // Set the shared numberPickerVals data structure
        ShareNumberPickersValues.getInstance().numberPickerVals.append(position, np.getValue());


        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                ShareNumberPickersValues.getInstance().numberPickerVals.append(position, numberPicker.getValue());
            }
        });


        // this code sets the values of the objects to values from the arrays
        itemNoView.setText(String.valueOf(position + 1));
        categoryNameView.setText(menuItems.get(position).getItemName());
        foodTypeView.setText("₹ " + menuItems.get(position).getItemPrice());

        return rowView;
    }
}
