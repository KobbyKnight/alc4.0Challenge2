package com.kobby.travelmantics;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    public static FirebaseDatabase sFirebasedb;
    public static DatabaseReference sDatabaseref;
    public static FirebaseAuth sFirebaseAuth;
    public static FirebaseStorage sFirebaseStorage;
    public static StorageReference sStorageReference;
    public static FirebaseAuth.AuthStateListener sAuthStateListener;
    public static ArrayList<TravelDeal> sDeals;
    private static FirebaseUtil sFirebaseUtil;
    private static ListActivity caller;
    private static final int RC_SIGN_IN = 123;
    static boolean isAdmin ;


    private FirebaseUtil() {}

//    Generic method
    public static void FirebaseRef(String ref, final ListActivity callerActivity){
        if (sFirebaseUtil == null){
            sFirebaseUtil = new FirebaseUtil();
            sFirebasedb = FirebaseDatabase.getInstance();
            sFirebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;
            sAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null){
                        signIn(caller);
                    }else {
                        String uid = firebaseAuth.getUid();
                        checkAdmin(uid);
                        Toast.makeText(caller.getBaseContext(), "Welcome", Toast.LENGTH_LONG).show();
                    }
                }
            };
            connectStorage();
        }
        sDeals = new ArrayList<TravelDeal>();
        sDatabaseref = sFirebasedb.getReference().child(ref);
    }

    private static void checkAdmin(String uid) {
        FirebaseUtil.isAdmin = false;
        DatabaseReference databaseReference = sFirebasedb.getReference().child("admins").child(uid);
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtil.isAdmin = true;
                Log.d("Admin","You're Admin");
                caller.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addChildEventListener(childEventListener);
    }

    public static void signIn(Activity caller){
// Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
// Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public static void attachListener(){
        sFirebaseAuth.addAuthStateListener(sAuthStateListener);
    }

    public static void detachListener(){
        sFirebaseAuth.removeAuthStateListener(sAuthStateListener);
    }

    public static void connectStorage(){
        sFirebaseStorage =FirebaseStorage.getInstance();
        sStorageReference = sFirebaseStorage.getReference().child("deals_pictures");
    }
}
