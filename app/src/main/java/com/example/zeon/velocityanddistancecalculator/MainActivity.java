package com.example.zeon.velocityanddistancecalculator;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.squareup.otto.Subscribe;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, MainFragment.newInstance(), "main_fragment")
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Subscribe
    public void serviceHandle(SimpleEvent event){
        if(event.getStatus() == SimpleEvent.OPEN_SERVICE){
            startService(new Intent(this, MyService.class));
        } else if(event.getStatus() == SimpleEvent.CLOSR_SERVICE){
            stopService(new Intent(this, MyService.class));
        }
        finish();
    }
}
