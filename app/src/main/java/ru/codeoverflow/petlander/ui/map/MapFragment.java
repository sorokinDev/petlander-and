package ru.codeoverflow.petlander.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import ru.codeoverflow.petlander.MainActivity;
import ru.codeoverflow.petlander.ui.Cards.Cards;
import ru.codeoverflow.petlander.ui.base.BaseFragment;
import ru.codeoverflow.petlander.R;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class MapFragment extends BaseFragment implements OnMapReadyCallback,LocationSource.OnLocationChangedListener {

    private GoogleMap mMap;
    private MapView mapView;
    protected Long geoX;
    protected Long geoY;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Double latitude;
    private Double longitude;


    public MapFragment() {

    }


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_map;
    }


    @Override
    protected void onSetupView(View rootView, Bundle saved) {
        ((AppCompatActivity) getActivity()).setSupportActionBar(rootView.findViewById(R.id.toolbar));
        locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if(location != null){
            onLocationChanged(location);
            Toast.makeText(getContext(), "Too", Toast.LENGTH_SHORT).show();
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Toast.makeText(getContext(), "Hie", Toast.LENGTH_SHORT).show();
                latitude= location.getLatitude();
                longitude=location.getLongitude();

                LatLng loc = new LatLng(latitude, longitude);


                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Toast.makeText(getContext(), "onStatusChanged", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(getContext(), "Hir", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(getContext(), "Provider disabled", Toast.LENGTH_SHORT).show();
            }
        };
        locationManager.requestLocationUpdates(bestProvider, 2000, 0,locationListener );

        mapView = (MapView) rootView.findViewById(R.id.map);
        mapView.onCreate(saved);
        mapView.onResume();
        mapView.getMapAsync(this);


    }






    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude= location.getLatitude();
        longitude=location.getLongitude();

        LatLng loc = new LatLng(latitude, longitude);

        Toast.makeText(getContext(), "Hi", Toast.LENGTH_SHORT).show();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users");
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
                                         @Override
                                         public void onDataChange(DataSnapshot dataSnapshot) {
                                                 if (dataSnapshot.exists()) {
                                                     for (DataSnapshot data : dataSnapshot.getChildren()) {


                                                         geoX = (Long) data.child("geoX").getValue();
                                                         geoY = (Long) data.child("geoY").getValue();

                                                         LatLng position = new LatLng(geoX, geoY);


                                                         mMap.addMarker(new MarkerOptions().position(position).title("Marker in Sydney"));
                                                         Log.e("MAP", "OK");
                                                 }
                                             }
                                         }

                                         @Override
                                         public void onCancelled(@NonNull DatabaseError databaseError) { }
                                     }
        );

        requestLocationPermission();






    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(1)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if(EasyPermissions.hasPermissions(getContext(), perms)) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    return false;
                }
            });
        }
        else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", 1, perms);
        }
    }


}
