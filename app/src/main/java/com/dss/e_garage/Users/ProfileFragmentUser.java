package com.dss.e_garage.Users;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dss.e_garage.ModelService;
import com.dss.e_garage.R;
import com.dss.e_garage.UserDataModel;
import com.dss.e_garage.checkPerm;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileFragmentUser extends Fragment {
    ImageView iv_pp;
    TextView tv_name,tv_mob;
    RecyclerView rv_services;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        iv_pp=v.findViewById(R.id.iv_pp);
        tv_name=v.findViewById(R.id.tv_name);
        tv_mob=v.findViewById(R.id.tv_mob);
        rv_services=v.findViewById(R.id.rv_services);
        rv_services.setHasFixedSize(true);
        rv_services.setLayoutManager(new LinearLayoutManager(getActivity()));
        getUserData(new OngetUserData() {
            @Override
            public void getuserdata(UserDataModel userdata) {
                try {
                    Glide.with(getActivity())
                            .load(userdata.getPpurl())
                            .circleCrop()
                            .into(iv_pp);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                tv_name.setText(userdata.getFname()+" "+userdata.getLname());
                tv_mob.setText(userdata.getMob());

                iv_pp.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if(new checkPerm().checkPermissions(getActivity())){

                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            ((Activity)getContext()).startActivityForResult(Intent.createChooser(intent, "Select Picture"), 102);

                        }
                        return false;
                    }
                });
            }
        });

        getServiceData(new OngetServiceData() {
            @Override
            public void getuserdata(List<ModelService> servicelist) {
                if(servicelist!=null) {
                    ServiceAdapter serviceAdapter = new ServiceAdapter(servicelist, getActivity());
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
                        if (String.valueOf(ds.child("userId").getValue())
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
    public void getUserData(OngetUserData ongetUserData) {
        try{
            FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<UserDataModel> userDataModelList = new ArrayList<>();
                    DataSnapshot userdata = snapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser()
                            .getPhoneNumber().toString());

                    UserDataModel userDataModel = new UserDataModel(
                            String.valueOf(userdata.child("fname").getValue()),
                            String.valueOf(userdata.child("lname").getValue()),
                            String.valueOf(userdata.child("mob").getValue()),
                            String.valueOf(userdata.child("ppurl").getValue())

                    );
                    ongetUserData.getuserdata(userDataModel);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OngetUserData{
        void getuserdata(UserDataModel userdata);
    }

    public interface OngetServiceData{
        void getuserdata(List<ModelService> servicelist);
    }
}
