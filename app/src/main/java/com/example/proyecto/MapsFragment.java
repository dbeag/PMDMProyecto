package com.example.proyecto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.proyecto.model.Ubicacion;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapsFragment extends Fragment implements LocationListener {

    FusedLocationProviderClient fusedLocationProviderClient;
    static ArrayList<Marker> lstMarker = new ArrayList<>();
    static GoogleMap googleMapGeneral;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            googleMapGeneral = googleMap;
            establecerGeoposicionamiento(googleMap);
//            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            actualizarMarcadores(googleMap);
        }


    };

    public static void actualizarTodo() {
        eliminarMarcadores(googleMapGeneral);
        obtenerMarcadores(googleMapGeneral);
    }

    private static void eliminarMarcadores(GoogleMap googleMapGeneral) {
        for (Marker marker : lstMarker){
            marker.remove();
        }
    }

    private static void obtenerMarcadores(GoogleMap googleMapGeneral) {
        if (MainActivity.lstUbicaciones != null) {
            if (MainActivity.lstUbicaciones.size() > 0) {
                Marker marker = null;
                for (Ubicacion ubicacion : MainActivity.lstUbicaciones) {
                    switch (ubicacion.getTipoUbicacion()) {
                        case "Trabajo":
                        case "Work":
                            marker = googleMapGeneral.addMarker(new MarkerOptions().title(ubicacion.getNombre()).position(new LatLng(ubicacion.getLatitud(), ubicacion.getLongitud())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                            break;
                        case "Paisaje":
                        case "Landscape":
                            marker = googleMapGeneral.addMarker(new MarkerOptions().title(ubicacion.getNombre()).position(new LatLng(ubicacion.getLatitud(), ubicacion.getLongitud())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            break;
                        case "Ocio":
                        case "Leisure":
                            marker = googleMapGeneral.addMarker(new MarkerOptions().title(ubicacion.getNombre()).position(new LatLng(ubicacion.getLatitud(), ubicacion.getLongitud())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                            break;
                        default:
                            marker = googleMapGeneral.addMarker(new MarkerOptions().title(ubicacion.getNombre()).position(new LatLng(ubicacion.getLatitud(), ubicacion.getLongitud())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                            break;
                    }
                    lstMarker.add(marker);
                }
            }
        }
    }

    private void actualizarMarcadores(GoogleMap googleMap) {
        if (MainActivity.lstUbicaciones != null) {
            if (MainActivity.lstUbicaciones.size() > 0) {
                for (Ubicacion ubicacion : MainActivity.lstUbicaciones) {
                    Marker marker = googleMap.addMarker(new MarkerOptions().title(ubicacion.getNombre()).position(new LatLng(ubicacion.getLatitud(), ubicacion.getLongitud())));
                    lstMarker.add(marker);
                }
            }
        }
    }

    private void establecerGeoposicionamiento(GoogleMap googleMap) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

                Location location = task.getResult();
                if (location != null) {
                    try {
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        List<Address> direcciones = geocoder.getFromLocation(
                                location.getLatitude(),
                                location.getLongitude(),
                                1
                        );
                        Double latitude = direcciones.get(0).getLatitude();
                        Double longitude = direcciones.get(0).getLongitude();
                        Log.i("UBICACION", latitude.toString());
                        Log.i("UBICACION", longitude.toString());
                        LatLng currentLocation = new LatLng(latitude, longitude);
                        if (currentLocation != null) {
//                            Marker currentMarker = googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Current location"));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15.0f));
                            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        } else {
                            Toast.makeText(getContext(), R.string.app_ubicationNotFound, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        actualizarMarcadores(googleMapGeneral);
        establecerGeoposicionamiento(googleMapGeneral);
    }
}