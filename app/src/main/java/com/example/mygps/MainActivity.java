package com.example.mygps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapview;
    private GoogleMap gmap;
    private static final String MAP_VIEW_BUNDLE_KEY="MapViewBundleKey";
    private MarkerOptions markeroptions;
    private Marker marcador;
    private TextView txlat, txlong, txalt;
    private ImageButton ibinicar, ibpausar, ibretomar, ibfinalizar;
    private List<Coordenadas> coordenadas = new ArrayList<>();
    private LocationManager locationmanager;
    private LocationListener locationlistener;
    private double metros;
    private PermissionsMarshmallow permissionsMarshmallow = new PermissionsMarshmallow(this);
    private String[] PERMISSIONS = { Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapview = findViewById(R.id.mapView);
        mapview.setClickable(true);
        markeroptions = new MarkerOptions();
        Bundle mapviewbundle = null;
        txlat = findViewById(R.id.txlat);
        txlong = findViewById(R.id.txlong);
        txalt = findViewById(R.id.txalt);
        ibinicar = findViewById(R.id.ibIniciar);
        ibpausar = findViewById(R.id.ibPausar);
        ibretomar = findViewById(R.id.ibRetomar);
        ibfinalizar = findViewById(R.id.ibFinalizar);

        //desabilitando botões
        ibfinalizar.setEnabled(false);
        ibretomar.setEnabled(false);
        ibpausar.setEnabled(false);
        ibpausar.setBackgroundColor(3);
        ibretomar.setBackgroundColor(3);
        ibfinalizar.setBackgroundColor(3);
        //ibfinalizar.setBackgroundColor(0xFFFFFFFF);

        CheckPermissionGranted();
        locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationlistener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) // quando houve mudança na localização
            {
               coordenadas.add(new Coordenadas(location.getLatitude(), location.getLongitude()));
                mostrarLocalizacao(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        iniciarLocalizacao();


        if(savedInstanceState!=null)
        {
            mapviewbundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapview.onCreate(mapviewbundle);
        mapview.getMapAsync(this);


        ibinicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Iniciando monitoramento...",Toast.LENGTH_SHORT).show();
                onStart();
               // coordenadas = null;
                ibinicar.setEnabled(false);
                ibfinalizar.setEnabled(true);
                ibpausar.setEnabled(true);
                ibinicar.setBackgroundColor(0);
                ibretomar.setBackgroundColor(3);
                ibfinalizar.setBackgroundColor(0xFFFFFFFF);
                ibpausar.setBackgroundColor(0xFFFFFFFF);
            }
        });

        ibpausar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Pausou o monitoramento",Toast.LENGTH_SHORT).show();
                onStop();
                //ibinicar.setEnabled(false);
                ibpausar.setEnabled(false);
                ibretomar.setEnabled(true);
                ibpausar.setBackgroundColor(3);
                ibretomar.setBackgroundColor(0xFF00FF0A);
                ibfinalizar.setBackgroundColor(0xFF00FF0A);
            }
        });

        ibretomar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Retomando o monitoramento...",Toast.LENGTH_SHORT).show();
                onResume();
                //ibinicar.setEnabled(false);
                ibpausar.setEnabled(true);
                ibpausar.setBackgroundColor(0xFFFFFFFF);
                ibretomar.setBackgroundColor(3);
               // ibpausar.setBackgroundColor(0xFFFFFFFF);
               // ibfinalizar.setBackgroundColor(0xFF00FF0A);

            }
        });

        ibfinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parouMonitorar();
                Intent intent=new Intent(MainActivity.this, ResultadoActivity.class);
                intent.putExtra("metros",String.format("%10.2f",metros));
                startActivity(intent);

            }
        });

    }
    private void CheckPermissionGranted() {
        if (permissionsMarshmallow.hasPermissions(PERMISSIONS)) {
            //  permission granted
        } else {
            // request permission
            permissionsMarshmallow.requestPermissions(PERMISSIONS, 2);
        }
    }

    private void iniciarLocalizacao() {
        locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,locationlistener);
    }

    private void mostrarLocalizacao(Location location) {
        if(location != null){
            if(marcador != null)
            {
                marcador.remove();
            }

            txlat.setText(""+location.getLatitude());
            txlong.setText(""+location.getLongitude());
            txalt.setText(""+location.getAltitude());

            //apontador
            LatLng latlng = new LatLng(location.getLatitude(),location.getLongitude());
            markeroptions.position(latlng);
            marcador = gmap.addMarker(markeroptions);
            gmap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapview.onSaveInstanceState(mapViewBundle);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap=googleMap;
        gmap.setMinZoomPreference(12);
        gmap.setIndoorEnabled(true);
        UiSettings ponto = gmap.getUiSettings();
        ponto.setIndoorLevelPickerEnabled(true);
        ponto.setMyLocationButtonEnabled(true);
        ponto.setMapToolbarEnabled(true);
        ponto.setCompassEnabled(true);
        ponto.setZoomControlsEnabled(true);
    }

    private double calculaDistancia(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371;// km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;
        return dist * 1000; //em metros
    }

    private void parouMonitorar(){
        locationmanager.removeUpdates(locationlistener);

        int i = 0;
        while(i < coordenadas.size() - 1){

            metros += calculaDistancia(coordenadas.get(i).getLatitude(), coordenadas.get(i).getLongitude(),
                    coordenadas.get(i+1).getLatitude(), coordenadas.get(i+1).getLongitude());
            i++;
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        mapview.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapview.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapview.onStop();
    }
    @Override
    protected void onDestroy() {
        mapview.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapview.onLowMemory();
    }
}
