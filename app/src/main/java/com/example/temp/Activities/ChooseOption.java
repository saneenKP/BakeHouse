package com.example.temp.Activities;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.example.temp.CheckInternet;
import com.example.temp.Models.OrderDetails;
import com.example.temp.R;
import com.example.temp.SharedPreferenceConfig;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseOption extends AppCompatActivity {

    private MaterialButton food,services;
    private Boolean OrderStatus = true;
    private RelativeLayout root;
    private SharedPreferenceConfig sharedPreferenceConfig;
    private TextView orderStatus;
    private RelativeLayout layout;
    private Snackbar snackbar;
    private CheckInternet checkInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_choose_option);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        checkInternet = new CheckInternet(getApplicationContext());

        food = findViewById(R.id.btn_food);
        services = findViewById(R.id.btn_services);
        sharedPreferenceConfig = new SharedPreferenceConfig(getApplicationContext());
        root = findViewById(R.id.root);

        layout = findViewById(R.id.orderProgressLayout);
        orderStatus = findViewById(R.id.orderStatus);
        layout.setVisibility(View.GONE);

        if (!sharedPreferenceConfig.readOrderId().equals("")){
            showOrderProgress();
        }


        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent startOrderStatus = new Intent(ChooseOption.this , OrderStatus.class);
                startActivity(startOrderStatus);

            }
        });

    }

    private void showOrderProgress(){

        DatabaseReference orderStatusReference = FirebaseDatabase.getInstance().getReference().child(getApplicationContext().getResources().getString(R.string.OrderNode)).child(sharedPreferenceConfig.readOrderId());

        orderStatusReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String placedStatus =  snapshot.child(getApplicationContext().getResources().getString(R.string.placedIndexStatus)).getValue(String.class);
                String pickedStatus =  snapshot.child(getApplicationContext().getResources().getString(R.string.pickupIndexStatus)).getValue(String.class);
                String deliveredStatus =  snapshot.child(getApplicationContext().getResources().getString(R.string.deliveryIndexStatus)).getValue(String.class);

                if (placedStatus.equals("no") && pickedStatus.equals("no") && deliveredStatus.equals("no")){
                    layout.setVisibility(View.VISIBLE);
                    orderStatus.setText("Order Under Progress");
                }
                if (placedStatus.equals("yes")){
                    orderStatus.setText("");
                    layout.setVisibility(View.VISIBLE);
                    orderStatus.setText("Placed");
                }
                if (pickedStatus.equals("yes")){
                    orderStatus.setText("");
                    layout.setVisibility(View.VISIBLE);
                    orderStatus.setText("Picked");
                }
                if (deliveredStatus.equals("yes")){
                    orderStatus.setText("");
                    orderStatus.setText("Delivered");
                    sharedPreferenceConfig.removeOrderId();
                    layout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    @Override
    protected void onStart() {
        super.onStart();

        if (checkInternet.isNetworkConnected()){
            if (checkInternet.internetIsConnected()){
                if (!sharedPreferenceConfig.readOrderId().equals(""))
                    showOrderProgress();
            }else
                snackbar.make(layout , checkInternet.getNoNetworkConnectionError() , Snackbar.LENGTH_LONG).show();
        }else {
            snackbar.make(layout , checkInternet.getInternetNotSwitchedOnError() , Snackbar.LENGTH_LONG).show();
        }



        food.setOnClickListener(v -> {

            if (checkInternet.isNetworkConnected()){

                if (checkInternet.internetIsConnected()){
                    Intent openHotelSection = new Intent(ChooseOption.this , Hotels.class);
                    startActivity(openHotelSection);
                }else{
                    snackbar.make(layout , checkInternet.getNoNetworkConnectionError() , Snackbar.LENGTH_LONG).show();
                }

            }else{
                snackbar.make(layout , checkInternet.getInternetNotSwitchedOnError() , Snackbar.LENGTH_LONG).show();
            }

        });
        services.setOnClickListener(v -> {

            if (checkInternet.isNetworkConnected()){

                if (checkInternet.internetIsConnected()){
                    Intent openServicesSection = new Intent(ChooseOption.this , Services.class);
                    startActivity(openServicesSection);
                }else{
                    snackbar.make(layout , checkInternet.getNoNetworkConnectionError() , Snackbar.LENGTH_LONG).show();
                }
            }else{
                snackbar.make(layout , checkInternet.getInternetNotSwitchedOnError() , Snackbar.LENGTH_LONG).show();
            }

        });
    }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId())
            {
                case R.id.logout_menu:

                    if (checkInternet.isNetworkConnected()){
                        if (checkInternet.internetIsConnected()){

                            SharedPreferenceConfig sharedPreferenceConfig = new SharedPreferenceConfig(getApplicationContext());
                            sharedPreferenceConfig.clearPreferences();
                            FirebaseAuth.getInstance().signOut();
                            Intent backToLogin = new Intent(ChooseOption.this , PhoneAuth.class);
                            startActivity(backToLogin);
                            finish();

                        }else{
                            snackbar.make(layout , checkInternet.getNoNetworkConnectionError() , Snackbar.LENGTH_LONG).show();
                        }
                    }else{
                        snackbar.make(layout , checkInternet.getInternetNotSwitchedOnError() , Snackbar.LENGTH_LONG).show();
                    }
                    break;

                case R.id.about_menu:
                    break;

                case R.id.settings_menu:
                    break;

            }
            return super.onOptionsItemSelected(item);
        }



}