package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * Created by user on 4/28/2016.
 */
public class StockGraph extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    LineChartView chart;
    String symbol;
    LineSet dataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        chart = (LineChartView) findViewById(R.id.linechart);
        dataset = new LineSet();
        symbol = getIntent().getStringExtra(QuoteColumns.SYMBOL);

        if (getSupportActionBar() != null) getSupportActionBar().setTitle(symbol);

        getLoaderManager().initLoader(0, null, this);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this, QuoteProvider.Quotes.withSymbol(symbol),
                new String[]{QuoteColumns.BIDPRICE, QuoteColumns.CREATED}, null, null, QuoteColumns.CREATED);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || !data.moveToFirst()) {
            return;
        }

        float max = Float.MIN_VALUE;
        float min = Float.MAX_VALUE;
        while (data.moveToNext()) {
            float bidPrice = Float.parseFloat(data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE)));
            dataset.addPoint(Integer.toString(data.getPosition()), bidPrice);
            if (bidPrice > max) max = bidPrice;
            if (bidPrice < min) min = bidPrice;
        }
        chart.setAxisBorderValues((int) Math.max(0f, min - 5f), (int) max + 5);
        chart.setLabelsColor(Color.WHITE);

        dataset.setColor(ContextCompat.getColor(this, R.color.material_blue_700))
                .setDotsColor(Color.WHITE)
                .setThickness(4)
                .setDashed(new float[]{10f, 10f});

        chart.addData(dataset);
        chart.show();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
