package com.autochip.golecha;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import app_utility.DataBaseHelper;
import app_utility.DatabaseHandler;
import app_utility.OnFragmentInteractionListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link app_utility.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyOrdersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyOrdersFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private MyOrdersRVAdapter myOrdersRVAdapter;

    private FloatingActionButton fabCreateOrder;

    ArrayList<DataBaseHelper> alDBData;
    DatabaseHandler dbh;
    RecyclerView recyclerView;
    public MyOrdersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyOrdersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyOrdersFragment newInstance(String param1, String param2) {
        MyOrdersFragment fragment = new MyOrdersFragment();
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
        dbh = new DatabaseHandler(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_my_orders, container, false);

        initViews(view);
        setUpViews();

        return view;
    }

    private void initViews(View view){
        recyclerView = view.findViewById(R.id.rv_my_orders);
        fabCreateOrder = view.findViewById(R.id.fab_create_order);
    }

    private void setUpViews(){
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);

        fabCreateOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeScreenActivity.onFragmentInteractionListener.onFragmentMessage("MY_ORDER_FAB_CLICK", 0, "", "");
            }
        });

        alDBData = new ArrayList<>(dbh.getProductsFromTempProducts());
        LinkedHashMap<Integer, HashMap<String, String>> lhmMyOrdersData = new LinkedHashMap<>();
        for (int i=0; i<alDBData.size(); i++){
            int nID = alDBData.get(i).get_id();
            int nNumberOfProducts = Arrays.asList(alDBData.get(i).get_product_id_string().split(",")).size();
            String sDate = alDBData.get(i).get_delivery_date();
            ArrayList<String> sSubTotal =  new ArrayList<>(Arrays.asList(alDBData.get(i).get_sub_total_string().split(",")));

            double dTotal = 0.00;
            for (int j=0; j<sSubTotal.size(); j++){
                dTotal = dTotal + Double.valueOf(sSubTotal.get(j));
            }
            String sOrderStatus = alDBData.get(i).get_order_status();
            HashMap<String, String> hmKeyValue = new HashMap<>();
            hmKeyValue.put("number_of_products", String.valueOf(nNumberOfProducts));
            hmKeyValue.put("date", sDate);
            hmKeyValue.put("total", String.valueOf(dTotal));
            hmKeyValue.put("status", sOrderStatus);
            lhmMyOrdersData.put(nID, hmKeyValue);
        }

        myOrdersRVAdapter = new MyOrdersRVAdapter(getActivity(), getActivity().getSupportFragmentManager(), mListener, lhmMyOrdersData);
        recyclerView.setAdapter(myOrdersRVAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
