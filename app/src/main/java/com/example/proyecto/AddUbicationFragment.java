package com.example.proyecto;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto.model.TipoUbicacion;
import com.example.proyecto.model.Ubicacion;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddUbicationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddUbicationFragment extends Fragment {

    Button btnSave;
    EditText etName, etDescription;
    RadioGroup rgType;
    RadioButton rbOtros, rbTrabajo, rbPaisaje, rbRestaurante;
    TextView tvLatitud, tvLongitud;

    public Ubicacion ubication = new Ubicacion();
    public static Double latitud;
    public static Double longitud;

    public AddUbicationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddUbicationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddUbicationFragment newInstance(String param1, String param2) {
        AddUbicationFragment fragment = new AddUbicationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_ubication, container, false);
        btnSave = view.findViewById(R.id.btnSave);
        etName = view.findViewById(R.id.etNombre);
        etDescription = view.findViewById(R.id.etDescripcion);
        rgType = view.findViewById(R.id.rgType);
        rbOtros = view.findViewById(R.id.rbOtros);
        rbRestaurante = view.findViewById(R.id.rbRestaurante);
        rbPaisaje = view.findViewById(R.id.rbPaisaje);
        rbTrabajo = view.findViewById(R.id.rbTrabajo);
        rbOtros.setChecked(true);
        agregarLocalizacion(view);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latitud != null && longitud != null) {
                    if (!etName.getText().toString().isEmpty()) {
                        ubication.setNombre(etName.getText().toString().trim());
                        if (!etDescription.getText().toString().isEmpty()) {
                            ubication.setDescripcion(etDescription.getText().toString().trim());
                        }
                        ubication.setLatitud(latitud);
                        ubication.setLongitud(longitud);
                        if (rbTrabajo.isChecked()) {
                            ubication.setTipoUbicacion("TRABAJO");
                        } else if (rbPaisaje.isChecked()) {
                            ubication.setTipoUbicacion("PAISAJE");
                        } else if (rbRestaurante.isChecked()) {
                            ubication.setTipoUbicacion("RESTAURANTE");
                        } else {
                            ubication.setTipoUbicacion("OTROS");
                        }
                    } else {
                        Toast.makeText(getActivity(), R.string.app_namenotset, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), R.string.app_ubicationNotFound, Toast.LENGTH_SHORT).show();
                }
                MapsFragment.actualizarTodo();
                MainActivity.guardar(ubication);
            }
        });
        return view;
    }

    private void agregarLocalizacion(View view) {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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
                        latitud = latitude;
                        longitud = longitude;
//                        position = new LatLng(Double.parseDouble(tvLatitud.toString()), Double.parseDouble(tvLongitud.toString()));
//                        Log.i("UBICACION", latitude.toString());
//                        Log.i("UBICACION", longitude.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}