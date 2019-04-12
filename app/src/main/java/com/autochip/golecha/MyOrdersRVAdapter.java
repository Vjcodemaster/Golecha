package com.autochip.golecha;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import app_utility.OnFragmentInteractionListener;

/*
 * Created by Vj on 15-OCT-18.
 */

class MyOrdersRVAdapter extends RecyclerView.Adapter<MyOrdersRVAdapter.ProductsHolder> {

    private OnFragmentInteractionListener mListener;
    private Context context;
    private FragmentManager supportFragmentManager;
    LinkedHashMap<Integer, HashMap<String, String>> lhmMyOrdersData;
    ArrayList<Integer> alKeys;

    MyOrdersRVAdapter(Context context, FragmentManager supportFragmentManager, OnFragmentInteractionListener mListener,
                      LinkedHashMap<Integer, HashMap<String, String>> lhmMyOrdersData) {
        this.context = context;
        this.supportFragmentManager = supportFragmentManager;
        this.mListener = mListener;
        this.lhmMyOrdersData = lhmMyOrdersData;
        alKeys = new ArrayList<>(lhmMyOrdersData.keySet());
    }

    @Override
    public ProductsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_my_orders, parent, false);

        return new MyOrdersRVAdapter.ProductsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrdersRVAdapter.ProductsHolder holder, int position) {
        final int key = alKeys.get(position);
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
                sBackStackParent = newFragment.getClass().getName();
                transaction = supportFragmentManager.beginTransaction();
                transaction.replace(R.id.fl_container, newFragment, sBackStackParent);
                transaction.addToBackStack(null);
                transaction.commit();
                HomeScreenActivity.onFragmentInteractionListener.onFragmentMessage("MY_ORDER_ITEM_CLICK", 6, "", "");
            }
        });
    }

    @Override
    public int getItemCount() {
        return alKeys.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ProductsHolder extends RecyclerView.ViewHolder{
        TextView tvTotalProducts;
        TextView tvDate;
        TextView tvTotalAmount;
        TextView tvOrderStatus;

        ProductsHolder(View itemView) {
            super(itemView);
            tvTotalProducts = itemView.findViewById(R.id.tv_total_products);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTotalAmount = itemView.findViewById(R.id.tv_price);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
        }
    }
}
