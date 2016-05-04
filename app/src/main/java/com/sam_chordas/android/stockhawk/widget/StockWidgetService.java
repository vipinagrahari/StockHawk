package com.sam_chordas.android.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * Created by user on 4/30/2016.
 */
public class StockWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        Cursor data = getContentResolver().query(
                QuoteProvider.Quotes.CONTENT_URI,
                new String[]{
                        QuoteColumns._ID,
                        QuoteColumns.SYMBOL,
                        QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE,
                        QuoteColumns.CHANGE,
                        QuoteColumns.ISUP
                },
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);

        return new StockRemoteViewsFactory(this.getApplicationContext(), intent, data);
    }


    class StockRemoteViewsFactory implements RemoteViewsFactory {


        Context mContext;
        int mAppWidgetId;
        Cursor mCursor;

        private StockRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        public StockRemoteViewsFactory(Context context, Intent intent, Cursor cursor) {
            mCursor = cursor;
            new StockRemoteViewsFactory(context, intent);
        }

        @Override
        public void onCreate() {


        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return mCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            mCursor.moveToPosition(position);

            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.list_item_quote);
            remoteViews.setTextViewText(R.id.stock_symbol, mCursor.getString(mCursor.getColumnIndex(QuoteColumns.SYMBOL)));
            remoteViews.setTextViewText(R.id.bid_price, mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE)));
            remoteViews.setTextViewText(R.id.change, mCursor.getString(mCursor.getColumnIndex(QuoteColumns.CHANGE)));

            if (mCursor.getInt(mCursor.getColumnIndex("is_up")) == 1)
                remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
            else
                remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);


            Intent fillInIntent = new Intent();
            fillInIntent.putExtra(StockWidgetProvider.EXTRA_ITEM, position);
            fillInIntent.putExtra(QuoteColumns.SYMBOL, mCursor.getString(mCursor.getColumnIndex(QuoteColumns.SYMBOL)));
            remoteViews.setOnClickFillInIntent(R.id.list_item_quote, fillInIntent);

            return remoteViews;

        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

}
