package com.project.ams.automatedmess;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditMenu_CategorySection_Fragment extends Fragment {

    // Declare a few firebase stuff
    private FirebaseAuth mAuth;
    private String uID;
    DatabaseReference dbRef;

    // Used to store the Category Name and Food Type Values
    // They are stored as Category Name - Food Type
    private List<String> categoryInfo = new ArrayList<>();

    // Declare this to store the currently selected value from categoriesSpinner
    private String selectedCategoryInfo;

    // Declare a few refs
    private Spinner categoriesSpinner;

    private ArrayAdapter<String> dataAdapter;



    public EditMenu_CategorySection_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_menu__category_section, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MessProviderHome) getActivity()).setAppBarTitle("Edit Menu - Category Section");


        // Init Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getUid();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Menus").child(uID);

        categoriesSpinner = view.findViewById(R.id.categoriesSpinner);

        // Check whether or not you have any existing menus for the currently logged in mess user
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Store the cat and food type info
                for (DataSnapshot categoryNameDs : snapshot.getChildren()) {
                    for (DataSnapshot foodTypeDs : categoryNameDs.getChildren()) {
                        categoryInfo.add(categoryNameDs.getKey() + " - " + foodTypeDs.getKey());
                    }
                }

                // Check if the spinner is already populated if not then populate it with the
                // received data
                // Else notify the adapter to update the spinner
//                if (categoryInfo.size() == 0) {
                    dataAdapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_spinner_item, categoryInfo);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categoriesSpinner.setAdapter(dataAdapter);
//                } else {
//                    dataAdapter.notifyDataSetChanged();
//                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Button nxtBtn = view.findViewById(R.id.selectCatNxtBtn);
        nxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedCategoryInfo = categoriesSpinner.getSelectedItem().toString();
                String cat = selectedCategoryInfo.substring(0, selectedCategoryInfo.indexOf("-")).trim();
                String ft = selectedCategoryInfo.substring(selectedCategoryInfo.indexOf("-") + 1, selectedCategoryInfo.length()).trim();


                // We need to pass the Category Name, And Food Type
                Bundle bundle = new Bundle();
                bundle.putString("categoryName", cat);
                bundle.putString("foodType", ft);
                //set Fragmentclass Arguments
                EditMenu_EditItems_Fragment fragobj = new EditMenu_EditItems_Fragment();
                fragobj.setArguments(bundle);

                // Replace it with EditMenu_EditItems_Fragment
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.messProviderFLayout, fragobj).addToBackStack(null);
                fragmentTransaction.commit();

                Toast.makeText(getActivity(),
                        "Categories Spinner : " + categoriesSpinner.getSelectedItem().toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
