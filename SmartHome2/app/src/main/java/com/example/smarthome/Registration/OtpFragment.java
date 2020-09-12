package com.example.smarthome.Registration;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthome.MainActivity;
import com.example.smarthome.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class OtpFragment extends Fragment {

    public OtpFragment() {
    }

    public OtpFragment(String email, String phone, String password, String username) {
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.username = username;
    }

    private TextView txtPhone;
    private EditText OTP;
    private Button XNbtn, ResendBtn;
    private ProgressBar XNbar;
    private String email, phone, password, username;
    Timer timer;
    private int count = 60;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_otp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        firebaseAuth = FirebaseAuth.getInstance();

        email = getArguments().getString("email");
        phone = getArguments().getString("phone");
        password = getArguments().getString("password");
        username = getArguments().getString("name");

        Toast.makeText(getContext(), phone, Toast.LENGTH_SHORT).show();

        txtPhone.setText("Mã xác nhận được gửi tới số +84"+phone);

        sendOTP();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (count == 0){
                            ResendBtn.setText("Gửi lại");
                            ResendBtn.setEnabled(true);
                            ResendBtn.setAlpha(1);
                        }else {
                            ResendBtn.setText("Gửi lại trong "+count);
                            --count;
                        }
                    }
                });
            }
        },0,1000);

        ResendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resenOTP();
                ResendBtn.setEnabled(false);
                ResendBtn.setAlpha(0.5f);
                count = 60;
            }
        });

        XNbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (OTP.getText().toString() == null || OTP.getText().toString().isEmpty()){
                    return;
                }
                OTP.setError(null);
                String code = OTP.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                signInWithPhoneAuthCredential(credential);
                XNbar.setVisibility(View.VISIBLE);
            }
        });

    }

    private void init (View view){
        OTP = view.findViewById(R.id.OTP);
        XNbar = view.findViewById(R.id.otpBar);
        XNbtn = view.findViewById(R.id.btnXacNhan);
        txtPhone = view.findViewById(R.id.txt_phone);
        ResendBtn = view.findViewById(R.id.btnResend);
    }

    private void sendOTP(){
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                mVerificationId = verificationId;
                mResendToken = token;

            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                String smsCode = credential.getSmsCode();
                if (smsCode!= null){
                    PhoneAuthCredential credential1 = PhoneAuthProvider.getCredential(mVerificationId, smsCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    OTP.setText(e.getMessage());
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    OTP.setText(e.getMessage());
                }
                XNbar.setVisibility(View.INVISIBLE);
            }

        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+84" + phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallback);
        // OnVerificationStateChangedCallbacks

    }



    private void resenOTP(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+84" + phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallback, mResendToken);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                            user.linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){

                                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                                        Map<String, Object> map = new HashMap<>();
                                        map.put("email", email);
                                        map.put("phone", phone);
                                        map.put("username", username);

                                        firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Intent usernameIntent = new Intent(getContext(), MainActivity.class);
                                                    usernameIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(usernameIntent);
                                                    getActivity().finish();
                                                }else {
                                                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    XNbar.setVisibility(View.INVISIBLE);
                                                }
                                            }
                                        });
                                    }else {
                                        Toast.makeText(getContext(),  task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        XNbar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                OTP.setError("OTP không hợp lệ");
                            }
                            XNbar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}