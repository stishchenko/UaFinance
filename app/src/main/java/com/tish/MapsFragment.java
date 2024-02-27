package com.tish;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.tish.db.connectors.CostConnector;
import com.tish.db.connectors.GeoConnector;
import com.tish.dialogs.BottomInfoDialog;
import com.tish.models.Cost;
import com.tish.models.GeoPair;
import com.tish.models.Geolocation;

import java.util.HashMap;
import java.util.List;

public class MapsFragment extends Fragment
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View mWindow;

        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.map_info_window, null);
        }

        @SuppressLint("SetTextI18n")
        @Nullable
        @Override
        public View getInfoWindow(@NonNull Marker marker) {
            //call first -> if null, call infoContents
            TextView title = mWindow.findViewById(R.id.tv_title_address);
            title.setText(marker.getTitle());
            TextView numberValue = mWindow.findViewById(R.id.tv_snippet_number_value);
            TextView amountValue = mWindow.findViewById(R.id.tv_snippet_amount_value);
            GeoPair currentPair = infoSnippetsMap.get(marker.getTag());
            numberValue.setText(String.valueOf(currentPair.getNumber()));
            amountValue.setText(String.valueOf(-currentPair.getAmount()));
            return mWindow;
        }

        @Nullable
        @Override
        public View getInfoContents(@NonNull Marker marker) {
            //call second -> if null, call default
            return null;
        }


    }

    private static final String TAG = MapsFragment.class.getSimpleName();
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private CameraPosition cameraPosition;
    private final LatLng defaultLocation = new LatLng(53.428663, 14.551073);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    HashMap<Integer, GeoPair> infoSnippetsMap;
    List<Geolocation> geoList;
    CostConnector costConnector;
    GeoConnector geoConnector;

    public MapsFragment() {
        costConnector = new CostConnector(getContext());
        geoConnector = new GeoConnector(getContext());
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        infoSnippetsMap = costConnector.getGeoPairs();

        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;

        addMarkersToMap();

        map.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        map.setOnMarkerClickListener(this);
        map.setOnInfoWindowClickListener(this);

        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
    }

    private void addMarkersToMap() {
        geoList = geoConnector.getGeos();
        Marker currentMarker;
        Geolocation tempGeo;
        for (int i = 0; i < geoList.size(); i++) {
            tempGeo = geoList.get(i);
            float hue = i % 2 == 0 ? BitmapDescriptorFactory.HUE_BLUE : BitmapDescriptorFactory.HUE_YELLOW;
            currentMarker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(tempGeo.getLatitude(), tempGeo.getLongitude()))
                    .title(tempGeo.getAddress())
                    .icon(BitmapDescriptorFactory.defaultMarker(hue)));
            currentMarker.setTag(tempGeo.getGeoId());
        }
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        BottomInfoDialog bottomInfoDialog = new BottomInfoDialog();
        Bundle bundle = new Bundle();
        bundle.putString("title", marker.getTitle());
        bundle.putInt("tag", (Integer) marker.getTag());
        bottomInfoDialog.setArguments(bundle);
        bottomInfoDialog.show(getActivity().getSupportFragmentManager(), "bid");
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        //Toast.makeText(getContext(), marker.getTitle(), Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateLocationUI();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
            map.getUiSettings().setMapToolbarEnabled(false);
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            } else {
                                map.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
}