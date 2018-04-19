package com.example.chandler.cs442hw4;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Chandler on 3/3/2018.
 */

public class Stock implements Serializable, Comparable<Stock>{

    private String symbol;
    private String companyname;
    private double price;
    private double change;
    private double percent;

    public Stock(String symbol, String companyName, double price, double change, double percent) {
        this.symbol = symbol;
        this.companyname = companyName;
        this.price = price;
        this.change = change;
        this.percent = percent;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyname;
    }

    public void setCompanyName(String companyName) {
        this.companyname = companyName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    @Override
    public String toString() {
        return symbol+companyname+price+change+percent;
    }

    @Override
    public int compareTo(@NonNull Stock stock) {
        return this.symbol.compareTo(stock.symbol);
    }
}
