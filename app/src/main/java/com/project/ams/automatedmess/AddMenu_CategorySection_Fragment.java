package com.project.ams.automatedmess;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddMenu_CategorySection_Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddMenu_CategorySection_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddMenu_CategorySection_Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Declare a few fields for our views
    private EditText categoryNameView;
    private Spinner vegNonVegCategoryView;
    private Button proceedBtn;

    // For storing the category name and veg/non-veg values
    private String categoryName;
    private String vegNonVegCategory;

//    private OnFragmentInteractionListener mListener;

    public AddMenu_CategorySection_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddMenu_CategorySection_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddMenu_CategorySection_Fragment newInstance(String param1, String param2) {
        AddMenu_CategorySection_Fragment fragment = new AddMenu_CategorySection_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        ((MessProviderHome) getActivity()).setAppBarTitle("Add Menu - Category Section");


        View view = inflater.inflate(R.layout.fragment_add_menu__category_section_, container, false);

        // Create the connection's
        categoryNameView = view.findViewById(R.id.categoryName);
        vegNonVegCategoryView = view.findViewById(R.id.veg_nonveg_spinner);
        proceedBtn = view.findViewById(R.id.addMenuProceedBtn);

        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryName = categoryNameView.getText().toString();
                vegNonVegCategory = vegNonVegCategoryView.getSelectedItem().toString();

                if (categoryName.isEmpty()) {
                    Toast.makeText(getContext(), "Add a Category Name", Toast.LENGTH_SHORT).show();
                } else {

                    // We need to pass the Category Name, And Food Type
                    Bundle bundle = new Bundle();
                    bundle.putString("categoryName", categoryName);
                    bundle.putString("foodType", vegNonVegCategory);
                    //set Fragmentclass Arguments
                    AddMenu_AddItems_Fragment fragobj = new AddMenu_AddItems_Fragment();
                    fragobj.setArguments(bundle);

                    // Replace it with AddMenu_AddItems_fragment
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.messProviderFLayout, fragobj).addToBackStack(null);
                    fragmentTransaction.commit();
                }

            }
        });


        // Inflate the layout for this fragment
        return view;
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
