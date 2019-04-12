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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
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

class CreateOrderRVAdapter extends RecyclerView.Adapter<CreateOrderRVAdapter.ProductsHolder> implements OnFragmentInteractionListener,
        OnAsyncTaskInterface {

    public static OnFragmentInteractionListener mListener;
    public static OnAsyncTaskInterface onAsnycInterface;
    public Context context;
    private FragmentManager supportFragmentManager;
    private LinkedHashMap<Integer, HashMap<String, String>> lhmMyOrdersData;
    private ArrayList<Integer> alKeys;
    private ArrayList<String> alProducts = new ArrayList<>();

    private ArrayAdapter<String> adapter;

    private ArrayList<DataBaseHelper> alDBProductsData;

    private LinkedHashMap<Integer, ArrayList<String>> lhmSavedData = new LinkedHashMap<>();
    private float fQuantity;

    RecyclerView recyclerView;

    CreateOrderRVAdapter.ProductsHolder holder;

    String sDate, sStatus;

    private int count;

    private int nSizeOfData = 1;

    private ArrayList<TextView> alTextViews = new ArrayList<>();

    private ArrayList<Integer> alProductID = new ArrayList<>();

    private boolean isNotifyDataSetCalled = false;

    private DatabaseHandler dbh;

    private NetworkState networkState;

    private HashMap<String, Integer> hmSelectedProducts = new HashMap<>();

    /*CreateOrderRVAdapter(Context context, FragmentManager supportFragmentManager, OnFragmentInteractionListener mListener,
                         LinkedHashMap<Integer, HashMap<String, String>> lhmMyOrdersData) {
        this.context = context;
        this.supportFragmentManager = supportFragmentManager;
        this.mListener = mListener;
        this.lhmMyOrdersData = lhmMyOrdersData;
        alKeys = new ArrayList<>(lhmMyOrdersData.keySet());
    }*/

    CreateOrderRVAdapter(Context context, ArrayList<DataBaseHelper> alDBProductsData, RecyclerView recyclerView, DatabaseHandler dbh) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.alDBProductsData = alDBProductsData;
        this.dbh = dbh;
        alProducts.add("Select Product");
        for (int i = 0; i < alDBProductsData.size(); i++) {
            alProducts.add(alDBProductsData.get(i).get_product_name());
            alProductID.add(Integer.valueOf(alDBProductsData.get(i).get_product_id_string()));
        }
        mListener = this;
        onAsnycInterface = this;
        networkState = new NetworkState();
        //addDummyDataToList();
    }

    @Override
    public ProductsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_create_order, parent, false);

        return new CreateOrderRVAdapter.ProductsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CreateOrderRVAdapter.ProductsHolder holder, int position) {
        this.holder = holder;
        //if (position != 0)
        //if (!isNotifyDataSetCalled) {
        count++;
        String sSerialNumber = "0" + String.valueOf(position + 1);
        holder.tvSerialNo.setText(sSerialNumber);
        holder.tvSerialNo.setTag(count);
        //alTextViews.add(holder.tvSerialNo);
        //alTVTags.add(String.valueOf(count));
        /*} else {
            ArrayList<ArrayList<String>> alTmp = new ArrayList<>(lhmSavedData.values());
            String sSerialNumber = "0" + String.valueOf(position + 1);
            holder.tvSerialNo.setText(sSerialNumber);
            holder.tvSerialNo.setTag(alTmp.get(position).get(4));
        }*/
        //holder.tvUnitPrice.setText(alDBProductsData.get(position).get_unit_price_string());

        //if (!isNotifyDataSetCalled) {
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
                 if (!hmSelectedProducts.containsKey(sItem)) {
                     holder.tvUnitPrice.setText(alDBProductsData.get(position).get_unit_price_string());
                     hmSelectedProducts.put(sItem, Integer.valueOf(holder.tvSerialNo.getTag().toString()));
                 }
                else {
                     /*if (hmSelectedProducts.containsKey(sItem)) {
                         modifyListData(holder);
                     } else {*/
                     if(!sItem.equals("Select product")) {
                         Toast.makeText(context, "This product is already selected", Toast.LENGTH_SHORT).show();
                         holder.spinner.setSelection(0);
                     }
                     //}
                }
                    /*if (position!=0)
                        addProductToList(holder);
                    else
                        notifyItemRemoved(recyclerView.getAdapter().getItemCount()-1);*/

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProductFromList(holder);
            }
        });

        Objects.requireNonNull(holder.etQuantity.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

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

                /*if (holder.getAdapterPosition() != recyclerView.getAdapter().getItemCount()) {
                    modifyListData(holder);
                }*/
                /*if (!sQuantity.equals("")) {
                    fQuantity = Float.valueOf(sQuantity);
                    float dTmp = fQuantity * Float.valueOf(holder.tvUnitPrice.getText().toString().trim());
                    holder.tvSubTotal.setText(String.valueOf(dTmp));
                } else {
                    if()
                    holder.etQuantity.getEditText().setText("1.0");
                }*/
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //}
        /*final int key = alKeys.get(position);
        String sSizeOfProducts = String.valueOf(lhmMyOrdersData.get(key).get("number_of_products")) + " Products";
        holder.tvTotalProducts.setText(sSizeOfProducts);

        holder.tvDate.setText(lhmMyOrdersData.get(key).get("date"));
        holder.tvTotalAmount.setText(lhmMyOrdersData.get(key).get("total"));
        holder.tvOrderStatus.setText(lhmMyOrdersData.get(key).get("status"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment;
                FragmentTransaction transaction;
                String sBackStackParent;
                newFragment = ViewOrderFragment.newInstance(String.valueOf(key),"");
                sBackStackParent = newFragment.getClas  s().getName();
                transaction = supportFragmentManager.beginTransaction();
                transaction.replace(R.id.fl_container, newFragment, sBackStackParent);
                transaction.addToBackStack(null);
                transaction.commit();
                HomeScreenActivity.onFragmentInteractionListener.onFragmentMessage("MY_ORDER_ITEM_CLICK", 6);
            }
        });*/
        if (position == lhmSavedData.size()) {
            isNotifyDataSetCalled = false;
        }
    }

    private void increaseSize() {
        nSizeOfData = nSizeOfData + 1;
    }

    private void decreaseSize(CreateOrderRVAdapter.ProductsHolder holder) {
        nSizeOfData = nSizeOfData - 1;
        this.holder = holder;
        //new updateSerialNoAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "1");
        //int nTag = Integer.valueOf(holder.tvSerialNo.getTag().toString()) - 1;
        int nAdapterPosition = holder.getAdapterPosition();
        View view = recyclerView.getChildAt(nAdapterPosition);
        //Toast.makeText(context, "" + view.findViewById(R.id.tv_sl_no).getTag().toString(), Toast.LENGTH_SHORT).show();
        String sTag = holder.tvSerialNo.getTag().toString();
        //for (int i = 0; i < alTVTags.size(); i++) {
        //String sTagCompare = alTVTags.get(i);
        //if (sTagCompare.equals(String.valueOf(sTag))) {
        //alTextViews.remove(alTextViews.get(i));
        //lhmSavedData.remove(Integer.valueOf(sTag));

        hmSelectedProducts.remove(holder.spinner.getSelectedItem().toString());
        //break;
        //}
        //}
        notifyItemRemoved(nAdapterPosition);
    }

    private void modifyListData(CreateOrderRVAdapter.ProductsHolder holder) {
        ArrayList<String> alData = new ArrayList<>();

        int nProductID = alProducts.indexOf(holder.spinner.getSelectedItem().toString().trim()) - 1;

        alData.add(String.valueOf(alProductID.get(nProductID)));
        alData.add(holder.spinner.getSelectedItem().toString().trim());
        alData.add(holder.etQuantity.getEditText().getText().toString().trim());
        alData.add(holder.tvUnitPrice.getText().toString().trim());
        alData.add(holder.tvSubTotal.getText().toString().trim());
        alData.add(String.valueOf(holder.spinner.getSelectedItemPosition()));

        lhmSavedData.put(Integer.valueOf(holder.tvSerialNo.getTag().toString()), alData);
        hmSelectedProducts.put(alData.get(1), Integer.valueOf(holder.tvSerialNo.getTag().toString()));
    }

    private void addProductToList(CreateOrderRVAdapter.ProductsHolder holder) {

        if (recyclerView.getAdapter().getItemCount() == 0) {
            increaseSize();
            notifyItemInserted(recyclerView.getAdapter().getItemCount() + 1);
        } else if (holder.spinner.getSelectedItemPosition() != 0) {
            ArrayList<String> alData = new ArrayList<>();

            int nProductID = alProducts.indexOf(holder.spinner.getSelectedItem().toString().trim()) - 1;
            alData.add(String.valueOf(alProductID.get(nProductID)));
            alData.add(holder.spinner.getSelectedItem().toString().trim());
            alData.add(holder.etQuantity.getEditText().getText().toString().trim());
            alData.add(holder.tvUnitPrice.getText().toString().trim());
            alData.add(holder.tvSubTotal.getText().toString().trim());
            alData.add(String.valueOf(holder.spinner.getSelectedItemPosition()));

            lhmSavedData.put(Integer.valueOf(holder.tvSerialNo.getTag().toString()), alData);
            hmSelectedProducts.put(alData.get(1), Integer.valueOf(holder.tvSerialNo.getTag().toString()));
            if(sStatus.equals("")) {
                increaseSize();
                notifyItemInserted(recyclerView.getAdapter().getItemCount() + 1);
            }
        } else {
            Toast.makeText(context, "Please select product", Toast.LENGTH_SHORT).show();
        }
        //addDummyDataToList();
    }

    private void deleteProductFromList(CreateOrderRVAdapter.ProductsHolder holder) {
        //lhmSavedData.remove(Integer.valueOf(holder.tvSerialNo.getTag().toString()));
        decreaseSize(holder);
        //recyclerView.getAdapter().notifyItemRemoved(holder.getAdapterPosition());
    }

    private void addAllDataAtOnce(){
        for (int i=0; i<recyclerView.getAdapter().getItemCount(); i++){
            View view = recyclerView.getChildAt(i);

            ArrayList<String> alData = new ArrayList<>();

            Spinner spinner = view.findViewById(R.id.spinner_product);
            TextInputLayout etQuantity = view.findViewById(R.id.et_quantity);
            TextView tvUnitPrice = view.findViewById(R.id.tv_unit_price);
            TextView tvSubTotal = view.findViewById(R.id.tv_sub_total);
            TextView tvSerialNo = view.findViewById(R.id.tv_sl_no);
            int nProductID = alProducts.indexOf(spinner.getSelectedItem().toString().trim()) - 1;
            //int nProductID = alProducts.indexOf(holder.spinner.getSelectedItem().toString().trim()) - 1;
            alData.add(String.valueOf(alProductID.get(nProductID)));
            alData.add(spinner.getSelectedItem().toString().trim());
            alData.add(etQuantity.getEditText().getText().toString().trim());
            alData.add(tvUnitPrice.getText().toString().trim());
            alData.add(tvSubTotal.getText().toString().trim());
            //alData.add(String.valueOf(holder.spinner.getSelectedItemPosition()));

            lhmSavedData.put(Integer.valueOf(tvSerialNo.getTag().toString()), alData);
            //hmSelectedProducts.put(alData.get(1), Integer.valueOf(tvSerialNo.getTag().toString()));
        }
    }

    private void setSubTotal(final CreateOrderRVAdapter.ProductsHolder holder, String sQuantity) {
        fQuantity = Float.valueOf(sQuantity);
        float dTmp = fQuantity * Float.valueOf(holder.tvUnitPrice.getText().toString().trim());
        holder.tvSubTotal.setText(String.valueOf(dTmp));
    }

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
                //addProductToList(holder);
                increaseSize();
                notifyItemInserted(recyclerView.getAdapter().getItemCount() + 1);
                break;
            case "SAVE_BUTTON_CLICKED":
                this.sDate = sDate;
                this.sStatus = sStatus;
                if (holder.spinner.getSelectedItemPosition() != 0)
                    addAllDataAtOnce();
                    //addProductToList(holder);

                new updateSerialNoAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "2");
                break;
            case "PLACE_BUTTON_CLICKED":
                this.sDate = sDate;
                this.sStatus = sStatus;
                if (holder.spinner.getSelectedItemPosition() != 0)
                    addAllDataAtOnce();
                    //addProductToList(holder);
//networkState.isOnline() &&
                if (networkState.isNetworkAvailable(context)) {
                    GolechaAsyncTask golechaAsyncTask = new GolechaAsyncTask(context, onAsnycInterface, lhmSavedData);
                    golechaAsyncTask.execute(String.valueOf(2), "");
                    new updateSerialNoAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "2");
                } else {
                    new updateSerialNoAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "2");
                }
                break;
        }
    }

    @Override
    public void onAsyncTaskComplete(String sCase, int nFlag, LinkedHashMap<String, ArrayList<String>> lhmData, ArrayList<Integer> alImagePosition) {
        switch (sCase){
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

            /*tvTotalProducts = itemView.findViewById(R.id.tv_total_products);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTotalAmount = itemView.findViewById(R.id.tv_price);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);*/
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

            dbh.addDataToTempTable(new DataBaseHelper(sFinalProductID, sFinalProductName, sFinalProductQuantity, sFinalUnitPrice,
                    sFinalSubTotal, sDeliveryDate, sStatus));
        }

        @Override
        protected void onPostExecute(String result) {
            TextView tv;
            String sText;

            switch (type) {
                case 1:

                    //String stag = holder.tvSerialNo.getTag().toString();
                    int nTag = Integer.valueOf(holder.tvSerialNo.getTag().toString()) - 1;
                    int nAdapterPosition = holder.getAdapterPosition();
                    for (int i = 0; i < alTextViews.size(); i++) {
                        String sTag = alTextViews.get(i).getTag().toString();
                        if (sTag.equals(String.valueOf(nTag))) {
                            alTextViews.remove(alTextViews.get(i));
                            //lhmSavedData.remove(Integer.valueOf(sTag));
                            break;
                        }
                    }
                    //notifyItemRemoved(nAdapterPosition);

                    String sTmp;
                    for (int i = 0; i < alTextViews.size(); i++) {
                        int n = i + 1;
                        sTmp = "0" + n;
                        alTextViews.get(i).setText(sTmp);
                    }
                    lhmSavedData.size();
                    isNotifyDataSetCalled = true;
                    notifyDataSetChanged();
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
