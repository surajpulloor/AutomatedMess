package com.project.ams.automatedmess;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerOrder_CategorySectionFragment extends Fragment {

    // Used to store values we got from the CustomerHomeFragment
    private String brandName;
    private String address;
    private String phoneNo;
    private String mobileNo;
    private String uID;

    // Declare a few firebase stuff
    DatabaseReference dbRef;

    // Used to store two different set of categories namely VEG/NON-VEG
    private ArrayList<Category> vegCategoryInfo = new ArrayList<>();
    private ArrayList<Category> nonVegCategoryInfo = new ArrayList<>();

    // Declare this to store the currently selected value from categoriesSpinner
    private String selectedFoodType;

    // Declare a few refs
    private Spinner foodTypeSpinner;

    private CustomerOrder_CategoryAdapter dataAdapter;

    // Used to keep track of the no of categories
    private long noOfCategories;

    // Used for generating the list view based on the food type i.e veg/non-veg
    private HashMap<String, ArrayList<Category>> vegNonVegCategories = new HashMap<>();

    // ListView ref
    private ListView lv;

    // An array of categories
    private List<Category> categories = new ArrayList<>();

    private OrderItemsViewModel mOrderItemsViewModel;


    public CustomerOrder_CategorySectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get the arguments passed to this fragment
        brandName = getArguments().getString("brandName");
        address = getArguments().getString("address");
        phoneNo = getArguments().getString("phoneNo");
        mobileNo = getArguments().getString("mobileNo");
        uID = getArguments().getString("uID");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_order__category_section, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mOrderItemsViewModel = ViewModelProviders.of(getActivity()).get(OrderItemsViewModel.class);


        mOrderItemsViewModel.getAllItems().observe(this, new Observer<List<OrderItem>>() {
            @Override
            public void onChanged(@Nullable final List<OrderItem> items) {
                ((TextView) getActivity().findViewById(R.id.badge_notification)).setText(String.valueOf(items.size()));
            }
        });

        ((CustomerHome) getActivity()).setAppBarTitle("Customer OrderItem - Category Section");

        lv = view.findViewById(R.id.customerOrderCategoriesList);


        // Init Firebase Auth
        dbRef = FirebaseDatabase.getInstance().getReference().child("Menus").child(uID);

        foodTypeSpinner = view.findViewById(R.id.vegNonVegSpinner);


        // Store the selected food type by default i.e the first item/Veg
        selectedFoodType = foodTypeSpinner.getSelectedItem().toString();

        // Empty the categories array, so that we don't have the remains of the fragment when it was called previously
        vegCategoryInfo.clear();
        nonVegCategoryInfo.clear();


        // ---- ListView Code ---- Start ---- //
        // Set the adapter to the default category array i.e Veg
        dataAdapter = new CustomerOrder_CategoryAdapter(getActivity(), vegCategoryInfo);

        // Hook the adapter to the ListView
        lv.setAdapter(dataAdapter);
        // ---- ListView Code ---- End ---- //

        // Check whether or not you have any existing menus for the currently logged in mess user
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Store the cat and food type info
                for (DataSnapshot categoryNameDs : snapshot.getChildren()) {
                    for (DataSnapshot foodTypeDs : categoryNameDs.getChildren()) {

                        Category category = new Category();
                        category.setName(categoryNameDs.getKey());

                        // Used to store MenuItem's in the current category
                        ArrayList<MenuItem> items = new ArrayList<>();

                        // Go through the menuItems for the category
                        for (DataSnapshot menuItems : foodTypeDs.getChildren()) {
                            MenuItem mi = new MenuItem();
                            for (DataSnapshot item : menuItems.getChildren()) {
                                mi.setItemNo(menuItems.getKey());
                                switch (item.getKey()) {
                                    case "ItemName":
                                        mi.setItemName(item.getValue().toString());
                                        break;
                                    case "ItemPrice":
                                        mi.setItemPrice(Double.parseDouble(item.getValue().toString()));
                                        break;
                                }
                            }
                            items.add(mi);
                        }

                        // Once we have the items for the particular category
                        // pass it to the category instance
                        category.setMenuItems(items);

                        // We Also need to calculate the total cost of the category
                        category.calculatePriceFromMenuItems();

                        // Add the category to the categories array list
                        categories.add(category);

                        switch (foodTypeDs.getKey()) {
                            case "Veg":
                                // Add the category to the list
                                vegCategoryInfo.add(category);
                                vegNonVegCategories.put("Veg", vegCategoryInfo);
                                break;
                            case "Non-Veg":
                                nonVegCategoryInfo.add(category);
                                vegNonVegCategories.put("Non-Veg", nonVegCategoryInfo);
                                break;
                        }

                    }
                }

                dataAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        foodTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFoodType = adapterView.getSelectedItem().toString();
                switch (selectedFoodType) {
                    case "Veg":
                        dataAdapter = new CustomerOrder_CategoryAdapter(getActivity(), vegCategoryInfo);
                        break;
                    case "Non-Veg":
                        dataAdapter = new CustomerOrder_CategoryAdapter(getActivity(), nonVegCategoryInfo);
                        break;
                }

                lv.setAdapter(dataAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // First convert the menu items to an array of strings
                ArrayList<String> menuItemStrs = menuItemsToStrings(i);


                // Create a Bundle of mess values
                // i.e Brand Name, Address, MobileNo, Phone No.
                // We need to pass the Category Name, And Food Type to the next fragment
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("menuItemsStr", menuItemStrs);
                bundle.putString("foodType", selectedFoodType);
                bundle.putString("categoryName", vegNonVegCategories.get(selectedFoodType).get(i).getName());

                // Pass also the mess provider info.
                bundle.putString("uID",  uID);
                bundle.putString("brandName", brandName);
                bundle.putString("address", address);
                bundle.putString("phoneNo", phoneNo);
                bundle.putString("mobileNo", mobileNo);

                //set Fragmentclass Arguments
                CustomerOrder_ItemSelectionFragment fragobj = new CustomerOrder_ItemSelectionFragment();
                fragobj.setArguments(bundle);

                // Replace it with DeleteMenu_DeleteItems_Fragment
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.customerHomeFrame, fragobj).addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    public ArrayList<String> menuItemsToStrings(int categoryPosition) {
        ArrayList<String> itemsStr = new ArrayList<>();

        // Check which type/foodType of category it is
        switch (selectedFoodType) {
            case "Veg":
                // Once we have the menu items go through it
                for (MenuItem item : vegCategoryInfo.get(categoryPosition).getMenuItems()) {
                    itemsStr.add(item.toString());
                }
                break;
            case "Non-Veg":
                // Once we have the menu items go through it
                for (MenuItem item : nonVegCategoryInfo.get(categoryPosition).getMenuItems()) {
                    itemsStr.add(item.toString());
                }
                break;
        }

        return itemsStr;

    }
}
