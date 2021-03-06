package com.autochip.golecha;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import app_utility.DataBaseHelper;
import app_utility.DatabaseHandler;
import app_utility.OnFragmentInteractionListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link app_utility.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ViewOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewOrderFragment extends Fragment implements OnFragmentInteractionListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    int nDBID;
    ArrayList<String> alProductID;
    ArrayList<String> alProductName;
    ArrayList<String> alProductQuantity;
    ArrayList<String> alUnitPrice;
    ArrayList<String> alSubTotal;
    String sDate;

    public static OnFragmentInteractionListener mListener;

    private LinearLayout llBottomSheet;

    BottomSheetBehavior sheetBehavior;

    TextView tvSwipe, tvDate , tvTotalAmount;

    private ViewOrderRVAdapter viewOrderFragment;
    private RecyclerView recyclerView;

    private DatabaseHandler dbh;
    private Button btnAddProduct, btnSaveOrder, btnPlaceOrder;
    ArrayList<DataBaseHelper> alDBData;
    private final Calendar myCalendar = Calendar.getInstance();

    public ViewOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewOrderFragment newInstance(String param1, String param2) {
        ViewOrderFragment fragment = new ViewOrderFragment();
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
        mListener = this;
        dbh = new DatabaseHandler(getActivity());

        alDBData = new ArrayList<>(dbh.getSingleProductByID(Integer.valueOf(mParam1)));
        /*alDBData = new ArrayList<>(dbh.getSingleProductByID(Integer.valueOf(mParam1)));
        nDBID = alDBData.get(0).get_id();
        alProductID = new ArrayList<>(Arrays.asList(alDBData.get(0).get_product_id_string().split(",")));
        alProductName = new ArrayList<>(Arrays.asList(alDBData.get(0).get_product_name().split(",")));
        alProductQuantity = new ArrayList<>(Arrays.asList(alDBData.get(0).get_product_quantity_string().split(",")));
        alUnitPrice = new ArrayList<>(Arrays.asList(alDBData.get(0).get_unit_price_string().split(",")));
        alSubTotal = new ArrayList<>(Arrays.asList(alDBData.get(0).get_sub_total_string().split(",")));
        sDate = alDBData.get(0).get_delivery_date();*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_order, container, false);
        initViews(view);
        setUpViews();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }, 1000);
        return view;
    }

    private void initViews(View view) {
        llBottomSheet = view.findViewById(R.id.bottom_sheet);
        tvSwipe = llBottomSheet.findViewById(R.id.tv_swipe);
        btnSaveOrder = llBottomSheet.findViewById(R.id.btn_save_order);
        btnPlaceOrder = llBottomSheet.findViewById(R.id.btn_place_order);
        tvTotalAmount = llBottomSheet.findViewById(R.id.tv_total_amount);

        sheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        recyclerView = view.findViewById(R.id.rv_create_order);
        btnAddProduct = view.findViewById(R.id.btn_add_product);
        tvDate = view.findViewById(R.id.tv_date);
    }

    private void setUpViews() {
        Date date = new Date();
        String modifiedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(date);
        tvDate.setText(modifiedDate);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);

        ArrayList<DataBaseHelper> alDBProductsData = new ArrayList<>(dbh.getProductsData());

        viewOrderFragment = new ViewOrderRVAdapter(getActivity(), recyclerView, alDBData, alDBProductsData);
        recyclerView.setAdapter(viewOrderFragment);

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewOrderRVAdapter.mListener.onFragmentMessage("ADD_BUTTON_CLICKED", 0, sDate, "ADD_PRODUCT");
            }
        });

        btnSaveOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewOrderRVAdapter.mListener.onFragmentMessage("SAVE_BUTTON_CLICKED", 1, sDate, "SAVE_ORDER");
            }
        });

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewOrderRVAdapter.mListener.onFragmentMessage("PLACE_BUTTON_CLICKED", 0, sDate, "PLACE_ORDER");
            }
        });

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "dd/MM/yyyy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        sDate = sdf.format(myCalendar.getTime());
                        tvDate.setText(sDate);
                    }

                };
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), date,
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });
        sDate = alDBData.get(0).get_delivery_date();
        tvDate.setText(sDate);
        ViewOrderRVAdapter.mListener.onFragmentMessage("CHANGED_DATE", 0, sDate, "NO_STATUS");
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

    @Override
    public void onFragmentMessage(String sCase, int nFlag, String sDate, String sStatus) {
        switch (sCase){
            case "UPDATE_DATE":
                tvDate.setText(sDate);
                break;
            case "UPDATE_TOTAL":
                tvTotalAmount.setText(sStatus);
                break;
        }
    }
}
