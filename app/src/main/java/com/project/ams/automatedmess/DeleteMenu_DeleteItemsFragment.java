package com.project.ams.automatedmess;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class DeleteMenu_DeleteItemsFragment extends Fragment {

    // Declare a few firebase stuff
    private FirebaseAuth mAuth;
    private String uID;
    DatabaseReference dbRef;

    // Other Info. about the Category Name, And Veg/Non-Veg Category
    private String categoryName = null;
    private String foodType = null;

    // Used for storing the items we get from the database
    ArrayList<MenuItem> menuItems = new ArrayList<>();

    private View fragRef;

    // Used for ListView Generation
    private MenuItemsAdapter adapter;
    private ListView listView;

    // Used to keep track of the list item pressed
    private int listItemNo;

    public DeleteMenu_DeleteItemsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        categoryName = getArguments().getString("categoryName");
        foodType =  getArguments().getString("foodType");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_delete_menu__delete_items, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init fragRef to view
        fragRef = view;


        ((MessProviderHome) getActivity()).setAppBarTitle("Delete Menu - Delete Item");

        // Init Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getUid();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Menus").child(uID).child(categoryName).child(foodType);

        // Check whether or not you have any existing menus for the currently logged in mess user
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

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

                }

                adapter = new MenuItemsAdapter(getActivity(), menuItems);
                adapter.notifyDataSetChanged();

                listView = fragRef.findViewById(R.id.menuItemsList);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id) {

                        listItemNo = position;

                        Log.d(TAG, "onItemClick: position = " + position);


                        // Confirm the delete with this alert box
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Are you sure ?")
                                .setMessage("Do you really want to delete itemNo" + String.valueOf(position + 1) +
                                        " where, \n" +
                                        "Item Name is " + menuItems.get(position).getItemName() + "\n" +
                                        "and Item Price is ₹" + menuItems.get(position).getItemPrice()

                                )
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {

//                                        dbRef.addValueEventListener(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                                dataSnapshot.child(menuItems.get(listItemNo).getItemNo()).getRef().removeValue();
//                                                // Remove this item from the menuItems ArrayList
//                                                menuItems.remove(listItemNo);
//                                                // Notify the adapter to update the view
//                                                adapter.notifyDataSetChanged();
//                                                Toast.makeText(getActivity(), "Yaay", Toast.LENGTH_SHORT).show();
//                                            }
//
//                                            @Override
//                                            public void onCancelled(DatabaseError databaseError) {
//
//                                            }
//                                        });

                                        // Remove the item from the database
                                        dbRef.child(menuItems.get(listItemNo).getItemNo()).getRef().removeValue();

                                        // Remove this item from the menuItems ArrayList
                                        menuItems.remove(listItemNo);
                                        // Notify the adapter to update the view
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(getActivity(), "Item No." + String.valueOf(listItemNo + 1) + " is deleted.", Toast.LENGTH_SHORT).show();

                                        // Check if there are anymore items in the database
                                        // If true return to the previous fragment i.e DeleteMenu_categorySectionFragment
                                        if (menuItems.isEmpty()) {
                                            // Replace it with EditMenu_EditItems_Fragment
                                            FragmentManager fragmentManager = getFragmentManager();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.messProviderFLayout, new DeleteMenu_CategorySectionFragment()).addToBackStack(null);
                                            fragmentTransaction.commit();

                                            Toast.makeText(getActivity(), "No more items to delete!!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }})

                                .setNegativeButton(android.R.string.no, null).show();
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
