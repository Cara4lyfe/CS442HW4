package com.example.chandler.cs442hw4;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";
    private static final String marketwatchURL = "http://www.marketwatch.com/investing/stock/";

    private List<Stock> stockList = new ArrayList<>();

    private SwipeRefreshLayout swiper;
    private RecyclerView recyclerView;

    private ViewAdapter viewAdapter;

    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_main);

        viewAdapter = new ViewAdapter(stockList, this);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setAdapter(viewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Refresh();
            }
        });

        databaseHandler = new DatabaseHandler(this);
        databaseHandler.dumpDbToLog();

        if (hasConnection()) {
            ArrayList<String[]> list = databaseHandler.loadStocks();
            stockList.clear();
            for (String[] strings : list) {
                new getData(this).execute(strings[0], strings[1]);
            }
            Collections.sort(stockList);
            viewAdapter.notifyDataSetChanged();
        } else {
            showNoNetworkWarning();
        }
    }

    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Stock s = stockList.get(pos);

        String url = marketwatchURL + s.getSymbol();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public boolean onLongClick(final View view) {
        final int pos = recyclerView.getChildLayoutPosition(view);
        final Stock s = stockList.get(pos);

        new AlertDialog.Builder(this)
                .setTitle("Delete Stock")
                .setMessage("Delete Stock Symbol " + s.getSymbol() + "?")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(view.getContext(), "Stock Symbol " + s.getSymbol() + " deleted", Toast.LENGTH_SHORT).show();
                        databaseHandler.deleteStock(s.getSymbol());
                        stockList.remove(pos);
                        viewAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();

        return false;
    }
    public void getSearchResult(String symbol, String companyName) {
//        Toast.makeText(this, String.format("%s: %s", symbol, companyName), Toast.LENGTH_LONG).show();
        new getData(this).execute(symbol, companyName);
    }
    public void addStock(Stock stock) {
        Log.d(TAG, "addNewStock: " + stock);

        // Duplicate stock
        if (hasDuplicate(stock.getSymbol())) {
            new AlertDialog.Builder(this)
                    .setTitle("Duplicate Stock")
                    .setMessage("Stock symbol " + stock.getSymbol() + " is already displayed.")
                    .create()
                    .show();
            return;
        }

        stockList.add(stock);
        Collections.sort(stockList);
        if (!databaseHandler.existsStock(stock.getSymbol()))
            databaseHandler.addStock(stock);
        viewAdapter.notifyDataSetChanged();
    }

    private boolean hasDuplicate(String symbol) {
        for (Stock stock : stockList) {
            if (stock.getSymbol().equals(symbol))
                return true;
        }
        return false;
    }
    private boolean hasConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    private void showNoNetworkWarning() {
        new AlertDialog.Builder(this)
                .setTitle("No Network Connection")
                .setMessage("Stocks cannot be added without a network connection.")
                .create()
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                if (!hasConnection()) {
                    showNoNetworkWarning();
                    return true;
                }

                final EditText et = new EditText(this);
                et.setInputType(InputType.TYPE_CLASS_TEXT);
                et.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                et.setGravity(Gravity.CENTER_HORIZONTAL);

                new AlertDialog.Builder(this)
                        .setTitle("Stock Selection")
                        .setMessage("Please enter a Stock Symbol:")
                        .setView(et)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new Search(MainActivity.this).execute(et.getText().toString());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .create()
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        databaseHandler.shutDown();
        super.onDestroy();
    }

    private void Refresh() {
        if (hasConnection()) {
            ArrayList<String[]> list = databaseHandler.loadStocks();
            stockList.clear();
            for (String[] strings : list) {
                new getData(this).execute(strings[0], strings[1]);
            }
            Collections.sort(stockList);
            viewAdapter.notifyDataSetChanged();
        } else {
            showNoNetworkWarning();
        }
        swiper.setRefreshing(false);
    }
}
