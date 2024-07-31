package com.dss.e_garage.Users;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.dss.e_garage.Common;
import com.dss.e_garage.Garages.MainActivityG;
import com.dss.e_garage.R;
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
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
BottomNavigationView bot_nav;
FragmentContainerView fragmentContainerView;
ProgressDialog pd;
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pd=new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setTitle("Wait...");
        bot_nav=findViewById(R.id.bot_nav);
        loadfrag(new MapsFragmentSearch());
        bot_nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getTitle().toString()){
                    case "Map":
                        loadfrag(new MapsFragmentSearch());
                        return true;
                    case "Profile":
                        loadfrag(new ProfileFragmentUser());
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
        pd.show();
        if(requestCode==102&&resultCode==RESULT_OK) {
            try{
                StorageReference sref = FirebaseStorage.getInstance().getReference(FirebaseAuth.getInstance()
                        .getCurrentUser().getPhoneNumber()+".png");

                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Log.e("urlpp",""+data.getData().getPath());
                sref.putStream(inputStream).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        sref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                pd.dismiss();
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                                        .child("ppurl").setValue(String.valueOf(uri));
                                Toast.makeText(MainActivity.this, "Photo Updated", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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