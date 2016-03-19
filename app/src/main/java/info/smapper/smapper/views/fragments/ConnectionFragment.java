package info.smapper.smapper.views.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import info.smapper.smapper.R;


public class ConnectionFragment extends Fragment {
    private static View rootView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_connection, container, false);
        }

        return rootView;
    }
}
