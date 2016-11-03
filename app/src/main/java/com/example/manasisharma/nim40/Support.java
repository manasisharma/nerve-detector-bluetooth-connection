package com.example.manasisharma.nim40;

/**
 * Created by manasisharma on 8/20/15.
 */


import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.net.Uri;

import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;


import butterknife.ButterKnife;
import butterknife.InjectView;

public class Support extends ActionBarActivity {

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerlayout;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.drawer_recyclerView)
    RecyclerView drawerRecyclerView;

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_support);
        addListenerOnButton();

        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerlayout, toolbar, R.string.app_name, R.string.app_name);

        drawerlayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
        List<String> rows = new ArrayList<>();
        rows.add("Home");
        rows.add("Feedback");
        DrawerAdapter drawerAdapter = new DrawerAdapter(rows);
        drawerRecyclerView.setAdapter(drawerAdapter);
        drawerRecyclerView.hasFixedSize();
        drawerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    public void addListenerOnButton() {

        final Context context = this;

        button = (Button) findViewById(R.id.button3);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:8005959709"));
                startActivity(intent);

            }

        });

        button = (Button) findViewById(R.id.button10);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:8008745797"));
                startActivity(intent);

            }

        });


    }


}









