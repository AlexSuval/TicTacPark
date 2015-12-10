package com.alex.tictacpark.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.alex.tictacpark.R;
import com.alex.tictacpark.fragments.ParkingFragment;

public class ParkingDetalle extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_detalle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Infla el fragment ParkingFragment en la actividad ParkingDetalle
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new ParkingFragment()).commit();

        //Infla el icono de vuelta atrás
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


}
