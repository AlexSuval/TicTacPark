package com.alex.tictacpark.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.alex.tictacpark.R;
import com.alex.tictacpark.fragments.ParkingFragment;

public class ParkingDetalle extends AppCompatActivity {

    public static final String NOMBRE = "com.alex.tictacpark.NOMBRE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_detalle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Infla el fragment ParkingFragment en la actividad ParkingDetalle
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new ParkingFragment()).commit();

        //Coge el Intent y pone el nombre del parking en la barra
        Intent intent = getIntent();
        String nombre=intent.getStringExtra(NOMBRE);
        getSupportActionBar().setTitle(nombre);

        //Infla el icono de vuelta atrás
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
