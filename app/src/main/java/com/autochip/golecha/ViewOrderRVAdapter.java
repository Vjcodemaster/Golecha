package com.autochip.golecha;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import app_utility.CircularProgressBar;
import app_utility.DataBaseHelper;
import app_utility.DatabaseHandler;
import app_utility.GolechaAsyncTask;
import app_utility.NetworkState;
import app_utility.OnAsyncTaskInterface;
import app_utility.OnFragmentInteractionListener;

/*
 * Created by Vj on 15-OCT-18.
 */

class ViewOrderRVAdapter extends RecyclerView.Adapter<ViewOrderRVAdapter.ProductsHolder> implements OnFragmentInteractionListener, OnAsyncTaskInterface {

    public static OnFragmentInteractionListener mListener;
    private Context context;
    LinkedHashMap<Integer, HashMap<String, String>> lhmMyOrdersData;
    ArrayList<Integer> alKeys;
    public static OnAsyncTaskInterface onAsnycInterface;
    int nDBID;
    ArrayList<String> alProductID;
    String sDate, sStatus;

    ArrayList<Integer> alProductIDInt = new ArrayList<>();

    ArrayList<String> alProductName;
    ArrayList<String> alProductQuantity;
    ArrayList<String> alUnitPrice;
    ArrayList<String> alSubTotal;
    ArrayList<DataBaseHelper> alDBData;
    ArrayList<DataBaseHelper> alDBProductsData;

    ViewOrderRVAdapter.ProductsHolder holder;
    private LinkedHashMap<Integer, ArrayList<String>> lhmSavedData = new LinkedHashMap<>();
    private DatabaseHandler dbh;

    private NetworkState networkState;

    private float fQuantity;

    private int count, nPreviousListSize;

    float fTotal;

    private int nSizeOfData;

    RecyclerView recyclerView;
    private ArrayAdapter<String> adapter;

    private ArrayList<String> alProducts = new ArrayList<>();

    private HashSet<String> hsSelectedProducts = new HashSet<>();
    private HashMap<Integer, String> hmSelectedProducts = new HashMap<>();

    private boolean isExecuted = false;

    ViewOrderRVAdapter(Context context, RecyclerView recyclerView, ArrayList<DataBaseHelper> alDBData,
                       ArrayList<DataBaseHelper> alDBProductsData) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.alDBData = alDBData;
        this.alDBProductsData = alDBProductsData;

        nDBID = alDBData.get(0).get_id();
        alProductID = new ArrayList<>(Arrays.asList(alDBData.get(0).get_product_id_string().split(",")));
        alProductName = new ArrayList<>(Arrays.asList(alDBData.get(0).get_product_name().split(",")));
        alProductQuantity = new ArrayList<>(Arrays.asList(alDBData.get(0).get_product_quantity_string().split(",")));
        alUnitPrice = new ArrayList<>(Arrays.asList(alDBData.get(0).get_unit_price_string().split(",")));
        alSubTotal = new ArrayList<>(Arrays.asList(alDBData.get(0).get_sub_total_string().split(",")));
        sDate = alDBData.get(0).get_delivery_date();

        alProducts.add("Select Product");
        for (int i = 0; i < alDBProductsData.size(); i++) {
            alProducts.add(alDBProductsData.get(i).get_product_name());
            alProductIDInt.add(Integer.valueOf(alDBProductsData.get(i).get_product_id_string()));
        }
        mListener = this;
        onAsnycInterface = this;
        networkState = new NetworkState();
        nSizeOfData = alProductName.size();
        dbh = new DatabaseHandler(context);

    }

    @Override
    public ProductsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_create_order, parent, false);

        return new ViewOrderRVAdapter.ProductsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewOrderRVAdapter.ProductsHolder holder, int position) {
        this.holder = holder;

        count++;
        String sSerialNumber = "0" + String.valueOf(position + 1);
        holder.tvSerialNo.setText(sSerialNumber);
        holder.tvSerialNo.setTag(count);

        holder.ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseSize(holder);
            }
        });

        adapter = new ArrayAdapter<String>(
                context, R.layout.spinner_row, R.id.tv_products, alProducts) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = view.findViewById(R.id.tv_products);
                //ImageView iv = view.findViewById(R.id.iv_product_image);
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                    /*if (alImagePosition.contains(position + 1)) {
                        ArrayList<String> alImageData = new ArrayList<>(lhmProductsData.get(tv.getText().toString()));
                        Bitmap bitmap = convertToBitmap(alImageData.get(2).substring(3));
                        iv.setImageBitmap(bitmap);
                    }*/

                    //if (bitmap != null) {
                }
                return view;
            }
        };
        holder.spinner.setAdapter(adapter);

        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sItem = holder.spinner.getSelectedItem().toString();
                ArrayList<String> alProductsList = new ArrayList<>(hmSelectedProducts.values());
                if (!alProductsList.contains(sItem) && holder.spinner.getSelectedItemPosition() != 0) {
                    holder.tvUnitPrice.setText(alDBProductsData.get(position).get_unit_price_string());
                    hmSelectedProducts.put(Integer.valueOf(holder.tvSerialNo.getTag().toString()), sItem);
                    setSubTotal(holder, holder.etQuantity.getEditText().getText().toString());
                } else if (holder.spinner.getSelectedItemPosition() != 0) {
                    if (alProductsList.contains(sItem)) {
                        Toast.makeText(context, "This product is already selected", Toast.LENGTH_SHORT).show();
                        int nTag = Integer.valueOf(holder.tvSerialNo.getTag().toString());
                        if (hmSelectedProducts.containsKey(nTag)) {
                            holder.spinner.setSelection(alProducts.indexOf(hmSelectedProducts.get(nTag)));
                        } else {
                            holder.spinner.setSelection(0);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Objects.requireNonNull(holder.etQuantity.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //fPreviousSubTotal = Float.valueOf(holder.tvSubTotal.getText().toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String sQuantity = holder.etQuantity.getEditText().getText().toString().trim();
                if (!sQuantity.equals("")) {
                    if (sQuantity.contains(".")) {
                        setSubTotal(holder, sQuantity);
                    } else {
                        String tmp = sQuantity + ".0";
                        holder.etQuantity.getEditText().setText(tmp);
                        setSubTotal(holder, sQuantity);
                    }
                } else {
                    sQuantity = "1.0";
                    holder.etQuantity.getEditText().setText(sQuantity);

                    setSubTotal(holder, sQuantity);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (position < alProductName.size() && !isExecuted) {
            int nIndexSpinner = alProducts.indexOf(alProductName.get(position));
            holder.spinner.setSelection(nIndexSpinner);

            holder.etQuantity.getEditText().setText(alProductQuantity.get(position));
            holder.tvSubTotal.setText(alSubTotal.get(position));
            holder.tvUnitPrice.setText(alUnitPrice.get(position));
            fTotal = fTotal + Float.valueOf(alSubTotal.get(position));
            ViewOrderFragment.mListener.onFragmentMessage("UPDATE_TOTAL", 0, sDate, String.valueOf(fTotal));
            if (position == alProductName.size() - 1) {
                isExecuted = true;
            }
        } else {
            holder.spinner.setSelection(0);
            holder.etQuantity.getEditText().setText("1.0");
            holder.tvSubTotal.setText("0.0");
            holder.tvUnitPrice.setText("0.0");
        }
    }

    private void increaseSize() {
        nSizeOfData = nSizeOfData + 1;
    }

    private void decreaseSize(ViewOrderRVAdapter.ProductsHolder holder) {
        nSizeOfData = nSizeOfData - 1;
        this.holder = holder;
        //new updateSerialNoAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "1");
        //int nTag = Integer.valueOf(holder.tvSerialNo.getTag().toString()) - 1;
        int nAdapterPosition = holder.getAdapterPosition();
        //View view = recyclerView.getChildAt(nAdapterPosition);
        //String sTag = holder.tvSerialNo.getTag().toString();

        // alProductName.remove(holder.spinner.getSelectedItem().toString());
        //String sSpinner = holder.spinner.getSelectedItem().toString();
        //hsSelectedProducts.remove(sSpinner);
        hmSelectedProducts.remove(Integer.valueOf(holder.tvSerialNo.getTag().toString()));
        fTotal = fTotal - Float.valueOf(holder.tvSubTotal.getText().toString());
        ViewOrderFragment.mListener.onFragmentMessage("UPDATE_TOTAL", 0, sDate, String.valueOf(fTotal));
        notifyItemRemoved(nAdapterPosition);
    }

    private void addAllDataAtOnce() {
        for (int i = 0; i < recyclerView.getAdapter().getItemCount(); i++) {
            View view = recyclerView.getChildAt(i);

            ArrayList<String> alData = new ArrayList<>();

            Spinner spinner = view.findViewById(R.id.spinner_product);
            TextInputLayout etQuantity = view.findViewById(R.id.et_quantity);
            TextView tvUnitPrice = view.findViewById(R.id.tv_unit_price);
            TextView tvSubTotal = view.findViewById(R.id.tv_sub_total);
            TextView tvSerialNo = view.findViewById(R.id.tv_sl_no);
            int nProductID = alProducts.indexOf(spinner.getSelectedItem().toString().trim()) - 1;
            //int nProductID = alProducts.indexOf(holder.spinner.getSelectedItem().toString().trim()) - 1;
            alData.add(String.valueOf(alProductIDInt.get(nProductID)));
            alData.add(spinner.getSelectedItem().toString().trim());
            alData.add(etQuantity.getEditText().getText().toString().trim());
            alData.add(tvUnitPrice.getText().toString().trim());
            alData.add(tvSubTotal.getText().toString().trim());
            //alData.add(String.valueOf(holder.spinner.getSelectedItemPosition()));

            lhmSavedData.put(Integer.valueOf(tvSerialNo.getTag().toString()), alData);
            //hmSelectedProducts.put(alData.get(1), Integer.valueOf(tvSerialNo.getTag().toString()));
        }
    }

    private void setSubTotal(final ViewOrderRVAdapter.ProductsHolder holder, String sQuantity) {
        float fOldSubTotal = Float.valueOf(holder.tvSubTotal.getText().toString());
        fQuantity = Float.valueOf(sQuantity);
        float dTmp = fQuantity * Float.valueOf(holder.tvUnitPrice.getText().toString().trim());
        holder.tvSubTotal.setText(String.valueOf(dTmp));

        if (isExecuted)
            updateTotal(dTmp, fOldSubTotal);
        /*if(holder.getAdapterPosition()>alProductName.size())
        updateTotal(dTmp);*/
        //fTotal = fTotal + dTmp;
        //ViewOrderFragment.mListener.onFragmentMessage("UPDATE_TOTAL", 0, sDate, String.valueOf(fTotal));
        //ViewOrderFragment.mListener.onFragmentMessage("UPDATE_TOTAL", 0, sDate, String.valueOf(dTmp));
    }

    private void updateTotal(float fNewSubTotal, Float fOldSubTotal) {
        float fLatestTotal = fOldSubTotal - fNewSubTotal;
        //if (fLatestTotal<0){
        fTotal = fTotal - fLatestTotal;
        /*} else {
            fTotal = fTotal + fLatestTotal;
        }*/
        if (hmSelectedProducts.size() == 0)
            fTotal = 0;
        ViewOrderFragment.mListener.onFragmentMessage("UPDATE_TOTAL", 0, sDate, String.valueOf(fTotal));
    }

    /*private void updateTotal(float fNewSubTotal){
        if(fPreviousSubTotal > fNewSubTotal){
            float latest = fPreviousSubTotal - fNewSubTotal;
            fTotal = fTotal - latest;
        } else {
            float latest = fNewSubTotal - fPreviousSubTotal;
            fTotal = fTotal + latest;
        }
        ViewOrderFragment.mListener.onFragmentMessage("UPDATE_TOTAL", 0, sDate, String.valueOf(fTotal));
    }*/

    @Override
    public int getItemCount() {
        return nSizeOfData;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onFragmentMessage(String sCase, int nFlag, String sDate, String sStatus) {
        switch (sCase) {
            case "ADD_BUTTON_CLICKED":
                if (nSizeOfData == hmSelectedProducts.size())
                    increaseSize();
                /*else if(hmSelectedProducts.size()>nSizeOfData){
                    View view;
                    hmSelectedProducts.clear();
                    for (int i=0; i<recyclerView.getAdapter().getItemCount() - 1; i++) {
                        view = recyclerView.getChildAt(i);
                        Spinner spinner = view.findViewById(R.id.spinner_product);
                        hmSelectedProducts.put(spinner.getSelectedItem(), )
                    }
                }*/
                /*if(nSizeOfData<hmSelectedProducts.size()){
                    increaseSize();
                }*/
                else {
                    Toast.makeText(context, "Select product before adding another item", Toast.LENGTH_SHORT).show();
                }
               /* View view;
                //addProductToList(holder);
                view = recyclerView.getChildAt(recyclerView.getAdapter().getItemCount() - 1);
                if (view != null) {
                    Spinner spinner = view.findViewById(R.id.spinner_product);
                    if (spinner.getSelectedItemPosition() == 0)
                        Toast.makeText(context, "Select product before adding another item", Toast.LENGTH_SHORT).show();
                    else {
                        increaseSize();
                    }
                }


                if (view == null)
                    Toast.makeText(context, "Select product before adding another item", Toast.LENGTH_SHORT).show();
                else {
                    Spinner spinner = view.findViewById(R.id.spinner_product);
                    if (spinner.getSelectedItemPosition() == 0)
                        Toast.makeText(context, "Select product before adding another item", Toast.LENGTH_SHORT).show();
                    else {
                        increaseSize();
                    }
                }*/
               /* if(view!=null) {
                    Spinner spinner = view.findViewById(R.id.spinner_product);
                    if (spinner.getSelectedItemPosition() != 0)
                        increaseSize();
                    else{
                        Toast.makeText(context, "Select product before adding another item", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    view = recyclerView.getChildAt(recyclerView.getAdapter().getItemCount()-2);
                }*/

                notifyItemInserted(recyclerView.getAdapter().getItemCount() + 1);

                break;
            case "SAVE_BUTTON_CLICKED":
                this.sDate = sDate;
                this.sStatus = sStatus;
                if (holder.spinner.getSelectedItemPosition() != 0) {
                    addAllDataAtOnce();
                    new ViewOrderRVAdapter.updateSerialNoAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "2");
                } else
                    Toast.makeText(context, "Select Product or delete the product item", Toast.LENGTH_SHORT).show();
                //addProductToList(holder);


                break;
            case "PLACE_BUTTON_CLICKED":
                this.sDate = sDate;
                this.sStatus = sStatus;
                if (holder.spinner.getSelectedItemPosition() != 0) {
                    addAllDataAtOnce();
                    if (networkState.isNetworkAvailable(context)) {
                        GolechaAsyncTask golechaAsyncTask = new GolechaAsyncTask(context, onAsnycInterface, lhmSavedData);
                        golechaAsyncTask.execute(String.valueOf(2), "");
                        new ViewOrderRVAdapter.updateSerialNoAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "2");
                    } else {
                        new ViewOrderRVAdapter.updateSerialNoAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "2");
                    }
                } else
                    Toast.makeText(context, "Select Product or delete the product item", Toast.LENGTH_SHORT).show();
                //addProductToList(holder);
//networkState.isOnline() &&
                /*if (networkState.isNetworkAvailable(context)) {
                    GolechaAsyncTask golechaAsyncTask = new GolechaAsyncTask(context, onAsnycInterface, lhmSavedData);
                    golechaAsyncTask.execute(String.valueOf(2), "");
                    new ViewOrderRVAdapter.updateSerialNoAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "2");
                } else {
                    new ViewOrderRVAdapter.updateSerialNoAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "2");
                }*/
                break;
        }
    }

    @Override
    public void onAsyncTaskComplete(String sCase, int nFlag, LinkedHashMap<String, ArrayList<String>> lhmData, ArrayList<Integer> alImagePosition) {
        switch (sCase) {
            case "PRODUCTS_CREATED":
                sStatus = "";
                break;
        }
    }

    static class ProductsHolder extends RecyclerView.ViewHolder {
        TextView tvSerialNo;
        TextView tvUnitPrice;
        TextView tvSubTotal;
        TextInputLayout etQuantity;
        Spinner spinner;
        ImageButton ibClose;

        ProductsHolder(View itemView) {
            super(itemView);
            tvSerialNo = itemView.findViewById(R.id.tv_sl_no);
            tvUnitPrice = itemView.findViewById(R.id.tv_unit_price);
            tvSubTotal = itemView.findViewById(R.id.tv_sub_total);
            etQuantity = itemView.findViewById(R.id.et_quantity);
            spinner = itemView.findViewById(R.id.spinner_product);
            ibClose = itemView.findViewById(R.id.ib_close);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class updateSerialNoAsync extends AsyncTask<String, String, String> {
        int type;
        private CircularProgressBar circularProgressBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setProgressBar();
        }

        @Override
        protected String doInBackground(String... params) {
            type = Integer.parseInt(params[0]);
            switch (type) {
                case 1:
                    //loginTask();
                    break;
                case 2:
                    //setProgressBar();
                    saveDataToTempDB();
                    //createOrder();
                    //updateTask();
                    break;
            }
            return null;
        }

        private void saveDataToTempDB() {
            ArrayList<Integer> alKeySet = new ArrayList<>(lhmSavedData.keySet());
            ArrayList<String> alData;
            ArrayList<String> alProductID = new ArrayList<>();
            ArrayList<String> alProductName = new ArrayList<>();
            ArrayList<String> alProductQuantity = new ArrayList<>();
            ArrayList<String> alUnitPrice = new ArrayList<>();
            ArrayList<String> alSubTotal = new ArrayList<>();
            for (int i = 0; i < lhmSavedData.size(); i++) {
                alData = new ArrayList<>(lhmSavedData.get(alKeySet.get(i)));
                alProductID.add(alData.get(0));
                alProductName.add(alData.get(1));
                alProductQuantity.add(alData.get(2));
                alUnitPrice.add(alData.get(3));
                alSubTotal.add(alData.get(4));
                //String productID = Integer.valueOf(alData.get(0));
            }
            String sFinalProductID = android.text.TextUtils.join(",", alProductID);
            String sFinalProductName = android.text.TextUtils.join(",", alProductName);
            String sFinalProductQuantity = android.text.TextUtils.join(",", alProductQuantity);
            String sFinalUnitPrice = android.text.TextUtils.join(",", alUnitPrice);
            String sFinalSubTotal = android.text.TextUtils.join(",", alSubTotal);
            String sDeliveryDate = sDate;

            dbh.updateSpecificOrderDataByID(new DataBaseHelper(sFinalProductID, sFinalProductName, sFinalProductQuantity, sFinalUnitPrice,
                    sFinalSubTotal, sDeliveryDate, sStatus), nDBID);
        }

        @Override
        protected void onPostExecute(String result) {
            TextView tv;
            String sText;

            switch (type) {
                case 1:
                    break;
                case 2:
                    break;
            }
            if (circularProgressBar != null && circularProgressBar.isShowing()) {
                circularProgressBar.dismiss();
            }
            /*for (int i = 0; i < alSlNoTextView.size(); i++) {
                tv = alSlNoTextView.get(i);
                sText = String.valueOf(i + 1);
                tv.setText(sText);
            }*/
        }

        private void setProgressBar() {
            circularProgressBar = new CircularProgressBar(context);
            circularProgressBar.setCanceledOnTouchOutside(false);
            circularProgressBar.setCancelable(false);
            circularProgressBar.show();
        }
    }
}
