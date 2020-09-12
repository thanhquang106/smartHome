package com.example.smarthome.Fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.smarthome.MainActivity;
import com.example.smarthome.R;
import com.example.smarthome.Registration.RegisterActivity;
import com.example.smarthome.Registration.UseNameActivity;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    Button Logout;
    TextView user;
    TextView username;
    CircleImageView profile;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        user = view.findViewById(R.id.username);
        Logout = view.findViewById(R.id.logout);
        username = view.findViewById(R.id.username);
        profile = view.findViewById(R.id.profile);

        username.setText(MainActivity.username);
        Glide.with(view).load(MainActivity.url)
                .placeholder(getResources().getDrawable(R.drawable.add_photo))
                .into(profile);

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getContext(), UseNameActivity.class);
                startActivity(intent);
            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), RegisterActivity.class);
                startActivity(intent);


            }
        });
        return view;
    }
}