package com.example.smarthome.Registration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.smarthome.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;


public class ForgotPasswordFragment extends Fragment {

    Button btnReset;
    TextView email;
    ProgressBar resetBar;
    public static final Pattern emailPattern = Pattern.compile("^[a-z][a-z0-9_\\.]{5,32}@[a-z0-9]{2,}(\\.[a-z0-9]{2,4}){1,2}$", Pattern.CASE_INSENSITIVE);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnReset = view.findViewById(R.id.btnReset);
        email = view.findViewById(R.id.OTP);
        resetBar = view.findViewById(R.id.otpBar);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailPattern.matcher(email.getText().toString()).find()){
                    email.setError(null);
                    resetBar.setVisibility(View.VISIBLE);
                    btnReset.setEnabled(false);
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                resetBar.setVisibility(View.VISIBLE);
                                Toast.makeText(getContext(), "Email cài đặt lại mật khẩu đả được gởi thành công.", Toast.LENGTH_SHORT).show();
                                getActivity().onBackPressed();
                            }else {
                                email.setError(task.getException().getMessage());
                                resetBar.setVisibility(View.INVISIBLE);
                            }
                            btnReset.setEnabled(true);
                        }
                    });
                }else {
                    Toast.makeText(getContext(), "Hãy nhập đúng giá trị Email.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}