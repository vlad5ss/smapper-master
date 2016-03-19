package info.smapper.smapper.logic;

import android.app.Activity;
import android.location.Location;
import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import info.smapper.smapper.data.CellData;
import info.smapper.smapper.views.fragments.MapFragment;


public class BackgroundUploader {

    private static Activity activity;
    private static int updateInterval = 10000;
    private static Handler handler = new Handler();
    private static String remoteSite = "http://pagabo.dyndns.org:3001/api/signal";

    private static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            uploadData();
            handler.postDelayed(this, updateInterval);
        }
    };

    public static void initWorker(Activity a) {
        BackgroundUploader.activity = a;
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, updateInterval);
    }

    private static void uploadData() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                //Logger.add("BU: Triggered runnable.");
                Location loc = MapFragment.getCurrentLocation();
                //Logger.add("BU: got location! => " + loc);

                if (loc == null) {
                    return;
                }

                //Logger.add("BU: got location! current height: " + loc.getAltitude() + "m");
                List<CellData> data = BackgroundWorker.getCurrentData();
                JSONObject obj = new JSONObject();
                int bestSignal = 0;

                //Logger.add("BU: location was OK, cell list retrieved");
                if (data != null) {
                    bestSignal = -99999;
                    for (CellData e: data) {
                        int eSignal = Integer.parseInt(e.getSignalStrengthValue().substring(0, e.getSignalStrengthValue().length() - 4));
                        if (eSignal > bestSignal) {
                           bestSignal = eSignal;
                        }
                    }

                    //Logger.add("BU: determined best signal: " + bestSignal);
                    try {
                        obj.put("latitude", loc.getLatitude());
                        obj.put("longitude", loc.getLongitude());
                        obj.put("strength", bestSignal);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Logger.add("BU: created JSON obj: " + obj.toString());
                    try {
                        URL url = new URL (remoteSite);
                        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                        DataOutputStream out;
                        urlConn.setDoOutput(true);

                        out = new DataOutputStream(urlConn.getOutputStream());
                        out.writeBytes(URLEncoder.encode(obj.toString(), "UTF-8"));
                        out.close();
                        Logger.add("BU: got HTTP response " + urlConn.getResponseCode() + " - " + urlConn.getResponseMessage());
                        urlConn.disconnect();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
    }
}


