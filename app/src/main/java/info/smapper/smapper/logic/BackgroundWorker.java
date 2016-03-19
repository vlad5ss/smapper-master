package info.smapper.smapper.logic;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import info.smapper.smapper.R;
import info.smapper.smapper.data.CellData;


public class BackgroundWorker {
    private static boolean useCompatibleMode = false;
    private static BackgroundWorkerDataFetcher fetcher;
    private static Handler handler = new Handler();
    private static Activity activity;
    private static int updateInterval = 1000;
    private static boolean refreshIndicatorVisible = false;

    private static ArrayAdapter<Spanned> adapter;
    private static List<Spanned> listNetworks = new ArrayList<>();
    private static List<CellData> data;

    private static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (fetcher.isModemAvailable()) {
                updateStatisticsFragment();
                updateNetworksFragment(useCompatibleMode);
            }
            handler.postDelayed(this, updateInterval);
        }
    };

    public static void initWorker(Activity activity) {
        BackgroundWorker.activity = activity;
        fetcher = new BackgroundWorkerDataFetcher((TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE), activity);
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, updateInterval);
    }

    private static void updateStatisticsFragment() {
        setTextSafe(R.id.cProvider, "Operator:");
        setTextSafe(R.id.val_cProvider, fetcher.getNetworkOperatorName());

        setTextSafe(R.id.cData1, "Phone Type:");
        setTextSafe(R.id.val_cData1, fetcher.getPhoneType());

        setTextSafe(R.id.cData2, "Network Country Code:");
        setTextSafe(R.id.val_cData2, fetcher.getNetworkCountryIso());

        setTextSafe(R.id.cData3, "Network Type:");
        setTextSafe(R.id.val_cData3, fetcher.getNetworkType());

        CellData cd = fetcher.getCellData();

        setTextSafe(R.id.cData4, "Signal Strength:");
        setTextSafe(R.id.val_cData4, cd.getSignalStrengthValue());

        setTextSafe(R.id.cData5, "Signal Quality:");
        setTextSafe(R.id.val_cData5, cd.getSignalQuality());

        setTextSafe(R.id.cData6, "Available Base Stations:");
        setTextSafe(R.id.val_cData6, cd.getAvailableCells());

        setTextSafe(R.id.cData7, "Registered Base Stations:");
        setTextSafe(R.id.val_cData7, cd.getRegisteredCells());

        setTextSafe(R.id.cData8, cd.getLowLevelCellData()[0][0]);
        setTextSafe(R.id.val_cData8, cd.getLowLevelCellData()[0][1]);

        setTextSafe(R.id.cData9, cd.getLowLevelCellData()[1][0]);
        setTextSafe(R.id.val_cData9, cd.getLowLevelCellData()[1][1]);

        setTextSafe(R.id.cData10, cd.getLowLevelCellData()[2][0]);
        setTextSafe(R.id.val_cData10, cd.getLowLevelCellData()[2][1]);

        setTextSafe(R.id.cData11, cd.getLowLevelCellData()[3][0]);
        setTextSafe(R.id.val_cData11, cd.getLowLevelCellData()[3][1]);

        setTextSafe(R.id.cData12, cd.getLowLevelCellData()[4][0]);
        setTextSafe(R.id.val_cData12, cd.getLowLevelCellData()[4][1]);

        ImageView indicator = (ImageView) activity.findViewById(R.id.refreshIcon);
        if (indicator != null) {
            if (refreshIndicatorVisible) {
                indicator.setVisibility(View.INVISIBLE);
                refreshIndicatorVisible = false;
            } else {
                indicator.setVisibility(View.VISIBLE);
                refreshIndicatorVisible = true;
            }
        }
    }

    private static void updateNetworksFragment(boolean useCompatibleMode) {
        //data = null;

        if (useCompatibleMode) {
            data = fetcher.getAllCellDataCompatible();
        } else {
            data = fetcher.getAllCellData();
        }


        listNetworks.clear();
        for (CellData e:data) {
            String[][] lowLevelCellData = e.getLowLevelCellData();
            String formattedEntry = "Operator: " + e.getOperator() + "<br>" + lowLevelCellData[3][0] + " " + lowLevelCellData[3][1] + "<br>" + lowLevelCellData[0][0] + " " + lowLevelCellData[0][1] + "<br>" + lowLevelCellData[2][0] + " " + lowLevelCellData[2][1] + "<br>Signal Strength: " + e.getSignalStrengthValue() + " (" + e.getSignalQuality() + ")" + "<br>" + lowLevelCellData[1][0] + " " + lowLevelCellData[1][1] + "<br>" + lowLevelCellData[4][0] + " " + lowLevelCellData[4][1] + "<br>isRegistered: " + e.getIsRegistered();
            listNetworks.add(Html.fromHtml(formattedEntry));
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private static void setTextSafe(int id_TextView, String text) {
        TextView t = (TextView) activity.findViewById(id_TextView);
        if (t != null) {
            t.setText(text);
        }
    }

    public static void setNetworkAdapter(ArrayAdapter<Spanned> adapter) {
        BackgroundWorker.adapter = adapter;
    }

    public static List<Spanned> getListNetworks() {
        return listNetworks;
    }

    public static boolean getCompatibleModeState() {
        return useCompatibleMode;
    }

    public static void setCompatibleModeState(boolean state) {
        useCompatibleMode = state;
    }

    public static int getUpdateInterval() {
        return updateInterval;
    }

    public static void setUpdateInterval(int interval) {
        updateInterval = interval;
    }

    public static List<CellData> getCurrentData() {
        return data;
    }
}


