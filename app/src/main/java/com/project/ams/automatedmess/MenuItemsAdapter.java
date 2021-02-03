package com.project.ams.automatedmess;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.project.ams.automatedmess.MenuItem;
import com.project.ams.automatedmess.R;

import java.util.ArrayList;

/**
 * Created by surajpulloor on 22/3/18.
 */

public class MenuItemsAdapter extends ArrayAdapter {

    //to reference the Activity
    private Activity context;

    //to store the list of countries
    private ArrayList<MenuItem> menuItems = new ArrayList<>();

    public MenuItemsAdapter(@NonNull Activity context, ArrayList<MenuItem> items) {
        super(context, R.layout.listview_row);

        this.context = context;
        this.menuItems = items;
    }

//    @Override
//    public View getView(int position, View view, ViewGroup parent) {
//        LayoutInflater inflater = context.getLayoutInflater();
//        View rowView = inflater.inflate(R.layout.listview_row, null,true);
//
//        // this code gets references to objects in the listview_row.xml file
//        TextView categoryNameView = rowView.findViewById(R.id.itemNameTextViewID);
//        TextView foodTypeView = rowView.findViewById(R.id.itemPriceTextViewID);
//
//        // this code sets the values of the objects to values from the arrays
//        categoryNameView.setText(menuItems.get(position).getItemName());
//        foodTypeView.setText(menuItems.get(position).getItemPrice());
//
//        return rowView;
//
//    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return menuItems.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return menuItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_row, null,true);

        // this code gets references to objects in the listview_row.xml file
        TextView itemNoView = rowView.findViewById(R.id.itemNoTextViewID);
        TextView categoryNameView = rowView.findViewById(R.id.itemNameTextViewID);
        TextView foodTypeView = rowView.findViewById(R.id.itemPriceTextViewID);

        // this code sets the values of the objects to values from the arrays
        itemNoView.setText(String.valueOf(position + 1));
        categoryNameView.setText(menuItems.get(position).getItemName());
        foodTypeView.setText("₹ " + menuItems.get(position).getItemPrice());

        return rowView;
    }
}
