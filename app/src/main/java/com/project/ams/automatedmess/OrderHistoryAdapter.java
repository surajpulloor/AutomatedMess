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

public class OrderHistoryAdapter extends ArrayAdapter {

    //to reference the Activity
    private Activity context;

    //to store the list of countries
    private ArrayList<OrderHistory> orders = new ArrayList<>();

    public OrderHistoryAdapter(@NonNull Activity context, ArrayList<OrderHistory> orders) {
        super(context, R.layout.layout);

        this.context = context;
        this.orders = orders;
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int position) {
        return orders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.layout, null,true);

        // this code gets references to objects in the listview_row.xml file
        TextView orderNoView = rowView.findViewById(R.id.orderList_orderNo);
        TextView orderTotalView = rowView.findViewById(R.id.orderList_orderTotal);
        ImageView statusImageView = rowView.findViewById(R.id.orderList_orderStatus);

        orderNoView.setText("Order No. " + orders.get(position).getOrderNo());
        orderTotalView.setText("₹ " + orders.get(position).getTotalAmount());

        if (orders.get(position).getOrderStatus().equals("pending")) {
            statusImageView.setImageResource(R.drawable.confirmation_pending);
        } else if (orders.get(position).getOrderStatus().equals("accepted")) {
            statusImageView.setImageResource(R.drawable.order_accepted);
        } else if (orders.get(position).getOrderStatus().equals("rejected")) {
            statusImageView.setImageResource(R.drawable.order_rejected);
        }

        return rowView;
    }
}