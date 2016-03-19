package info.smapper.smapper.logic;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import info.smapper.smapper.data.CellData;
import info.smapper.smapper.views.activities.MainActivity;
import info.smapper.smapper.views.fragments.MapFragment;

public class BackgroundWorkerDataFetcher {
    private TelephonyManager tel;
    private HashMap<Integer, String> operatorIDs = new HashMap<>();
    private Activity activity;
    private byte MY_READ_PHONE_STATE_ID = 44;

    public BackgroundWorkerDataFetcher(TelephonyManager telephonyManager, Activity activity) {
        this.activity = activity;
        this.tel = telephonyManager;
        prepareOperatorIDMap();
    }

    private void prepareOperatorIDMap() {
        operatorIDs.put(1, "Swisscom (Schweiz) AG");
        operatorIDs.put(2, "Sunrise Communications AG");
        operatorIDs.put(3, "Salt Mobile SA");
        operatorIDs.put(5, "Comfone AG");
        operatorIDs.put(6, "Schweizerische Bundesbahnen SBB");
        operatorIDs.put(8, "TelCommunication Services AG");
        operatorIDs.put(9, "Comfone AG");
        operatorIDs.put(12, "Sunrise Communications AG");
        operatorIDs.put(51, "BebbiCell AG");
        operatorIDs.put(53, "upc cablecom GmbH");
        operatorIDs.put(54, "Lycamobile AG");
        operatorIDs.put(55, "WeMobile SA");
        operatorIDs.put(57, "Mitto AG");
        operatorIDs.put(60, "Sunrise Communications AG");
        operatorIDs.put(99, "Swisscom Broadcast AG");
    }

    public boolean isModemAvailable() {
        try {
            if (tel.getAllCellInfo() == null) {
                return false;
            } else {
                return true;
            }
        } catch (SecurityException e) {
            return false;
        }
    }

    public String getPhoneType() {
        String cPhoneType = "";
        if (tel.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
            cPhoneType = "GSM";
        } else if (tel.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
            cPhoneType = "CDMA";
        } else if (tel.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            cPhoneType = "NONE";
        } else if (tel.getPhoneType() == TelephonyManager.PHONE_TYPE_SIP) {
            cPhoneType = "SIP";
        }

        return cPhoneType;
    }

    public String getNetworkOperatorName() {
        return tel.getNetworkOperatorName();
    }

    public String getNetworkCountryIso() {
        return tel.getNetworkCountryIso();
    }

    public String getNetworkType() {
        String cNetworkType = "";
        int networkType = tel.getNetworkType();

        if (networkType == TelephonyManager.NETWORK_TYPE_1xRTT) {
            cNetworkType = "1xRTT";
        } else if (networkType == TelephonyManager.NETWORK_TYPE_CDMA) {
            cNetworkType = "CDMA";
        } else if (networkType == TelephonyManager.NETWORK_TYPE_EDGE) {
            cNetworkType = "EDGE";
        } else if (networkType == TelephonyManager.NETWORK_TYPE_EHRPD) {
            cNetworkType = "EHRPD";
        } else if (networkType == TelephonyManager.NETWORK_TYPE_EVDO_0) {
            cNetworkType = "EVDO_0";
        } else if (networkType == TelephonyManager.NETWORK_TYPE_EVDO_A) {
            cNetworkType = "EVDO_A";
        } else if (networkType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
            cNetworkType = "EVDO_B";
        } else if (networkType == TelephonyManager.NETWORK_TYPE_GPRS) {
            cNetworkType = "GPRS";
        } else if (networkType == TelephonyManager.NETWORK_TYPE_HSDPA) {
            cNetworkType = "HSDPA";
        } else if (networkType == TelephonyManager.NETWORK_TYPE_HSPA) {
            cNetworkType = "HSPA";
        } else if (networkType == TelephonyManager.NETWORK_TYPE_HSPAP) {
            cNetworkType = "HSPAP";
        } else if (networkType == TelephonyManager.NETWORK_TYPE_HSUPA) {
            cNetworkType = "HSUPA";
        } else if (networkType == TelephonyManager.NETWORK_TYPE_IDEN) {
            cNetworkType = "IDEN";
        } else if (networkType == TelephonyManager.NETWORK_TYPE_LTE) {
            cNetworkType = "LTE";
        } else if (networkType == TelephonyManager.NETWORK_TYPE_UMTS) {
            cNetworkType = "UMTS";
        } else if (networkType == TelephonyManager.NETWORK_TYPE_UNKNOWN) {
            cNetworkType = "UNKNOWN";
        }

        return cNetworkType;
    }

    public CellData getCellData() {
        int registeredConnections = 0;
        int signalStrengthValue = 0;
        int signalQuality = 0;
        String[][] lowLevelCellData = new String[5][2];

        List<CellInfo> list = tel.getAllCellInfo();

        CellData cd = new CellData();
        cd.setAvailableCells(list.size());

        for (int i = 0; i < list.size(); i++) {
            try {
                CellInfoGsm cellInfo = (CellInfoGsm) list.get(i);
                CellIdentityGsm cellIdentity = cellInfo.getCellIdentity();
                if (cellInfo.isRegistered()) {
                    registeredConnections++;
                    CellSignalStrengthGsm signalStrength = cellInfo.getCellSignalStrength();
                    signalStrengthValue = signalStrength.getDbm();
                    signalQuality = signalStrength.getLevel();
                    lowLevelCellData[0][0] = "Cell ID:";
                    lowLevelCellData[0][1] = String.valueOf(cellIdentity.getCid());
                    lowLevelCellData[1][0] = "Location Area Code:";
                    lowLevelCellData[1][1] = String.valueOf(cellIdentity.getLac());
                    lowLevelCellData[2][0] = "Mobile Country Code:";
                    lowLevelCellData[2][1] = String.valueOf(cellIdentity.getMcc());
                    lowLevelCellData[3][0] = "Mobile Network Code:";
                    lowLevelCellData[3][1] = String.valueOf(cellIdentity.getMnc()) + ", " + operatorIDs.get(cellIdentity.getMnc());
                    lowLevelCellData[4][0] = "";
                    lowLevelCellData[4][1] = "";
                }
            } catch (ClassCastException e) {}

            try {
                CellInfoCdma cellInfo = (CellInfoCdma) list.get(i);
                CellIdentityCdma cellIdentity = cellInfo.getCellIdentity();
                if (cellInfo.isRegistered()) {
                    registeredConnections++;
                    CellSignalStrengthCdma signalStrength = cellInfo.getCellSignalStrength();
                    signalStrengthValue = signalStrength.getDbm();
                    signalQuality = signalStrength.getLevel();
                    lowLevelCellData[0][0] = "Base Station ID:";
                    lowLevelCellData[0][1] = String.valueOf(cellIdentity.getBasestationId());
                    lowLevelCellData[1][0] = "Latitude:";
                    lowLevelCellData[1][1] = String.valueOf(cellIdentity.getLatitude());
                    lowLevelCellData[2][0] = "Longitude:";
                    lowLevelCellData[2][1] = String.valueOf(cellIdentity.getLongitude());
                    lowLevelCellData[3][0] = "Network ID:";
                    lowLevelCellData[3][1] = String.valueOf(cellIdentity.getNetworkId());
                    lowLevelCellData[4][0] = "System ID:";
                    lowLevelCellData[4][1] = String.valueOf(cellIdentity.getSystemId());
                }
            } catch (ClassCastException e) {}

            try {
                CellInfoWcdma cellInfo = (CellInfoWcdma) list.get(i);
                CellIdentityWcdma cellIdentity = cellInfo.getCellIdentity();
                if (cellInfo.isRegistered()) {
                    registeredConnections++;
                    CellSignalStrengthWcdma signalStrength = cellInfo.getCellSignalStrength();
                    signalStrengthValue = signalStrength.getDbm();
                    signalQuality = signalStrength.getLevel();
                    lowLevelCellData[0][0] = "Cell ID:";
                    lowLevelCellData[0][1] = String.valueOf(cellIdentity.getCid());
                    lowLevelCellData[1][0] = "Location Area Code:";
                    lowLevelCellData[1][1] = String.valueOf(cellIdentity.getLac());
                    lowLevelCellData[2][0] = "Mobile Country Code:";
                    lowLevelCellData[2][1] = String.valueOf(cellIdentity.getMcc());
                    lowLevelCellData[3][0] = "Mobile Network Code:";
                    lowLevelCellData[3][1] = String.valueOf(cellIdentity.getMnc() + ", " + operatorIDs.get(cellIdentity.getMnc()));
                    lowLevelCellData[4][0] = "Primary Scrambling Code:";
                    lowLevelCellData[4][1] = String.valueOf(cellIdentity.getPsc());
                }
            } catch (ClassCastException e) {}

            try {
                CellInfoLte cellInfo = (CellInfoLte) list.get(i);
                CellIdentityLte cellIdentity = cellInfo.getCellIdentity();
                if (cellInfo.isRegistered()) {
                    registeredConnections++;
                    CellSignalStrengthLte signalStrength = cellInfo.getCellSignalStrength();
                    signalStrengthValue = signalStrength.getDbm();
                    signalQuality = signalStrength.getLevel();
                    lowLevelCellData[0][0] = "Cell ID:";
                    lowLevelCellData[0][1] = String.valueOf(cellIdentity.getCi());
                    lowLevelCellData[1][0] = "Mobile Country Code:";
                    lowLevelCellData[1][1] = String.valueOf(cellIdentity.getMcc());
                    lowLevelCellData[2][0] = "Physical Cell ID:";
                    lowLevelCellData[2][1] = String.valueOf(cellIdentity.getPci());
                    lowLevelCellData[3][0] = "Mobile Network Code:";
                    lowLevelCellData[3][1] = String.valueOf(cellIdentity.getMnc() + ", " + operatorIDs.get(cellIdentity.getMnc()));
                    lowLevelCellData[4][0] = "Tracking Area Code:";
                    lowLevelCellData[4][1] = String.valueOf(cellIdentity.getTac());
                }
            } catch (ClassCastException e) {}
        }

        cd.setSignalQuality(signalQuality);
        cd.setRegisteredCells(registeredConnections);
        cd.setSignalStrengthValue(signalStrengthValue);
        cd.setLowLevelCellData(lowLevelCellData);
        return cd;
    }

    public List<CellData> getAllCellData() {
        List<CellInfo> list = tel.getAllCellInfo();
        List<CellData> listData = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            int signalStrengthValue = 0;
            int signalQuality = 0;
            boolean isRegistered = false;
            String[][] lowLevelCellData = new String[5][2];
            String operator = "";
            CellData cd = new CellData();

            try {
                CellInfoGsm cellInfo = (CellInfoGsm) list.get(i);
                CellIdentityGsm cellIdentity = cellInfo.getCellIdentity();
                isRegistered = cellInfo.isRegistered();
                CellSignalStrengthGsm signalStrength = cellInfo.getCellSignalStrength();
                signalStrengthValue = signalStrength.getDbm();
                signalQuality = signalStrength.getLevel();
                operator = operatorIDs.get(cellIdentity.getMnc());
                lowLevelCellData[0][0] = "Cell ID:";
                lowLevelCellData[0][1] = String.valueOf(cellIdentity.getCid());
                lowLevelCellData[1][0] = "Mobile Country Code:";
                lowLevelCellData[1][1] = String.valueOf(cellIdentity.getMcc());
                lowLevelCellData[2][0] = "Location Area Code:";
                lowLevelCellData[2][1] = String.valueOf(cellIdentity.getLac());
                lowLevelCellData[3][0] = "Mobile Network Code:";
                lowLevelCellData[3][1] = String.valueOf(cellIdentity.getMnc());
                lowLevelCellData[4][0] = "";
                lowLevelCellData[4][1] = "";
            } catch (ClassCastException e) {}

            try {
                CellInfoCdma cellInfo = (CellInfoCdma) list.get(i);
                CellIdentityCdma cellIdentity = cellInfo.getCellIdentity();
                isRegistered = cellInfo.isRegistered();
                CellSignalStrengthCdma signalStrength = cellInfo.getCellSignalStrength();
                signalStrengthValue = signalStrength.getDbm();
                signalQuality = signalStrength.getLevel();
                lowLevelCellData[0][0] = "Base Station ID:";
                lowLevelCellData[0][1] = String.valueOf(cellIdentity.getBasestationId());
                lowLevelCellData[1][0] = "Latitude:";
                lowLevelCellData[1][1] = String.valueOf(cellIdentity.getLatitude());
                lowLevelCellData[2][0] = "System ID:";
                lowLevelCellData[2][1] = String.valueOf(cellIdentity.getSystemId());
                lowLevelCellData[3][0] = "Network ID:";
                lowLevelCellData[3][1] = String.valueOf(cellIdentity.getNetworkId());
                lowLevelCellData[4][0] = "Longitude:";
                lowLevelCellData[4][1] = String.valueOf(cellIdentity.getLongitude());
            } catch (ClassCastException e) {}

            try {
                CellInfoWcdma cellInfo = (CellInfoWcdma) list.get(i);
                CellIdentityWcdma cellIdentity = cellInfo.getCellIdentity();
                isRegistered = cellInfo.isRegistered();
                CellSignalStrengthWcdma signalStrength = cellInfo.getCellSignalStrength();
                signalStrengthValue = signalStrength.getDbm();
                signalQuality = signalStrength.getLevel();
                operator = operatorIDs.get(cellIdentity.getMnc());
                lowLevelCellData[0][0] = "Cell ID:";
                lowLevelCellData[0][1] = String.valueOf(cellIdentity.getCid());
                lowLevelCellData[1][0] = "Mobile Country Code:";
                lowLevelCellData[1][1] = String.valueOf(cellIdentity.getMcc());
                lowLevelCellData[2][0] = "Location Area Code:";
                lowLevelCellData[2][1] = String.valueOf(cellIdentity.getLac());
                lowLevelCellData[3][0] = "Mobile Network Code:";
                lowLevelCellData[3][1] = String.valueOf(cellIdentity.getMnc());
                lowLevelCellData[4][0] = "Primary Scrambling Code:";
                lowLevelCellData[4][1] = String.valueOf(cellIdentity.getPsc());
            } catch (ClassCastException e) {}

            try {
                CellInfoLte cellInfo = (CellInfoLte) list.get(i);
                CellIdentityLte cellIdentity = cellInfo.getCellIdentity();
                isRegistered = cellInfo.isRegistered();
                CellSignalStrengthLte signalStrength = cellInfo.getCellSignalStrength();
                signalStrengthValue = signalStrength.getDbm();
                signalQuality = signalStrength.getLevel();
                operator = operatorIDs.get(cellIdentity.getMnc());
                lowLevelCellData[0][0] = "Cell ID:";
                lowLevelCellData[0][1] = String.valueOf(cellIdentity.getCi());
                lowLevelCellData[1][0] = "Mobile Country Code:";
                lowLevelCellData[1][1] = String.valueOf(cellIdentity.getMcc());
                lowLevelCellData[2][0] = "Physical Cell ID:";
                lowLevelCellData[2][1] = String.valueOf(cellIdentity.getPci());
                lowLevelCellData[3][0] = "Mobile Network Code:";
                lowLevelCellData[3][1] = String.valueOf(cellIdentity.getMnc());
                lowLevelCellData[4][0] = "Tracking Area Code:";
                lowLevelCellData[4][1] = String.valueOf(cellIdentity.getTac());
            } catch (ClassCastException e) {}

            if (operator != null) {
                cd.setOperator(operator);
            }
            cd.setIsRegistered(isRegistered);
            cd.setSignalQuality(signalQuality);
            cd.setSignalStrengthValue(signalStrengthValue);
            cd.setLowLevelCellData(lowLevelCellData);
            listData.add(cd);
        }

        saveData(listData);
        return listData;
    }

    public List<CellData> getAllCellDataCompatible() {
        List<NeighboringCellInfo> list = tel.getNeighboringCellInfo();
        List<CellData> listData = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            int signalStrengthValue = 0;
            int signalQuality = 0;
            String[][] lowLevelCellData = new String[5][2];
            CellData cd = new CellData();

            try {
                NeighboringCellInfo cellInfo = list.get(i);
                signalStrengthValue = -113 + 2 * cellInfo.getRssi(); //GSM only, UMTS differs...
                lowLevelCellData[0][0] = "Cell ID:";
                lowLevelCellData[0][1] = String.valueOf(cellInfo.getCid());
                lowLevelCellData[1][0] = "Location Area Code:";
                lowLevelCellData[1][1] = String.valueOf(cellInfo.getLac());
                lowLevelCellData[2][0] = "Network Type:";
                lowLevelCellData[2][1] = ""; // will be set below
                lowLevelCellData[3][0] = "Primary Scrambling Code:"; //only used in UMTS
                lowLevelCellData[3][1] = String.valueOf(cellInfo.getPsc());
                lowLevelCellData[4][0] = "";
                lowLevelCellData[4][1] = "";

                if (cellInfo.getNetworkType() == TelephonyManager.NETWORK_TYPE_GPRS) {
                    lowLevelCellData[2][1] = "GPRS";
                } else if (cellInfo.getNetworkType() == TelephonyManager.NETWORK_TYPE_EDGE) {
                    lowLevelCellData[2][1] = "EDGE";
                } else if (cellInfo.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS) {
                    lowLevelCellData[2][1] = "UMTS";
                } else if (cellInfo.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSDPA) {
                    lowLevelCellData[2][1] = "HSDPA";
                } else if (cellInfo.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSUPA) {
                    lowLevelCellData[2][1] = "HSUPA";
                } else if (cellInfo.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSPA) {
                    lowLevelCellData[2][1] = "HSPA";
                }
            } catch (ClassCastException e) {}

            cd.setSignalQuality(signalQuality);
            cd.setSignalStrengthValue(signalStrengthValue);
            cd.setLowLevelCellData(lowLevelCellData);
            listData.add(cd);
        }

        saveData(listData);
        return listData;
    }

    private void saveData(List<CellData> data) {
        StringBuilder s = new StringBuilder();
        CellData cd;
        Location location = MapFragment.getCurrentLocation();
        for (int i = 0; i < data.size(); i++) {
            cd = data.get(i);
            if (i == 0) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
                String currentTimestamp = simpleDateFormat.format(new Date());
                s.append(currentTimestamp + "|");
                s.append("v_" + MainActivity.version + "|");
                s.append("availableCells_" + cd.getAvailableCells() + "|");
                s.append("registeredCells_" + cd.getRegisteredCells() + "|");
                s.append("operator_" + cd.getOperator() + "|");

                if (location != null) {
                    s.append("location_lat_" + location.getLatitude() + "|");
                    s.append("location_lon_" + location.getLongitude() + "|");
                } else {
                    s.append("location_lat_" + "n/a" + "|");
                    s.append("location_lon_" + "n/a" + "|");
                }
            }

            s.append("cell_" + i + "|");
            s.append("isRegistered_" + cd.getIsRegistered() + "|");
            s.append("signalQuality_" + cd.getSignalQuality() + "|");
            s.append("signalStrength_" + cd.getSignalStrengthValue() + "|");
            s.append(cd.getLowLevelCellData()[0][0]+ ":" + cd.getLowLevelCellData()[0][1] + "|");
            s.append(cd.getLowLevelCellData()[1][0]+ ":" + cd.getLowLevelCellData()[1][1] + "|");
            s.append(cd.getLowLevelCellData()[2][0]+ ":" + cd.getLowLevelCellData()[2][1] + "|");
            s.append(cd.getLowLevelCellData()[3][0] + ":" + cd.getLowLevelCellData()[3][1] + "|");
            s.append(cd.getLowLevelCellData()[4][0] + ":" + cd.getLowLevelCellData()[4][1]);
        }

        if (ContextCompat.checkSelfPermission(activity.getBaseContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_PHONE_STATE}, MY_READ_PHONE_STATE_ID); // 8 bits for request code, hence we use 44
            return;
        }

        IoHandler.saveData(s.toString(), tel.getDeviceId());
    }
}
