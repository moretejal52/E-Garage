package com.dss.e_garage.Garages;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dss.e_garage.ModelService;
import com.dss.e_garage.R;
import com.dss.e_garage.Users.ProfileFragmentUser;
import com.dss.e_garage.Garages.ServiceAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentServices extends Fragment {
   RecyclerView rv_services;
    ServiceAdapter serviceAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_services_g,container,false);
        rv_services=v.findViewById(R.id.rv_services_g);
        rv_services.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_services.setHasFixedSize(true);
        getServiceData(new OngetServiceData() {
            @Override
            public void getuserdata(List<ModelService> servicelist) {
                if(servicelist!=null) {
                    serviceAdapter = new ServiceAdapter(servicelist, getActivity());
                    rv_services.setAdapter(serviceAdapter);
                }
                else {
                    Toast.makeText(getActivity(), "No service record", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }


    public void getServiceData(OngetServiceData ongetServiceData){
        try {
            List<ModelService> mslist = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference("Services").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (String.valueOf(ds.child("gid").getValue())
                                .equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                            ModelService ms = ds.getValue(ModelService.class);
                            Log.e("msdata",ms.getOrderId());
                            mslist.add(ms);
                        }
                    }
                    ongetServiceData.getuserdata(mslist);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public interface OngetServiceData{
        void getuserdata(List<ModelService> servicelist);
    }
}
