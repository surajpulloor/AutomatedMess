package com.project.ams.automatedmess;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by surajpulloor on 23/3/18.
 */

public class CustomerOrder_CategoryAdapter extends ArrayAdapter {

    //to reference the Activity
    private Activity context;

    //to store the list of countries
    private ArrayList<Category> categories = new ArrayList<>();

    public CustomerOrder_CategoryAdapter(@NonNull Activity context, ArrayList<Category> items) {
        super(context, R.layout.category_section_listview);

        this.context = context;
        this.categories = items;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return categories.get(position);
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
        View rowView = inflater.inflate(R.layout.category_section_listview, null,true);

        // this code gets references to objects in the listview_row.xml file
        TextView itemNoView = rowView.findViewById(R.id.categoryNoTextViewID);
        TextView categoryNameView = rowView.findViewById(R.id.categoryNameTextViewID);
        TextView itemPriceView = rowView.findViewById(R.id.categoryPriceTextViewID);

        // this code sets the values of the objects to values from the arrays
        itemNoView.setText(String.valueOf(position + 1));
        categoryNameView.setText(categories.get(position).getName());
        itemPriceView.setText("₹ " + categories.get(position).getPrice());

        return rowView;
    }
}
