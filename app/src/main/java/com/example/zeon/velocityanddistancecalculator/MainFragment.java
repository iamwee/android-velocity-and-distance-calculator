package com.example.zeon.velocityanddistancecalculator;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class MainFragment extends Fragment {

    private Button btnStart, btnStop, btnLog;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initInstances(rootView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        isLocationPermissionGranted();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void initInstances(View rootView) {
        btnStart = (Button) rootView.findViewById(R.id.btn_start);
        btnStop = (Button) rootView.findViewById(R.id.btn_stop);
        btnLog = (Button) rootView.findViewById(R.id.btn_log);


        btnStart.setOnClickListener(clickListener);
        btnStop.setOnClickListener(clickListener);
        btnLog.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == btnStart) {
                startCalculate();
            } else if (view == btnStop) {
                stopCalculate();
            } else if (view == btnLog) {
                showLog();
            }
        }
    };

    private DialogInterface.OnClickListener dialogClickListener =
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    getActivity().startActivity(new Intent(Settings.
                            ACTION_LOCATION_SOURCE_SETTINGS));
                }
            };

    private void startCalculate() {
        if (isLocationAvailable()) {
            if (isLocationPermissionGranted()) {
                BusProvider.getInstance().post(new SimpleEvent(SimpleEvent.OPEN_SERVICE));
            }
        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Warning")
                    .setMessage("Enable location to use, Enable now?")
                    .setPositiveButton(R.string.cast_tracks_chooser_dialog_ok, dialogClickListener)
                    .setNegativeButton(R.string.cast_tracks_chooser_dialog_cancel, null)
                    .show();
        }
    }

    private boolean isLocationPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {

            int fineLocationGrant = ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int coarseLocationGrant = ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if (fineLocationGrant != PackageManager.PERMISSION_GRANTED
                    && coarseLocationGrant != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    new AlertDialog.Builder(getActivity())
                            .setMessage("You need to grant access to Location\nGrant now?")
                            .setPositiveButton(R.string.cast_tracks_chooser_dialog_ok,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent =
                                                    new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.parse("package:"
                                                    + getActivity().getPackageName()));
                                            startActivity(intent);
                                        }
                                    })
                            .setNegativeButton(R.string.cast_tracks_chooser_dialog_cancel, null)
                            .show();
                    return false;
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION}, 123);
                    return false;
                }
            }
        }
        return true;
    }

    private void stopCalculate() {
        BusProvider.getInstance().post(new SimpleEvent(SimpleEvent.CLOSR_SERVICE));
    }

    private void showLog() {
        Toast.makeText(getActivity(), "Show Log", Toast.LENGTH_SHORT).show();
    }

    private boolean isLocationAvailable() {
        LocationManager manager = (LocationManager)
                getActivity().getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void init() {

    }
}
