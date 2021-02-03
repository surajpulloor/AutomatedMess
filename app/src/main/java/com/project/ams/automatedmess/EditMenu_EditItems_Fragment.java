package com.project.ams.automatedmess;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
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
import java.util.List;

import static com.project.ams.automatedmess.AddMenu_AddItems_Fragment.dipToPixels;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditMenu_EditItems_Fragment extends Fragment {

    private Button menuSaveBtn;

    private int previousSerialNoId = R.id.serialNum3;
    private int previousItemNameId = R.id.itemName3;
    private int previousRupeeSymbolId = R.id.rupeeSymboltextView3;
    private int previousItemPriceId = R.id.itemPrice3;

    private ArrayList<TextView> serialNumbersRefs = new ArrayList<>(0);
    private ArrayList<EditText> itemNameRefs = new ArrayList<>(0);
    private ArrayList<EditText> itemPriceRefs = new ArrayList<>(0);


    // Declare a few firebase stuff
    private FirebaseAuth mAuth;
    private String uID;
    DatabaseReference dbRef;

    // Used for keeping track of the total no of items in the activity
    private long itemCount = 0;


    private static final String TAG = "AddMenuItems";


    // Get a ref. to the Relative Layout
    private RelativeLayout layout = null;

    // Other Info. about the Category Name, And Veg/Non-Veg Category
    private String categoryName = null;
    private String foodType = null;

    // Used for storing the items we get from the database
    List<MenuItem> menuItems = new ArrayList<>();


    public EditMenu_EditItems_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        categoryName = getArguments().getString("categoryName");
        foodType =  getArguments().getString("foodType");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_menu__edit_items, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ((MessProviderHome) getActivity()).setAppBarTitle("Edit Menu - Edit Item");

        // Start coding from here...

        layout = view.findViewById(R.id.editItemsLayout);


        // Go through the existing EditText views (both itemName and itemPrice) in the activity to get their ids and store them in an ArrayList
        serialNumbersRefs.add((TextView) view.findViewById(R.id.serialNum1));
        serialNumbersRefs.add((TextView) view.findViewById(R.id.serialNum2));
        serialNumbersRefs.add((TextView) view.findViewById(R.id.serialNum3));

        itemNameRefs.add((EditText) view.findViewById(R.id.itemName1));
        itemNameRefs.add((EditText) view.findViewById(R.id.itemName2));
        itemNameRefs.add((EditText) view.findViewById(R.id.itemName3));

        itemPriceRefs.add((EditText) view.findViewById(R.id.itemPrice1));
        itemPriceRefs.add((EditText) view.findViewById(R.id.itemPrice2));
        itemPriceRefs.add((EditText) view.findViewById(R.id.itemPrice3));

        menuSaveBtn = view.findViewById(R.id.menuSaveBtn);

        // Init Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getUid();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Menus").child(uID).child(categoryName).child(foodType);

        // Check whether or not you have any existing menus for the currently logged in mess user
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // We need to reset the item count to zero, else it will increment the previous itemCount value
                itemCount = 0;
                for (DataSnapshot items : snapshot.getChildren()) {
                    // Create a new MenuItem object
                    MenuItem mi = new MenuItem();

                    // Start init'ing it
                    mi.setItemNo(items.getKey());

                    // Declare an array for storing itemName and itemPrice (size 2)
                    String[] buffer = new String[2];

                    // Declare a counter as well
                    int i = 0;

                    // Go through the itemN node
                    // Its nothing but 2 more nodes in it
                    // i.e itemName and itemPrice
                    for (DataSnapshot item : items.getChildren()) {
                        buffer[i++] = item.getValue().toString();
                    }

                    // Set Item Name and ItemPrice
                    mi.setItemName(buffer[0]);
                    mi.setItemPrice(Double.parseDouble(buffer[1]));

                    menuItems.add(mi);

                    // Inc the total item count
                    itemCount++;
                }

                int count = 0;

                // Go through the menuItems List
                for (MenuItem item : menuItems) {

                    if (count < 3) {
                        serialNumbersRefs.get(count).setText(String.valueOf(count + 1));
                        itemNameRefs.get(count).setText(item.getItemName());
                        itemPriceRefs.get(count).setText(String.valueOf(item.getItemPrice()));
                        count++;
                    } else {
                        // Define 3 EditText for item name, 3 TextViews with rupee symbol, 3 EditText for price
                        TextView serialNumber = new TextView(getContext());
                        EditText itemName = new EditText(getContext());
                        TextView rupeeSymbol = new TextView(getContext());
                        EditText itemPrice = new EditText(getContext());

                        // Set a few properties for the serialNumber TextView
                        serialNumber.setId(View.generateViewId());
                        serialNumber.setText(String.valueOf(++count));
                        serialNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);


                        // Set a few properties for the itemName edittext
                        itemName.setId(View.generateViewId());
                        itemName.setText(item.getItemName());
                        itemName.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);


                        // Set a few properties for the rupee symbol textview
                        rupeeSymbol.setId(View.generateViewId());
                        rupeeSymbol.setText("₹");
                        rupeeSymbol.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

                        // Set a few properties for the itemName edittext
                        itemPrice.setId(View.generateViewId());
                        itemPrice.setText(String.valueOf(item.getItemPrice()));
                        itemPrice.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

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


                        // Finally add the newly created EditText(itemName, itemPrice) view refs
                        // in itemNameRefs, itemPriceRefs ArrayList's
                        serialNumbersRefs.add(serialNumber);
                        itemNameRefs.add(itemName);
                        itemPriceRefs.add(itemPrice);

                        // Add the views into the layout
                        layout.addView(serialNumber, serialNumberLp);
                        layout.addView(itemName, itemNameLp);
                        layout.addView(rupeeSymbol, rupeeSymbolLp);
                        layout.addView(itemPrice, itemPriceLp);
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        menuSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int currentItemNo = 0;
                int noOfItems = itemNameRefs.size();

                // Check whether we have values in all the EditText views(both itemName, itemPrice)
                for (; currentItemNo < noOfItems; currentItemNo++) {

                    String itemNameValue = itemNameRefs.get(currentItemNo).getText().toString().trim();
                    String itemPriceValue = itemPriceRefs.get(currentItemNo).getText().toString().trim();

                    if (itemNameValue.equals("")) {
                        Toast.makeText(getActivity(), ("Please enter the name for the item\n in Item Name " + String.valueOf(currentItemNo + 1)), Toast.LENGTH_SHORT).show();
                        itemNameRefs.get(currentItemNo).requestFocus();
                        break;
                    }

                    if (itemPriceValue.equals("")) {
                        Toast.makeText(getActivity(), ("Please enter a price for the item in Item Price " + String.valueOf(currentItemNo + 1)), Toast.LENGTH_SHORT).show();
                        itemPriceRefs.get(currentItemNo).requestFocus();
                        break;
                    }

                    // Update both itemName and itemPrice for the current index
                    // if there are not errors
                    menuItems.get(currentItemNo).setItemName(itemNameValue);
                    menuItems.get(currentItemNo).setItemPrice(Double.parseDouble(itemPriceValue));
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

                    for (MenuItem item : menuItems) {
                        // Store ItemName and ItemPrice as key value pair in the database
                        dbRef.child(item.getItemNo()).child("ItemName").setValue(item.getItemName());
                        dbRef.child(item.getItemNo()).child("ItemPrice").setValue(item.getItemPrice());
                    }

                    Toast.makeText(getActivity(), "Menu items Updated", Toast.LENGTH_SHORT).show();

                }



            }
        });
    }



}
