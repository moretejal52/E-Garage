package com.dss.e_garage.Users;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dss.e_garage.OtpEditText;
import com.dss.e_garage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
EditText et_phone;
OtpEditText et_otp;
TextView tv_phone,tv_resend;
Button bt_next,bt_verify;
String phone;
RelativeLayout rl_phone,rl_verify;
boolean resend=false;

    FirebaseAuth mAuth;
    String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pd=new ProgressDialog(LoginActivity.this);
        pd.setTitle("Wait...");
        et_phone=findViewById(R.id.et_phone);
        et_otp=findViewById(R.id.et_otp);
        bt_next=findViewById(R.id.bt_next);
        bt_verify=findViewById(R.id.bt_verify);
        rl_phone=findViewById(R.id.rv_phone);
        rl_verify=findViewById(R.id.rv_verify);
        tv_phone=findViewById(R.id.tv_phone);
        tv_resend=findViewById(R.id.tv_resend);

        mAuth=FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser()!=null){
            pd.show();
            FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).exists()){
                       pd.dismiss();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                    else {
                        pd.dismiss();
                        Signup();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
      mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("auth", "onVerificationCompleted:" + credential);
                et_otp.setText(credential.getSmsCode().toString());
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("Auth", "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("codesent", "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                resend=false;
                bt_verify.setEnabled(true);
                new CountDownTimer(60000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        tv_resend.setText("wait... " + (millisUntilFinished / 1000) + " sec");
                    }

                    public void onFinish() {
                        resend=true;
                        tv_resend.setText("Resend Code");
                    }

                }.start();
            }
        };

        bt_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, et_otp.getText().toString());
                signInWithPhoneAuthCredential(credential);
            }
        });

        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone=et_phone.getText().toString().trim();
                if(phone!=null&&!phone.isEmpty()&&phone.length()==10){
                    String phoneNumber="+91"+et_phone.getText().toString();
                    Log.e("Phone",phoneNumber);
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber(phoneNumber)       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(LoginActivity.this)                 // Activity (for callback binding)
                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                    tv_phone.setText(phone+"  "+"Change.");
                    rl_phone.setVisibility(View.GONE);
                    rl_verify.setVisibility(View.VISIBLE);
                    bt_verify.setEnabled(false);

                }
                else {
                    et_phone.setError("enter valid phone number");
                }
            }
        });

        tv_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_phone.setVisibility(View.VISIBLE);
                rl_verify.setVisibility(View.GONE);
            }
        });

        tv_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(resend){
                    resendVerificationCode(phone,mResendToken);
                }
            }
        });
    }
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Verified", Toast.LENGTH_SHORT).show();
                            bt_verify.setEnabled(false);
                            FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                   if(snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).exists()){
                                       startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                       finish();
                                   }
                                   else {
                                       Signup();
                                   }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            Log.w("signin", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                et_otp.setError("Incorrect OTP");
                            }
                        }
                    }
                });
    }

    private void Signup() {
        TextInputEditText et_fname,et_lname,et_email,et_mob;
        String ppurl="https://www.pngall.com/wp-content/uploads/5/Profile-PNG-Clipart.png";
        Button bt_signup;
        Dialog d_signup=new Dialog(LoginActivity.this);
        d_signup.setContentView(R.layout.dialog_signup_users);
        d_signup.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        d_signup.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        d_signup.show();
        et_fname=d_signup.findViewById(R.id.et_fname);
        et_lname=d_signup.findViewById(R.id.et_lname);
        et_email=d_signup.findViewById(R.id.et_email);
        et_mob=d_signup.findViewById(R.id.et_mob);
        bt_signup=d_signup.findViewById(R.id.bt_signup);
        et_mob.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

        bt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
                HashMap<String,Object> user=new HashMap<>();
                user.put("mob",FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                user.put("fname",et_fname.getText().toString());
                user.put("lname",et_lname.getText().toString());
               // user.put("email",et_email.getText().toString());
                user.put("ppurl",ppurl);

                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                        .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                    }
                });

            }
        });

    }

}