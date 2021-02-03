package com.project.ams.automatedmess;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerOrder_ItemSelectionFragment extends Fragment {

    // Used to store values we got from the CustomerHomeFragment
    private String brandName;
    private String address;
    private String phoneNo;
    private String mobileNo;
    private String uID;

    // A few view refs
    private Button addItemsBtn;

    private String selectedFoodType;
    private String categoryName;

    private ArrayList<String> itemStrs;
    private ArrayList<MenuItem> menuItems = new ArrayList<>();
    private ListView lv;

    private OrderItemsViewModel mOrderItemsViewModel;
    private MessProviderViewModel mMessProviderViewModel;

    // Used to check whether messProvider info is present in the table
    private boolean messProviderInfoPresentinDb;


    private static final String TAG = "CustomerOrder_ItemSelec";

    public CustomerOrder_ItemSelectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        categoryName = getArguments().getString("categoryName");

        selectedFoodType = getArguments().getString("foodType");

        itemStrs = getArguments().getStringArrayList("menuItemsStr");

        // Get the arguments passed to this fragment
        brandName = getArguments().getString("brandName");
        address = getArguments().getString("address");
        phoneNo = getArguments().getString("phoneNo");
        mobileNo = getArguments().getString("mobileNo");
        uID = getArguments().getString("uID");

        // Populate menuItems
        menuItemsToStrings();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_order__item_selection, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Start coding from here...

        mOrderItemsViewModel = ViewModelProviders.of(getActivity()).get(OrderItemsViewModel.class);
        mMessProviderViewModel = ViewModelProviders.of(getActivity()).get(MessProviderViewModel.class);

        ((CustomerHome) getActivity()).setAppBarTitle("Customer OrderItem - Item Section");

        lv = view.findViewById(R.id.customerOrderMenuList);

        CustomerOrder_ItemSelectionAdapter adapter = new CustomerOrder_ItemSelectionAdapter(getActivity(), menuItems);
        lv.setAdapter(adapter);


        addItemsBtn = view.findViewById(R.id.customerOrderAddItemsBtn);

        mOrderItemsViewModel.getAllItems().observe(this, new Observer<List<OrderItem>>() {
            @Override
            public void onChanged(@Nullable final List<OrderItem> items) {
                ((TextView) getActivity().findViewById(R.id.badge_notification)).setText(String.valueOf(items.size()));
            }
        });

        mMessProviderViewModel.getAllItems().observe(this, new Observer<List<MessProviderProfile>>() {
            @Override
            public void onChanged(@Nullable final List<MessProviderProfile> items) {
                messProviderInfoPresentinDb = items.size() != 0 ? true : false;
            }
        });

        addItemsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int in = 0;

                for (MenuItem item : menuItems) {
                    int numberPickerVal = ShareNumberPickersValues.getInstance().numberPickerVals.get(in);
                    if (numberPickerVal != 0) {
                        OrderItem orderItem = new OrderItem();
                        orderItem.setItemName(menuItems.get(in).getItemName());
                        orderItem.setItemPrice(menuItems.get(in).getItemPrice());
                        orderItem.setItemQuantity(numberPickerVal);
                        orderItem.setItemType(selectedFoodType);
                        orderItem.setItemCategory(categoryName);

                        // Save these values in the users-orderItem database in the orderItem table
                        mOrderItemsViewModel.insert(orderItem);
                    }
                    in++;

                }

                // Also add the mess provider details for this particular order
                // in the database if it isn't present
                if (!messProviderInfoPresentinDb) {
                    MessProviderProfile messProvider = new MessProviderProfile();
                    messProvider.setUid(uID);
                    messProvider.setBrandName(brandName);
                    messProvider.setAddress(address);
                    messProvider.setMobileNo(mobileNo);
                    messProvider.setPhoneNo(phoneNo);

                    mMessProviderViewModel.insert(messProvider);
                }


                getFragmentManager().popBackStack();
            }
        });

    }

    public void menuItemsToStrings() {
        for (String itemStr : itemStrs) {
            // Extract itemNo, itemName and itemPrice for each itemStr
            String itemNo = itemStr.substring(0, itemStr.indexOf(":"));
            String itemName = itemStr.substring(itemStr.indexOf(":") + 1, itemStr.indexOf("-")).trim();
            String itemPrice = itemStr.substring(itemStr.indexOf("-") + 1, itemStr.length()).trim();

            // Create a new instance of MenuItem
            MenuItem menuItem = new MenuItem();
            menuItem.setItemNo(itemNo);
            menuItem.setItemName(itemName);
            menuItem.setItemPrice(Double.parseDouble(itemPrice));

            // Add the MenuItem to the menuItems array
            menuItems.add(menuItem);
        }
    }
}
