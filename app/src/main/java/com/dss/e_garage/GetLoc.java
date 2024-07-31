package com.dss.e_garage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;

public class GetLoc extends Activity implements LocationListener {
    Context context;
    LocationManager locationManager;
    OnGotloc onGotloc1;
    public double Current_lat, Current_long;
    @SuppressLint("MissingPermission")
    public GetLoc(Context context) {
        this.context=context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(new checkPerm().checkPermissions((Activity) context)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }
    @SuppressLint("MissingPermission")
    public GetLoc(Context context, OnGotloc onGotloc){
        this.context=context;
        this.onGotloc1=onGotloc;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(new checkPerm().checkPermissions((Activity) context)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(onGotloc1!=null && Current_long==0){
            this.Current_lat=location.getLatitude();
            this.Current_long=location.getLongitude();
            onGotloc1.gotLoc();
        }
        this.Current_lat=location.getLatitude();
        this.Current_long=location.getLongitude();
        Log.e("loc",Current_lat+"====="+Current_long);
        removelocupdates();
    }


    public void removelocupdates(){
        if(locationManager!=null) {
            locationManager.removeUpdates(this);
            locationManager = null;
        }
    }
    public interface OnGotloc{
        public void gotLoc();
    }
}
