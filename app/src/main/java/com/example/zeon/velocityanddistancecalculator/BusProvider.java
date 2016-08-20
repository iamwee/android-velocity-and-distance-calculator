package com.example.zeon.velocityanddistancecalculator;

import com.squareup.otto.Bus;

/**
 * Created by Zeon on 20/8/2559.
 */
public class BusProvider extends Bus {
    public static BusProvider instance;
    public static BusProvider getInstance(){
        if(instance == null) instance = new BusProvider();
        return instance;
    }
}
