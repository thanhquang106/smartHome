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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.regex.Pattern;


public class CreateAccountFragment extends Fragment {

    TextView txtLoginUser;
    TextView email, password, phone, cnfpassword, username;
    Button loginBtn;
    ProgressBar createbar;
    public static final Pattern emailPattern = Pattern.compile("^[a-z][a-z0-9_\\.]{5,32}@[a-z0-9]{2,}(\\.[a-z0-9]{2,4}){1,2}$", Pattern.CASE_INSENSITIVE);
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        firebaseAuth = FirebaseAuth.getInstance();

        txtLoginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_login);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check();
            }
        });

    }

    private void Check(){
        email.setError(null);
        phone.setError(null);
        password.setError(null);
        cnfpassword.setError(null);
        if (email.getText().toString().isEmpty()){
            email.setError("Trống.");
            return;
        }
        if (phone.getText().toString().isEmpty()){
            phone.setError("Trống.");
            return;
        }
        if (password.getText().toString().isEmpty()){
            password.setError("Trống.");
            return;
        }
        if (cnfpassword.getText().toString().isEmpty()){
            cnfpassword.setError("Trống.");
            return;
        }
        if (!emailPattern.matcher(email.getText().toString()).find()){
            email.setError("Hãy nhập đúng giá trị email.");
            return;
        }
        if (phone.getText().toString().length() <9){
            phone.setError("Số điện thoại không đúng.");
            return;
        }
        if (password.getText().toString().length() <8){
            password.setError("Mật khẩu phải từ 8 ký tự.");
        }
        if (!password.getText().toString().equals(cnfpassword.getText().toString())){
            cnfpassword.setError("Mật khẩu xác nhận không phù hợp.");
            return;
        }
        CreateAccount();
    }

    private void CreateAccount(){
        createbar.setVisibility(View.VISIBLE);
        firebaseAuth.fetchSignInMethodsForEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()){
                    if (task.getResult().getSignInMethods().isEmpty()){
                        Bundle bundle = new Bundle();
                        bundle.putString("name", username.getText().toString());
                        bundle.putString("email", email.getText().toString());
                        bundle.putString("phone", phone.getText().toString());
                        bundle.putString("password" , password.getText().toString());
                        Navigation.findNavController(getView()).navigate(R.id.action_otp, bundle);
                    }else {
                        email.setError("Tài khoản không tồn tại");
                        createbar.setVisibility(View.INVISIBLE);
                    }
                }else {
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                createbar.setVisibility(View.GONE);
            }
        });
    }

    private void init (View view){

        username = view.findViewById(R.id.RegisterinputName);
        email = view.findViewById(R.id.RegisterinputEmail);
        phone = view.findViewById(R.id.RegisterinputPhone);
        password = view.findViewById(R.id.Registerinputpassword);
        cnfpassword = view.findViewById(R.id.RegisterinputConfirmpassword);
        loginBtn = view.findViewById(R.id.Register);
        createbar = view.findViewById(R.id.ProgressBar);
        txtLoginUser = view.findViewById(R.id.loginuser);
    }
}