package com.example.smarthome.Fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.smarthome.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


public class HomeFragment extends Fragment  {


    private DatabaseReference firebaseDatabase ;
    private DatabaseReference Tmp, Humi , Led_home,Door_home,lpg,CO,SMOKE;
    private TextView Temp,Humidity,date,Gas,Co,Smoke;
    private Long reference1 ,reference2 ,reference3,reference4,reference5;
    private FirebaseFirestore firebaseFirestore;
    ImageButton Door, LedOff, Mic,video;
    private int led, door;
    private static final int REQUEST_CODE =1000;
    private LinearLayout lpgLinear,coLinear,smokeLinear;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Temp = view.findViewById(R.id.temp);
        Humidity = view.findViewById(R.id.humidity);
        date = view.findViewById(R.id.date);
        Gas = view.findViewById(R.id.gas);
        Co= view.findViewById(R.id.co);
        Smoke = view.findViewById(R.id.smoke);
        lpgLinear = view.findViewById(R.id.lpgLinear);
        coLinear = view.findViewById(R.id.colinear);
        smokeLinear = view.findViewById(R.id.smokeLinear);
        video= view.findViewById(R.id.video);


        //button
        LedOff = view.findViewById(R.id.led_off);
        Door = view.findViewById(R.id.door);
        Mic = view.findViewById(R.id.mic);



       firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        Tmp = firebaseDatabase.child("Temp");
        Humi = firebaseDatabase.child("Humidity");
        Led_home = firebaseDatabase.child("light");
        Door_home = firebaseDatabase.child("dr");
        lpg = firebaseDatabase.child("LPG");
        CO = firebaseDatabase.child("CO");
        SMOKE = firebaseDatabase.child("smoke");


        Mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak();
            }
        });
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.youtube.com/watch?v=fTXd-DpN3AI&ab_channel=Qu%C3%A2nA.POfficial";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        LedOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (led ==0){
                    LedOff.setImageDrawable(getResources().getDrawable(R.drawable.lightbulb));
                    led = 1;
                    Led_home.setValue(1);
                }else {
                    LedOff.setImageDrawable(getResources().getDrawable(R.drawable.gear));
                    led = 0;
                    Led_home.setValue(0);
                }
            }
        });
        Door.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (door ==0){
                    Door.setImageDrawable(getResources().getDrawable(R.drawable.cdoor));
                    door = 1;
                    Door_home.setValue(1);
                }else {
                    Door.setImageDrawable(getResources().getDrawable(R.drawable.door));
                    door = 0;
                    Door_home.setValue(0);
                }
            }
        });


//set date
    firebaseFirestore = FirebaseFirestore.getInstance();
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        date.setText(currentDate);
        HashMap<String, Object> map = new HashMap<>();
        map.put("temp", reference1 );
//       map.put("temp",temp);
        firebaseFirestore.collection("dht11").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                }else {

                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Tmp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reference1  = snapshot.getValue(Long.class);
                Temp.setText("+"+String.valueOf(reference1 )+"°C");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Humi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                reference2 = snapshot.getValue(Long.class);
                Humidity.setText(String.valueOf(reference2)+"%");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        lpg.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                reference3 = snapshot.getValue(Long.class);
                Gas.setText(String.valueOf(reference3));
                if (reference3 >=200){
                    lpgLinear.setBackgroundColor(Color.parseColor("#FF0000"));
                }else {

                    lpgLinear.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        CO.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                reference4 = snapshot.getValue(Long.class);
                Co.setText(String.valueOf(reference4));
                if (reference4 >= 1000){
                    coLinear.setBackgroundColor(Color.parseColor("#FF0000"));
                }else {

                    coLinear.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        SMOKE.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reference5 = snapshot.getValue(Long.class);
                Smoke.setText(String.valueOf(reference5));
                if (reference5 >= 200){
                    smokeLinear.setBackgroundColor(Color.parseColor("#FF0000"));

                }else {
                    smokeLinear.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Led_home.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                led = snapshot.getValue(Integer.class);

                if (led == 0) {
                    LedOff.setImageDrawable(getResources().getDrawable(R.drawable.gear));
                }else if(led == 1) {
                    LedOff.setImageDrawable(getResources().getDrawable(R.drawable.lightbulb));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        Door_home.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 door= snapshot.getValue(Integer.class);

                if (door == 0) {
                    Door.setImageDrawable(getResources().getDrawable(R.drawable.door));
                }else if(led == 1) {
                    Door.setImageDrawable(getResources().getDrawable(R.drawable.cdoor));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        Tmp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reference1  = snapshot.getValue(Long.class);
                Temp.setText(String.valueOf(reference1 )+"°C");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Humi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                reference2 = snapshot.getValue(Long.class);
                Humidity.setText(String.valueOf(reference2)+"%");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        lpg.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                reference3 = snapshot.getValue(Long.class);
                Gas.setText(String.valueOf(reference3));
                if (reference3 >=200){
                    lpgLinear.setBackgroundColor(Color.parseColor("#FF0000"));
                }else {

                    lpgLinear.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        CO.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                reference4 = snapshot.getValue(Long.class);
                Co.setText(String.valueOf(reference4));
                if (reference4 >= 1000){
                    coLinear.setBackgroundColor(Color.parseColor("#FF0000"));
                }else {

                    coLinear.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        SMOKE.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reference5 = snapshot.getValue(Long.class);
                Smoke.setText(String.valueOf(reference5));
                if (reference5 >= 200){
                    smokeLinear.setBackgroundColor(Color.parseColor("#FF0000"));

                }else {
                    smokeLinear.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Led_home.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                led = snapshot.getValue(Integer.class);
                if (led == 0) {
                    LedOff.setImageDrawable(getResources().getDrawable(R.drawable.gear));
                }else if(led == 1) {
                    LedOff.setImageDrawable(getResources().getDrawable(R.drawable.lightbulb));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        Door_home.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                door = snapshot.getValue(Integer.class);
                if (door == 0) {
                    Door.setImageDrawable(getResources().getDrawable(R.drawable.door));
                }else if(door == 1) {
                    Door.setImageDrawable(getResources().getDrawable(R.drawable.cdoor));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
    //mic

    private void speak(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL ,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening...");

        try{
            startActivityForResult(intent,REQUEST_CODE);
        }
        catch (Exception e){
            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE:{
                if(requestCode != RESULT_OK && null != data){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);


                    String txtvoice = result.get(0)+"";


                    if (txtvoice.indexOf("bật đèn")!=-1){
                        Led_home.setValue(1);
                    }else if (txtvoice.indexOf("Tắt Đèn")!=-1){
                        Led_home.setValue(0);
                    }
                     else if (txtvoice.indexOf("mở cửa")!=-1){
                        Door_home.setValue(1);
                    }else if (txtvoice.indexOf("đóng cửa")!=-1){
                        Door_home.setValue(0);
                    }else {
                        Toast.makeText(getContext(), "Không đúng yêu cầu !", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
    }

}