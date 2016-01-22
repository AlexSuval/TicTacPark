package com.alex.tictacpark.models;

import android.location.Address;

/**
 * Created by Alex on 21/01/2016.
 */

public class GeoSearchResult {

    private Address address;

    public GeoSearchResult(Address address)
    {
        this.address = address;
    }

    public String getAddress(){

        String display_address = "";

        display_address += address.getAddressLine(0) + "\n";

        int c = address.getMaxAddressLineIndex();
        for(int i = 1; i <= address.getMaxAddressLineIndex(); i++)
        {
            display_address += address.getAddressLine(i) + " ";
        }

        //if(c > 1) display_address = display_address.substring(0, display_address.length() - 2);
        display_address = display_address.substring(0, display_address.length() - 1);

        return display_address;
    }

    public String toString(){
        String display_address = "";

        if(address.getFeatureName() != null)
        {
            display_address += address + ", ";
        }

        for(int i = 0; i < address.getMaxAddressLineIndex(); i++)
        {
            display_address += address.getAddressLine(i);
        }

        return display_address;
    }
}