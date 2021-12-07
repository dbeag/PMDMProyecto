package com.example.proyecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.proyecto.adapter.MainAdapter;
import com.example.proyecto.model.Ubicacion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPager2;
    TabLayout tabLayout;
    MainAdapter adapter;
    static String email;
    static FirebaseFirestore db;

    public static String getEmail() {
        return email;
    }
    public static ArrayList<Ubicacion> lstUbicaciones = new ArrayList<>();

    public static void guardar(Ubicacion ubication) {
        borrarListas();
        lstUbicaciones.add(ubication);
        Map<String, String> ubicaciones = new HashMap<>();
        String ubicacion = jsonEncode(lstUbicaciones);
        ubicaciones.put("ubicacion", ubicacion);
        db.collection("ubicacion").document(email).set(ubicaciones);
        MapsFragment.actualizarTodo();
    }

    private static void jsonDecode(String json) {
        lstUbicaciones.clear();
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Ubicacion>>(){}.getType();
        ArrayList<Ubicacion> lstUbicacionesAux = gson.fromJson(json, type);
        if (lstUbicacionesAux != null) {
            for (Ubicacion ubicacion : lstUbicacionesAux){
                if (ubicacion.getEmail().equals(email)){
                    lstUbicaciones.add(ubicacion);
                }
            }
        }
        MapsFragment.actualizarTodo();
    }

    private static String jsonEncode(ArrayList<Ubicacion> lstUbicaciones) {
        Gson gson = new Gson();
        String json = gson.toJson(lstUbicaciones);
        Log.i("LOGJSON", "jsonEncode: " + json);
        return json;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        obtenerLista();

        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager2);

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_map));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_location));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_list));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_person));

        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new MainAdapter(fragmentManager, getLifecycle());
        viewPager2.setAdapter(adapter);
        viewPager2.setUserInputEnabled(false);

        Bundle datos = this.getIntent().getExtras();
        email = datos.getString("email");
        SharedPreferences sesion = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor Obj_editor = sesion.edit();
        Obj_editor.putString("email", email);
        Obj_editor.apply();
        Obj_editor.commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }

    private void obtenerLista() {

        db.collection("ubicacion").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        String json = document.getString("ubicacion");
                        jsonDecode(json);
                    }
                } else {
                    Log.e("Ubicaciones descargadas", "No se ha encontrado ubicaciones");
                }
            }
        });
        MapsFragment.actualizarTodo();
    }

    private static void borrarListas(){
        db.collection("ubicacion").document(email).delete();
    }
}