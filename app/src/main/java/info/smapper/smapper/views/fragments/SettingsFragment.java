package info.smapper.smapper.views.fragments;

import android.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;


import java.util.ArrayList;
import java.util.List;

import info.smapper.smapper.R;
import info.smapper.smapper.data.Configuration;
import info.smapper.smapper.logic.BackgroundWorker;
import info.smapper.smapper.logic.IoHandler;

public class SettingsFragment extends Fragment {

    private static View rootView;
    private static CheckBox compatibility;
    private static SeekBar connectionUpdateInterval;
    private static Button clearButtonMeasurements;
    private static Button clearButtonSettings;
    private static Spinner mapTypeSelection;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_settings, container, false);

            compatibility = (CheckBox) rootView.findViewById(R.id.settings_UseCompatibleMode);
            compatibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    BackgroundWorker.setCompatibleModeState(isChecked);
                    Configuration config = new Configuration(isChecked, connectionUpdateInterval.getProgress() + 250, mapTypeSelection.getSelectedItemPosition());
                    IoHandler.saveSettings(config);
                }
            });
            connectionUpdateInterval = (SeekBar) rootView.findViewById(R.id.settings_UpdateInterval);
            connectionUpdateInterval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int realValue = progress + 250;
                    BackgroundWorker.setUpdateInterval(realValue);
                    Configuration config = new Configuration(compatibility.isChecked(), realValue, mapTypeSelection.getSelectedItemPosition());
                    IoHandler.saveSettings(config);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            clearButtonMeasurements = (Button) rootView.findViewById(R.id.settings_ClearCache);
            clearButtonMeasurements.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IoHandler.clearMeasurements();
                }
            });

            clearButtonSettings = (Button) rootView.findViewById(R.id.settings_ClearSettings);
            clearButtonSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IoHandler.clearSettings();
                }
            });

            mapTypeSelection = (Spinner) rootView.findViewById(R.id.settings_MapType);
            List<String> mapTypes = new ArrayList<>();
            mapTypes.add("None");
            mapTypes.add("Normal");
            mapTypes.add("Satellite");
            mapTypes.add("Terrain");
            mapTypes.add("Hybrid");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mapTypes);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mapTypeSelection.setAdapter(adapter);
            mapTypeSelection.setSelection(MapFragment.getMapType());
            mapTypeSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Configuration config = new Configuration(compatibility.isChecked(), connectionUpdateInterval.getProgress() + 250, position);
                    MapFragment.setMapType(position);
                    IoHandler.saveSettings(config);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            if (compatibility != null) {
                compatibility.setChecked(getCurrentCompatiblitySetting());
            }

            if (connectionUpdateInterval != null) {
                connectionUpdateInterval.setProgress(BackgroundWorker.getUpdateInterval() - 250);
            }
        }

        return rootView;
    }

    private boolean getCurrentCompatiblitySetting() {
        return BackgroundWorker.getCompatibleModeState();
    }
}
