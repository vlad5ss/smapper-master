package info.smapper.smapper.data;


import android.telephony.CellSignalStrength;

public class CellData {
    String operator = "n/a";
    String availableCells;
    String registeredCells;
    String signalStrengthValue;
    String signalQuality;
    String[][] lowLevelCellData;
    boolean isRegistered = false;

    public void setAvailableCells(int availableCells) {
        this.availableCells = String.valueOf(availableCells);
    }

    public void setRegisteredCells(int registeredCells) {
        this.registeredCells = String.valueOf(registeredCells);
    }

    public void setSignalStrengthValue(int signalStrengthValue) {
        this.signalStrengthValue = String.valueOf(signalStrengthValue) + " dBm";
    }

    public void setSignalQuality(int signalQuality) {
        if (signalQuality == CellSignalStrength.SIGNAL_STRENGTH_NONE_OR_UNKNOWN) {
            this.signalQuality = "UNKNOWN";
        } else if (signalQuality == CellSignalStrength.SIGNAL_STRENGTH_POOR) {
            this.signalQuality = "POOR";
        } else if (signalQuality == CellSignalStrength.SIGNAL_STRENGTH_MODERATE) {
            this.signalQuality = "MODERATE";
        } else if (signalQuality == CellSignalStrength.SIGNAL_STRENGTH_GOOD) {
            this.signalQuality = "GOOD";
        } else if (signalQuality == CellSignalStrength.SIGNAL_STRENGTH_GREAT) {
            this.signalQuality = "GREAT";
        }
    }

    public void setLowLevelCellData(String[][] lowLevelCellData) {
        this.lowLevelCellData = lowLevelCellData;
    }

    public void setIsRegistered(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }

    public String getAvailableCells() {
        return this.availableCells;
    }

    public String getRegisteredCells() {
        return this.registeredCells;
    }

    public String getSignalStrengthValue() {
        return this.signalStrengthValue;
    }

    public String getSignalQuality() {
        return this.signalQuality;
    }

    public String[][] getLowLevelCellData() {
        return this.lowLevelCellData;
    }

    public boolean getIsRegistered() {
        return this.isRegistered;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return this.operator;
    }
}

