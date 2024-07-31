package com.dss.e_garage.Garages;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dss.e_garage.ModelGarage;
import com.dss.e_garage.ModelService;
import com.dss.e_garage.R;
import com.dss.e_garage.UserDataModel;
import com.dss.e_garage.Users.ServiceAdapter;
import com.dss.e_garage.checkPerm;
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

public class ProfileFragmentGarage extends Fragment {
    ImageView iv_pp;
    TextView tv_name,tv_mob;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_g, container, false);

        iv_pp=v.findViewById(R.id.iv_pp);
        tv_name=v.findViewById(R.id.tv_name);
        tv_mob=v.findViewById(R.id.tv_mob);
        getGData(new OngetUserData() {
            @Override
            public void getuserdata(ModelGarage userdata) {
                try {
                    Glide.with(getActivity())
                            .load(userdata.getGppurl())
                            .into(iv_pp);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                tv_name.setText(userdata.getGname());
                tv_mob.setText(userdata.getGmob());

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


        return v;
    }

    public void getGData(OngetUserData ongetUserData) {
        try{
            FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DataSnapshot userdata = snapshot.child("Garages").child(FirebaseAuth.getInstance().getCurrentUser()
                            .getPhoneNumber().toString());
                    ModelGarage mg=userdata.getValue(ModelGarage.class);
                    ongetUserData.getuserdata(mg);

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
        void getuserdata(ModelGarage userdata);
    }

}
