package com.sam_chordas.android.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;
import com.sam_chordas.android.stockhawk.ui.StockGraph;

/**
 * Created by user on 4/30/2016.
 */
public class StockWidgetProvider extends AppWidgetProvider {

    public static final String APP_ACTION = "APP_ACTION";
    public static final String DETAIL_ACTION = "DETAIL_ACTION";

    public static final String EXTRA_ITEM = "EXTRA_ITEM";


    @Override
    public void onReceive(Context context, Intent intent) {

        AppWidgetManager mgr = AppWidgetManager.getInstance(context);

        if (intent.getAction() == DETAIL_ACTION) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            Intent detailIntent = new Intent(context, StockGraph.class);

            detailIntent.putExtra(QuoteColumns.SYMBOL, intent.getStringExtra(QuoteColumns.SYMBOL));
            detailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(detailIntent);

        } else if (intent.getAction() == APP_ACTION) {
            Intent activityIntent = new Intent(context, MyStocksActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);
        }


        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {


        for (int appWidgetId : appWidgetIds) {

            Intent intent = new Intent(context, StockWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setRemoteAdapter(appWidgetId, R.id.rv_stock, intent);
            views.setEmptyView(R.id.rv_stock, R.id.empty_view);


            // For starting Stock Graph activity on clicking a list item
            Intent detailIntent = new Intent(context, StockWidgetProvider.class);
            detailIntent.setAction(DETAIL_ACTION);
            detailIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            detailIntent.setData(Uri.parse(detailIntent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent pendingClickIntent = PendingIntent.getBroadcast(context, 0, detailIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.rv_stock, pendingClickIntent);

            //for opening app on clicking widget header
            Intent appIntent = new Intent(context, StockWidgetProvider.class);
            appIntent.setAction(APP_ACTION);
            appIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            appIntent.setData(Uri.parse(appIntent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent pendingAppIntent = PendingIntent.getBroadcast(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_title, pendingAppIntent);



            appWidgetManager.updateAppWidget(appWidgetId, views);


        }


    }
}
