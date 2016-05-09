package com.alex.tictacpark.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.alex.tictacpark.R;
import com.alex.tictacpark.fragments.PropietarioFragment;

public class AreaPropietarios extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_propietarios);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Infla el fragment PropietarioFragment en la actividad AreaPropietarios
        if(savedInstanceState==null)
        {
            Fragment newFragment = new PropietarioFragment();
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
                //Toast.makeText(this, "Volver atrás", Toast.LENGTH_SHORT).show();
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