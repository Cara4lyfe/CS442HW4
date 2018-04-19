package com.example.chandler.cs442hw4;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Chandler on 3/3/2018.
 */

public class ViewHolder extends RecyclerView.ViewHolder {

    public TextView Symbol;
    public TextView Price;
    public TextView Change;
    public TextView CompanyName;

    public ViewHolder(View view) {
        super(view);
        Symbol = view.findViewById(R.id.detailSymbol);
        Price = view.findViewById(R.id.detailPrice);
        Change = view.findViewById(R.id.detailChange);
        CompanyName = view.findViewById(R.id.detailCompanyName);
    }

}