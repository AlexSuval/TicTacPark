package com.alex.tictacpark.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.alex.tictacpark.R;
import com.alex.tictacpark.fragments.ParkingFragment;

public class ParkingDetalle extends AppCompatActivity {

    public static final String NOMBRE = "com.alex.tictacpark.NOMBRE";
    public static final String LONGITUD = "com.alex.tictacpark.LONGITUDE";
    public static final String LATITUD = "com.alex.tictacpark.LATITUDE";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_detalle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Infla el fragment ParkingFragment en la actividad ParkingDetalle
        if(savedInstanceState==null)
        {
            Fragment newFragment = new ParkingFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.container,newFragment).commit();
        }

        //Infla el icono de vuelta atrás
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    //Al darle al botón de vuelta atrás va hacia atrás
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Método para poner el nombre del Parking en la barra
    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }
}