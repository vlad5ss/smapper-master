package info.smapper.smapper.views.fragments;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import info.smapper.smapper.R;
import info.smapper.smapper.logic.Logger;


public class MapFragment extends Fragment {

    static final byte MY_LOCATION_FINE_PERMISSION_ID = 43;
    private static GoogleMap map;
    private static View rootView;
    private static int mapType;
    private static Location location;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null || getChildFragmentManager().findFragmentById(R.id.map) == null) {
            rootView = inflater.inflate(R.layout.fragment_map, container, false);
        }

        map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();

        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_FINE_PERMISSION_ID); // 8 bits for request code, hence we use 42
        } else {
            initializeLocationUpdates();
        }

        map.setMapType(mapType);

        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        initializeLocationUpdates();
    }

    private void initializeLocationUpdates() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);

            LocationListener myLocationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    MapFragment.location = location;
                    LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraUpdate cUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(currentPosition, 15, 0, 0));
                    map.animateCamera(cUpdate, 1000, null);
                    //Logger.add("MF: received location update! current height: " + location.getAltitude() + "m");
                }
                public void onProviderDisabled(String provider) {}
                public void onProviderEnabled(String provider) {}
                public void onStatusChanged(String provider, int status, Bundle extras) {}
            };

            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, myLocationListener);
        }
    }

    public static void setMapType(int type) {
        mapType = type;

        if (map != null) {
            map.setMapType(type);
        }
    }

    public static int getMapType() {
        return mapType;
    }

    public static Location getCurrentLocation() {
        return MapFragment.location;
    }
}



