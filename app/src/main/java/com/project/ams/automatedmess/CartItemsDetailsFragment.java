package com.project.ams.automatedmess;


import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class CartItemsDetailsFragment extends Fragment {

    // Used to store the refs of the views of this fragment
    private TextView itemNameView;
    private TextView itemPriceView;
    private NumberPicker itemQuantityView;
    private TextView itemCategoryView;
    private ImageView itemTypeView;
    private Button deleteItemView;
    private Button saveView;

    // Used to store values we got from the CustomerHomeFragment
    private String itemId;
    private String itemName;
    private String itemPrice;
    private String itemQuantity;
    private String itemCategory;
    private String itemType;

    // Used for creating a OrderItem object which will be used to delete the item
    // or save the quantity to the database
    private OrderItem item = new OrderItem();

    // View Model refs
    private OrderItemsViewModel orderItemsViewModel;


    public CartItemsDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        itemId = getArguments().getString("itemId");
        itemName = getArguments().getString("itemName");
        itemPrice = getArguments().getString("itemPrice");
        itemQuantity = getArguments().getString("itemQuantity");
        itemCategory = getArguments().getString("itemCategory");
        itemType = getArguments().getString("itemType");

        // Assign these values to the OrderItem object
        item.setId(Integer.parseInt(itemId));
        item.setItemName(itemName);
        item.setItemPrice(Double.parseDouble(itemPrice));
        item.setItemQuantity(Integer.parseInt(itemQuantity));
        item.setItemCategory(itemCategory);
        item.setItemType(itemType);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart_items_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        orderItemsViewModel = ViewModelProviders.of(getActivity()).get(OrderItemsViewModel.class);

        // Assign ref's
        itemNameView = view.findViewById(R.id.cartItemDetails_itemName);
        itemPriceView = view.findViewById(R.id.cartItemDetails_itemPrice);
        itemQuantityView = view.findViewById(R.id.cartItemDetails_itemQuantity);
        itemCategoryView = view.findViewById(R.id.cartItemDetails_itemCategory);
        itemTypeView = view.findViewById(R.id.cartItemDetails_itemType);
        deleteItemView = view.findViewById(R.id.cartItemDetails_deleteItem);
        saveView = view.findViewById(R.id.cartItemDetails_save);

        // Assign the values we got from the cart fragment into these vies
        itemNameView.setText(itemName);
        itemPriceView.setText("₹ " + itemPrice);
        itemQuantityView.setMinValue(1);
        itemQuantityView.setMaxValue(10);
        itemQuantityView.setValue(Integer.parseInt(itemQuantity));
        itemCategoryView.setText(itemCategory);
        itemTypeView.setImageResource((itemType.equals("Veg") ? R.drawable.veg_symbol : R.drawable.nonveg_symbol));

        // Set click event listener's for the two buttons
        deleteItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Confirm the delete with this alert box
                new AlertDialog.Builder(getActivity())
                        .setTitle("Are you sure ?")
                        .setMessage("Do you really want to delete this item from the cart, \nyou'll have to add this item again, Are you sure?"

                        )
                        .setIcon(R.drawable.exclamation_mark)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                orderItemsViewModel.delete(item);
                                getFragmentManager().popBackStack();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        saveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.setItemQuantity(itemQuantityView.getValue());
                orderItemsViewModel.update(item);
                getFragmentManager().popBackStack();
            }
        });


    }
}
