package info.smapper.smapper.logic;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import info.smapper.smapper.data.Configuration;

public class IoHandler {
    private static File fileSettings;
    private static File fileMeasurements;
    private static File fileMeasurementsParentFolder;
    private static Activity activity;
    private static byte MY_WRITE_EXTERNAL_STORAGE_ID = 43;

    public static void initIoHandler(Activity activity) {
        IoHandler.activity = activity;
        fileSettings = new File(IoHandler.activity.getBaseContext().getFilesDir(), "Settings.ini");
        fileMeasurements = new File(Environment.getExternalStorageDirectory() + "/Smapper", "Measurements.log");
        fileMeasurementsParentFolder = new File(Environment.getExternalStorageDirectory() + "/Smapper");
        Logger.add("IoHandler is now ready.");
        Logger.add("Previously taken measurements take in total " + fileMeasurements.length() + " bytes of space.");
    }

    public static Configuration readSettings() {
        Configuration config = new Configuration();

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileSettings));

            config.setCompatibleModeStatus(Boolean.parseBoolean(br.readLine()));
            config.setUpdateInterval(Integer.valueOf(br.readLine()));
            config.setMapType(Integer.valueOf(br.readLine()));

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            // Nothing here, file not found can happen...
            // NumberException (cast null to int) + IOException (file access) can occur
        }

        return config;
    }

    public static void saveSettings(Configuration config) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileSettings, false));
            bw.write(String.valueOf(config.getCompatibleModeStatus()));
            bw.newLine();
            bw.write(String.valueOf(config.getUpdateInterval()));
            bw.newLine();
            bw.write(String.valueOf(config.getMapType()));
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Nothing here, file not found can happen...
        }
    }

    public static void saveData(String data, String deviceId) {
        if (ContextCompat.checkSelfPermission(activity.getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_WRITE_EXTERNAL_STORAGE_ID); // 8 bits for request code, hence we use 43
                return;
        }

        if (!fileMeasurementsParentFolder.exists()) {
            fileMeasurementsParentFolder.mkdir();
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(fileMeasurements, true);
            outputStream.write(deviceId.getBytes());
            outputStream.write(": ".getBytes());
            outputStream.write(data.getBytes());
            outputStream.write("\n".getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearMeasurements() {
        fileMeasurements.delete();
        fileMeasurements = new File(Environment.getExternalStorageDirectory() + "/Smapper", "Measurements.log");
        Logger.add("Measurement Cache successfully cleared.");
    }

    public static void clearSettings() {
        fileSettings.delete();
        fileSettings = new File(IoHandler.activity.getBaseContext().getFilesDir(), "Settings.ini");
        Logger.add("Settings successfully cleared.");
    }

    public static File getAccessibleFileObject() {
        return fileMeasurements;
    }
}
