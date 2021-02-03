package com.project.ams.automatedmess;


import android.content.Context;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddMenu_AddItems_Fragment extends Fragment {

    private Button menuSaveBtn;

    private FloatingActionButton addMenuBtn;

    private int previousSerialNoId = R.id.serialNum3;
    private int previousItemNameId = R.id.itemName3;
    private int previousRupeeSymbolId = R.id.rupeeSymboltextView3;
    private int previousItemPriceId = R.id.itemPrice3;

    private ArrayList<TextView> serialNumbers = new ArrayList<>(3);
    private ArrayList<EditText> itemNameRefs = new ArrayList<>(3);
    private ArrayList<EditText> itemPriceRefs = new ArrayList<>(3);


    // Declare a few firebase stuff
    private FirebaseAuth mAuth;
    private String uID;
    DatabaseReference dbRef;

    // Used for keeping track of the total no of items in the activity
    private long itemCount;

    // Use this to keep a count on the no of items currently in the database
    private long itemCountInDb;

    private static final String TAG = "AddMenuItems";


    // Get a ref. to the Relative Layout
    private RelativeLayout layout = null;

    // Other Info. about the Category Name, And Veg/Non-Veg Category
    private String categoryName = null;
    private String foodType = null;



    public AddMenu_AddItems_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Assign category name and food type which we got from the Category Section Fragment
        categoryName = getArguments().getString("categoryName");
        foodType = getArguments().getString("foodType");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_menu__add_items_, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MessProviderHome) getActivity()).setAppBarTitle("Add Menu - Add Item");

        // Start coding from here...

        layout = view.findViewById(R.id.addItemsLayout);

        // Go through the existing EditText views (both itemName and itemPrice) in the activity to get their ids and store them in an ArrayList
        serialNumbers.add((TextView) view.findViewById(R.id.serialNum1));
        serialNumbers.add((TextView) view.findViewById(R.id.serialNum2));
        serialNumbers.add((TextView) view.findViewById(R.id.serialNum3));

        itemNameRefs.add((EditText) view.findViewById(R.id.itemName1));
        itemNameRefs.add((EditText) view.findViewById(R.id.itemName2));
        itemNameRefs.add((EditText) view.findViewById(R.id.itemName3));

        itemPriceRefs.add((EditText) view.findViewById(R.id.itemPrice1));
        itemPriceRefs.add((EditText) view.findViewById(R.id.itemPrice2));
        itemPriceRefs.add((EditText) view.findViewById(R.id.itemPrice3));


        menuSaveBtn = view.findViewById(R.id.menuSaveBtn);
        addMenuBtn = view.findViewById(R.id.addMenuItemsBtn);

        // Init Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getUid();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Menus").child(uID).child(categoryName).child(foodType);

        // Check whether or not you have any existing menus for the currently logged in mess user
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                itemCount = itemCountInDb = snapshot.getChildrenCount();
                for (TextView srNo : serialNumbers) {
                    srNo.setText(String.valueOf(++itemCount));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Define 3 EditText for item name, 3 TextViews with rupee symbol, 3 EditText for price
                TextView serialNumber = new TextView(getContext());
                EditText itemName = new EditText(getContext());
                TextView rupeeSymbol = new TextView(getContext());
                EditText itemPrice = new EditText(getContext());

                // Set a few properties for the itemName edittext
                serialNumber.setId(View.generateViewId());
                serialNumber.setText(String.valueOf(++itemCount));
                serialNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);


                // Set a few properties for the itemName edittext
                itemName.setId(View.generateViewId());
                itemName.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);


                // Set a few properties for the rupee symbol textview
                rupeeSymbol.setId(View.generateViewId());
                rupeeSymbol.setText("₹");
                rupeeSymbol.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

                // Set a few properties for the itemName edittext
                itemPrice.setId(View.generateViewId());
                itemPrice.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);

//                RelativeLayout layout2 = new RelativeLayout(getContext());
//                long id = layout.getId();

                // Set the layout parameters for the above created views
                RelativeLayout.LayoutParams serialNumberLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams itemNameLp = new RelativeLayout.LayoutParams(dipToPixels(getContext(), 120), RelativeLayout.LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams rupeeSymbolLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams itemPriceLp = new RelativeLayout.LayoutParams(dipToPixels(getContext(), 120), RelativeLayout.LayoutParams.WRAP_CONTENT);

                // Add a few layout rules to all these views so that they will be properly positioned


                // Add rules to the layout params for itemName
                serialNumberLp.addRule(RelativeLayout.BELOW, previousSerialNoId);
                serialNumberLp.setMargins(0, dipToPixels(getContext(), 19), 0, 0);

                // Update previousItemNameId
                previousSerialNoId = serialNumber.getId();


                // Add rules to the layout params for itemName
                itemNameLp.addRule(RelativeLayout.ALIGN_START, previousItemNameId);
                itemNameLp.addRule(RelativeLayout.BELOW, previousItemNameId);
                itemNameLp.setMargins(0, dipToPixels(getContext(), 14), 0, 0);

                // Update previousItemNameId
                previousItemNameId = itemName.getId();

                // Add rules to the layout params for rupeeSymbol
                rupeeSymbolLp.addRule(RelativeLayout.ALIGN_TOP, previousItemNameId);
                rupeeSymbolLp.addRule(RelativeLayout.START_OF, previousItemPriceId);
                rupeeSymbolLp.setMargins(dipToPixels(getContext(), 22), 0, 0, 0);

//                rupeeSymbolLp.setMargins(0, dipToPixels(getContext(), 12), 0, 0);

                // Update previousItemNameId
                previousRupeeSymbolId = rupeeSymbol.getId();

                // Add rules to the layout params for itemPrice
                itemPriceLp.addRule(RelativeLayout.ALIGN_TOP, previousRupeeSymbolId);
                itemPriceLp.addRule(RelativeLayout.END_OF, previousRupeeSymbolId);
                itemPriceLp.addRule(RelativeLayout.RIGHT_OF, previousRupeeSymbolId);
                itemNameLp.addRule(RelativeLayout.BELOW, previousItemPriceId);

                // Update previousItemNameId
                previousItemPriceId = itemPrice.getId();

                // Shift the position of the menuSaveBtn
                // So we create a new RelativeLayout.LayoutParams object
                RelativeLayout.LayoutParams menuSaveBtnLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                menuSaveBtnLp.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
                menuSaveBtnLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                menuSaveBtnLp.addRule(RelativeLayout.BELOW, previousItemNameId);
                menuSaveBtnLp.setMargins(0, dipToPixels(getContext(), 13), 0, 0);
                // Update the layout params of the fab button
                menuSaveBtn.setLayoutParams(menuSaveBtnLp);

                // Shift the position of the fab
                // So we create a new RelativeLayout.LayoutParams object
                RelativeLayout.LayoutParams fabLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                fabLp.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
                fabLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                fabLp.addRule(RelativeLayout.BELOW, previousItemPriceId);
                fabLp.setMargins(0, dipToPixels(getContext(), 20), 0, 0);
                // Update the layout params of the fab button
                addMenuBtn.setLayoutParams(fabLp);


                // Finally add the newly created EditText(itemName, itemPrice) view refs
                // in itemNameRefs, itemPriceRefs ArrayList's
                serialNumbers.add(serialNumber);
                itemNameRefs.add(itemName);
                itemPriceRefs.add(itemPrice);

                // Add the views into the layout
                layout.addView(serialNumber, serialNumberLp);
                layout.addView(itemName, itemNameLp);
                layout.addView(rupeeSymbol, rupeeSymbolLp);
                layout.addView(itemPrice, itemPriceLp);
            }
        });

        menuSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int currentItemNo = 0;
                int noOfItems = itemNameRefs.size();
                int errorOnItemNo = (int) itemCountInDb;

                // Used to store all the itemName and itemPrice values as a Dictionary
                HashMap<String, String> itemValues = new HashMap<>();

                // Check whether we have values in all the EditText views(both itemName, itemPrice)
                for (; currentItemNo < noOfItems; currentItemNo++) {

                    String itemNameValue = itemNameRefs.get(currentItemNo).getText().toString().trim();
                    String itemPriceValue = itemPriceRefs.get(currentItemNo).getText().toString().trim();

                    if (itemNameValue.equals("")) {
                        Toast.makeText(getActivity(), ("Please enter the name for the item\n in Item Name " + String.valueOf(errorOnItemNo + 1)), Toast.LENGTH_SHORT).show();
                        itemNameRefs.get(currentItemNo).requestFocus();
                        break;
                    }

                    if (itemPriceValue.equals("")) {
                        Toast.makeText(getActivity(), ("Please enter a price for the item in Item Price " + String.valueOf(errorOnItemNo + 1)), Toast.LENGTH_SHORT).show();
                        itemPriceRefs.get(currentItemNo).requestFocus();
                        break;
                    }

                    // User entered something in both itemNameN and itemPriceN so update errorOnItemNo
                    errorOnItemNo++;

                    // Insert itemNameValue(key), itemPriceValue(value) in the itemValues HashMap
                    itemValues.put(itemNameValue, itemPriceValue);
                }

                // This checks indirectly that we have values in all the EditText views in this Activity
                if (currentItemNo == noOfItems) {

                    // We need to store the given menu items(name, price) in this way
                    // Menus: {
                    //  uId: {
                    //      item1: {
                    //         itemName: value,
                    //         itemPrice: value,
                    //      },
                    //      item2: {
                    //         itemName: value,
                    //         itemPrice: value,
                    //      },
                    //      itemN: {
                    //         itemName: value,
                    //         itemPrice: value,
                    //      }
                    //  }
                    // }


                    // Assign itemNo to 0 again to go through each item(the EditText's refs, both itemNames and itemsPrices)
                    currentItemNo = 0;

                    // Go through each and every item - itemName(key), itemPrice(value)
                    for (Map.Entry<String, String> item : itemValues.entrySet()) {
                        // Store ItemName and ItemPrice as key value pair in the database
                        dbRef.child("item" + String.valueOf(itemCountInDb + 1)).child("ItemName").setValue(item.getKey());
                        dbRef.child("item" + String.valueOf(itemCountInDb + 1)).child("ItemPrice").setValue(item.getValue());

                        // Clear all the EditText views in this activity
                        itemNameRefs.get((int) currentItemNo).setText("");
                        itemPriceRefs.get((int) currentItemNo).setText("");
                        currentItemNo++;
                        itemCountInDb++;
                    }

                    // Return focus to the first EditText view(itemName1) as an indication to add new items from the top
                    itemNameRefs.get(0).requestFocus();


                    Toast.makeText(getActivity(), "Menu items added", Toast.LENGTH_SHORT).show();

                }



            }
        });


    }

    public static int dipToPixels(Context context, int dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics));
    }

}
