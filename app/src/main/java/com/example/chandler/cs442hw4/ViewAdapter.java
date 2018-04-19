package com.example.chandler.cs442hw4;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Locale;

/**
 * Created by Chandler on 3/3/2018.
 */

public class ViewAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "ViewAdapter";
    private List<Stock> stockList;
    private MainActivity mainAct;

    public ViewAdapter(List<Stock> stockList, MainActivity mainAct) {
        this.stockList = stockList;
        this.mainAct = mainAct;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_detail, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Stock stock = stockList.get(position);
        holder.Symbol.setText(stock.getSymbol());
        holder.CompanyName.setText(stock.getCompanyName());
        holder.Price.setText(String.format(Locale.US,"%.2f", stock.getPrice()));
        if (stock.getChange() > 0){
            holder.Change.setText(String.format(Locale.US,"\u25b2 %.2f (%.2f%%)",
                    stock.getChange(), stock.getPercent()*100));
            holder.Symbol.setTextColor(Color.GREEN);
            holder.Price.setTextColor(Color.GREEN);
            holder.Change.setTextColor(Color.GREEN);
            holder.CompanyName.setTextColor(Color.GREEN);
        } else {
            holder.Change.setText(String.format(Locale.US,"\u25bc %.2f (%.2f%%)",
                    stock.getChange(), stock.getPercent()*100));
            holder.Symbol.setTextColor(Color.RED);
            holder.Price.setTextColor(Color.RED);
            holder.Change.setTextColor(Color.RED);
            holder.CompanyName.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}

