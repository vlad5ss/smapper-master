package info.smapper.smapper.data;


public class Configuration {
    private int updateInterval;
    private boolean compatibleModeStatus;
    private int mapType;

    public Configuration() {
        this.updateInterval = 1000;
        this.compatibleModeStatus = false;
        this.mapType = 4;
    }

    public Configuration(boolean compatibleModeStatus, int updateInterval, int mapType) {
        this.compatibleModeStatus = compatibleModeStatus;
        this.updateInterval = updateInterval;
        this.mapType = mapType;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public boolean getCompatibleModeStatus() {
        return compatibleModeStatus;
    }

    public void setUpdateInterval(int interval) {
        this.updateInterval = interval;
    }

    public void setCompatibleModeStatus(boolean status) {
        this.compatibleModeStatus = status;
    }

    public void setMapType(int mapType) {
        this.mapType = mapType;
    }

    public int getMapType() {
        return mapType;
    }
}
