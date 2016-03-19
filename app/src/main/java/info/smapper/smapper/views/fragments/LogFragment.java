package info.smapper.smapper.views.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import info.smapper.smapper.R;
import info.smapper.smapper.logic.Logger;


public class LogFragment extends Fragment {

    private static View rootView;
    private static ListView listView;
    private static ArrayAdapter<Spanned> adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_log, container, false);
            listView = (ListView) rootView.findViewById(R.id.log_ListView);

            adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, Logger.getList());
            listView.setAdapter(adapter);

            Logger.setReferences(this, adapter);
        }

        return rootView;
    }
}

