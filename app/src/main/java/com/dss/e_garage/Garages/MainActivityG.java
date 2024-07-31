package com.dss.e_garage.Garages;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.dss.e_garage.Common;
import com.dss.e_garage.R;
import com.dss.e_garage.Users.MainActivity;
import com.dss.e_garage.Users.MapsFragmentSearch;
import com.dss.e_garage.Users.ProfileFragmentUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

public class MainActivityG extends AppCompatActivity {
   public static ProgressDialog pd;
BottomNavigationView bot_nav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_g);
        pd=new ProgressDialog(MainActivityG.this);
        pd.setTitle("Wait...");
        pd.setCancelable(false);
        bot_nav=findViewById(R.id.bot_nav_g);
        loadfrag(new FragmentServices());
        bot_nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getTitle().toString()){
                    case "Services":
                        loadfrag(new FragmentServices());
                        return true;
                    case "Profile":
                        loadfrag(new ProfileFragmentGarage());
                        return true;
                }
                return false;
            }
        });
    }
    public void loadfrag(Fragment fragment){

        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        ft.replace(R.id.frame,fragment);
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101&&resultCode==RESULT_OK) {
            try{
            StorageReference sref = FirebaseStorage.getInstance().getReference(Common.billid+".png");

            InputStream inputStream = getContentResolver().openInputStream(data.getData());
            sref.putStream(inputStream).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    sref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            pd.dismiss();
                            Common.billurl = String.valueOf(uri);
                            Toast.makeText(MainActivityG.this, "Bill Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(MainActivityG.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (Exception e) {
                e.printStackTrace();
            }
        }else
        if(requestCode==102&&resultCode==RESULT_OK) {
            try{
                StorageReference sref = FirebaseStorage.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()+"_G.png");

                InputStream inputStream = getContentResolver().openInputStream(data.getData());

                sref.putStream(inputStream).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        sref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                pd.dismiss();
                                FirebaseDatabase.getInstance().getReference("Garages").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                                        .child("gppurl").setValue(String.valueOf(uri));
                                Toast.makeText(MainActivityG.this, "Photo Updated", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(MainActivityG.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            pd.dismiss();
        }
    }
}