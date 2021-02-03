package com.project.ams.automatedmess;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class OrderItemsAdapter extends ArrayAdapter {

    //to reference the Activity
    private Activity context;

    //to store the list of countries
    private ArrayList<OrderItem> orderItems;

    public OrderItemsAdapter(@NonNull Activity context, ArrayList<OrderItem> items) {
        super(context, R.layout.customer_cart_listview);

        this.context = context;
        this.orderItems = items;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return orderItems.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return orderItems.get(position);
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
        View rowView = inflater.inflate(R.layout.customer_cart_listview, null,true);

        // this code gets references to objects in the listview_row.xml file
        TextView itemNoView = rowView.findViewById(R.id.customerCart_listView_itemNo);
        TextView itemNameView = rowView.findViewById(R.id.customerCart_listView_itemName);
        TextView itemPriceView = rowView.findViewById(R.id.customerCart_listView_itemPrice);
        TextView itemQuantityView = rowView.findViewById(R.id.customerCart_listView_itemQuantity);
        TextView categoryNameView = rowView.findViewById(R.id.customerCart_listView_categoryType);
        ImageView foodTypeView = rowView.findViewById(R.id.customerCart_listView_foodType);

        // this code sets the values of the objects to values from the arrays
        itemNoView.setText(String.valueOf(position + 1));
        itemNameView.setText(orderItems.get(position).getItemName());
        itemPriceView.setText("₹ " + orderItems.get(position).getItemPrice());
        itemQuantityView.setText("1x" + orderItems.get(position).getItemQuantity());
        categoryNameView.setText(orderItems.get(position).getItemCategory());
        foodTypeView.setImageResource((orderItems.get(position).getItemType().equals("Veg") ? R.drawable.veg_symbol : R.drawable.nonveg_symbol));


        return rowView;
    }
}
