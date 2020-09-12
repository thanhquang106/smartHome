package com.example.smarthome.Registration;

import android.content.Intent;
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

import com.example.smarthome.MainActivity;
import com.example.smarthome.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.regex.Pattern;


public class LoginFragment extends Fragment {

    TextView txtCreate, txtForgot;
    TextView EmailOrPhone, password;
    Button loginBtn;
    ProgressBar loginBar;
    public static final Pattern emailPattern = Pattern.compile("^[a-z][a-z0-9_\\.]{5,32}@[a-z0-9]{2,}(\\.[a-z0-9]{2,4}){1,2}$", Pattern.CASE_INSENSITIVE);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtCreate = view.findViewById(R.id.registerCreate);
        txtForgot = view.findViewById(R.id.textView2);
        EmailOrPhone = view.findViewById(R.id.inputEmail);
        password = view.findViewById(R.id.inputpassword);
        loginBar = view.findViewById(R.id.ProgressBar);
        loginBtn = view.findViewById(R.id.login);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmailOrPhone.setError(null);
                password.setError(null);
                if (EmailOrPhone.getText().toString().isEmpty()){
                    EmailOrPhone.setError("Trống");
                    return;
                }
                if (password.getText().toString().isEmpty()){
                    password.setError("Trống");
                    return;
                }
                if (emailPattern.matcher(EmailOrPhone.getText().toString()).find()){
                    loginBar.setVisibility(View.VISIBLE);
                    Login(EmailOrPhone.getText().toString());
                }else if (EmailOrPhone.getText().toString().matches("\\d{9}")){
                    loginBar.setVisibility(View.VISIBLE);
                    FirebaseFirestore.getInstance().collection("users").whereEqualTo("phone", EmailOrPhone.getText().toString())
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                List<DocumentSnapshot> document = task.getResult().getDocuments();
                                if (document.isEmpty()){
                                    EmailOrPhone.setError("Không tìm thấy số điện thoại");
                                    loginBar.setVisibility(View.INVISIBLE);
                                    return;
                                }else {
                                    String email = document.get(0).get("email").toString();
                                    Login(email);
                                }
                            }else {
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    EmailOrPhone.setError("Hãy nhập đúng giá email hoặc số điện thoại");
                }
            }
        });

        txtCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_craete_account);
            }
        });

        txtForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_forgot_password);
            }
        });

    }

    private void Login(String email){
        FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                    startActivity(mainIntent);
                    getActivity().finish();
                }else {
                    Toast.makeText(getActivity(), "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                }
                loginBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}