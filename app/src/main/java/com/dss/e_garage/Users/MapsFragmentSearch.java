package com.dss.e_garage.Users;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dss.e_garage.GetLoc;
import com.dss.e_garage.ModelGarage;
import com.dss.e_garage.ModelService;
import com.dss.e_garage.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapsFragmentSearch extends Fragment {
    GetLoc getLoc;
    LatLng userloc;
    List<ModelGarage> garagesList;
    FloatingActionButton fab_curr_loc;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {@Override
        public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        getActivity(), R.raw.mapstyle));

       getLocation(googleMap);
       getGarages(googleMap);
        fab_curr_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleMap.clear();
               getLocation(googleMap);
            }
        });

    }
    };

    private void getGarages(GoogleMap googleMap) {
        garagesList=new ArrayList<>();

        DatabaseReference dref=FirebaseDatabase.getInstance().getReference("Garages");
        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds:snapshot.getChildren()) {
                        Log.e("latlang", "" + ""+ds.child("glatlang").getValue());
                        ModelGarage mg = ds.getValue(ModelGarage.class);
                        garagesList.add(mg);
                        String[] latlong = mg.getGlatlang().split(",");
                        double latitude = Double.parseDouble(latlong[0]);
                        double longitude = Double.parseDouble(latlong[1]);
                        LatLng loc = new LatLng(latitude, longitude);
                        MarkerOptions marker = new MarkerOptions().position(loc)
                                //.icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(getActivity(),R.drawable.ic_baseline_local_gas_station_24)))

                                .title(mg.getGname());
                        Marker m = googleMap.addMarker(marker);
                        m.setTag(mg.getGmob());
                        m.showInfoWindow();

                        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(@NonNull Marker marker) {
                                if (marker.getTag() != null) {
                                    showBottomsheetGarage(marker.getTag().toString());
                                }
                                return false;
                            }
                        });
                    }
                }
                else {
                    Toast.makeText(getActivity(), "No garages avilable", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showBottomsheetGarage(String id) {
        BottomSheetDialog bsd=new BottomSheetDialog(getActivity());
        bsd.setContentView(R.layout.bottomsheet_garage);
        for(ModelGarage mg :garagesList){
            if(mg.getGmob().equals(id)){
                ImageView iv_pp;
                TextView tv_name;
                FloatingActionButton fab_nav,fab_call;
                Button bt_request;

                iv_pp=bsd.findViewById(R.id.iv_pp);
                tv_name=bsd.findViewById(R.id.tv_gname);
                fab_nav=bsd.findViewById(R.id.fab_nav);
                fab_call=bsd.findViewById(R.id.fab_call);
                bt_request=bsd.findViewById(R.id.bt_requestservice);

                Glide.with(getActivity()).load(mg.getGppurl()).into(iv_pp);
                tv_name.setText(mg.getGname());
                fab_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",mg.getGmob(), null));
                        getActivity().startActivity(intent);
                    }
                });
                fab_nav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String latlan=mg.getGlatlang();
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("geo:0,0?q="+latlan+" (" + mg.getGname() + ")"));
                        getActivity().startActivity(intent);
                    }
                });

                bt_request.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog dialog_place_Serv=new Dialog(getActivity());
                        dialog_place_Serv.setContentView(R.layout.dialog_req_serv);
                        dialog_place_Serv.show();

                        EditText et_vno;
                        Button bt_place_serv;
                        et_vno=dialog_place_Serv.findViewById(R.id.et_vno);
                        bt_place_serv=dialog_place_Serv.findViewById(R.id.bt_place_serv);

                        bt_place_serv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String time = String.valueOf(new Date().getTime());
                                ModelService ms=new ModelService(
                                        time,
                                        time,
                                        mg.getGmob(),
                                        "-",
                                        "In-Progress",
                                        FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),
                                        "-",
                                        "-",
                                        et_vno.getText().toString()
                                        );
                                FirebaseDatabase.getInstance().getReference("Services")
                                        .child(time).setValue(ms).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getActivity(), "Placed", Toast.LENGTH_SHORT).show();
                                        dialog_place_Serv.dismiss();
                                    }
                                });

                            }
                        });

                    }
                });

                bsd.show();
            }
        }
        bsd.show();
    }

    private void getLocation(GoogleMap googleMap) {
        fab_curr_loc.setEnabled(false);
        getLoc = new GetLoc(getActivity(), new GetLoc.OnGotloc() {
            @Override
            public void gotLoc() {
                fab_curr_loc.setEnabled(true);
                LatLng crr_loc = new LatLng(getLoc.Current_lat, getLoc.Current_long);
                Marker m = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(crr_loc).title("Your Location"));
                userloc = m.getPosition();
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(crr_loc, 18));


            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_maps_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fab_curr_loc = view.findViewById(R.id.fab_curr_loc2);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }


    }
}