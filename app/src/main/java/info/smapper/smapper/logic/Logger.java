package info.smapper.smapper.logic;

import android.app.Activity;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

import info.smapper.smapper.views.fragments.LogFragment;

public class Logger {
    private static ArrayList<Spanned> list = new ArrayList<>();
    private static Activity activity;
    private static LogFragment fragment;
    private static ArrayAdapter<Spanned> adapter;

    public static void initLogger(Activity a) {
        Logger.activity = a;
    }

    public static void setReferences(LogFragment f, ArrayAdapter<Spanned> a) {
        Logger.fragment = f;
        Logger.adapter = a;
    }

    public static void add(String entry) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        String currentTimestamp = simpleDateFormat.format(new Date());
        final String formattedEntry = "<font color='gray'>" + currentTimestamp + "</font><br>" + entry;
        final Spanned spanned = Html.fromHtml(formattedEntry);

        // Get a handler that can be used to post to the main thread
        Handler mainHandler = new Handler(activity.getBaseContext().getMainLooper());
        Runnable deferredWorker = new Runnable() {
            @Override
            public void run() {
                if (Logger.adapter != null) {
                    // Logger GUI up and running.
                    Logger.adapter.add(spanned);
                } else {
                    // Logger GUI still not initialized...
                    list.add(spanned);
                }
            }
        };
        mainHandler.post(deferredWorker);
    }

    public static ArrayList<Spanned> getList() {
        return list;
    }
}
