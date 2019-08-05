package com.kobby.travelmantics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    ArrayList <TravelDeal> aDeals;
    private FirebaseDatabase aFirebaseDatabase;
    private DatabaseReference aDatabaseReference;
    private ChildEventListener aChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        String a = FirebaseUtil.isAdmin?"TRUE":"false";
        Log.d("ListActivity Admin Menu",a);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem insertMenu = menu.findItem(R.id.insert_menu);
        if (FirebaseUtil.isAdmin) {
            insertMenu.setVisible(true);
        }else{
            insertMenu.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.insert_menu){
            Intent intent = new Intent(this, DealActivity.class);
            startActivity(intent);
            return true;
        }
        if(item.getItemId() == R.id.logout_menu)
                logout();

        return super.onOptionsItemSelected(item);
    }

    private boolean logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Logout","User Logged out");
                        FirebaseUtil.attachListener();
                    }
                });
        FirebaseUtil.detachListener();
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtil.FirebaseRef("travelDeals",this);
        aFirebaseDatabase = FirebaseUtil.sFirebasedb;
        aDatabaseReference = FirebaseUtil.sDatabaseref;

        RecyclerView rvDeals = findViewById(R.id.rvDeals);
        final DealAdapter dealAdapter = new DealAdapter();
        rvDeals.setAdapter(dealAdapter);
        LinearLayoutManager dealsLayoutManager =
                new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        rvDeals.setLayoutManager(dealsLayoutManager);

        FirebaseUtil.attachListener();
    }

    public void showMenu(){
        invalidateOptionsMenu();
    }
}
