package com.alex.tictacpark.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.alex.tictacpark.R;
import com.alex.tictacpark.fragments.RegistroParkingFragment;

public class AreaParking extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_parking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Infla el fragment RegistroParkingFragment en la actividad AreaParking
        if(savedInstanceState==null)
        {
            Fragment newFragment = new RegistroParkingFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.container,newFragment).commit();
        }

        //Infla el icono de vuelta atrás
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    // Al darle al botón de vuelta atrás va hacia atrás
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Método para poner el nombre del propietario en la barra
    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }
}