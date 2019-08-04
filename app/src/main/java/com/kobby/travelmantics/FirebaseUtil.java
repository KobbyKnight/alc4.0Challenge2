package com.kobby.travelmantics;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    public static FirebaseDatabase sFirebasedb;
    public static DatabaseReference sDatabaseref;
    public static FirebaseAuth sFirebaseAuth;
    public static FirebaseAuth.AuthStateListener sAuthStateListener;
    public static ArrayList<TravelDeal> sDeals;
    private static FirebaseUtil sFirebaseUtil;
    private static final int RC_SIGN_IN = 123;

    private FirebaseUtil() {}

//    Generic method
    public static void FirebaseRef(String ref, final Activity callerActivity){
        if (sFirebaseUtil == null){
            sFirebaseUtil = new FirebaseUtil();
            sFirebasedb = FirebaseDatabase.getInstance();
            sFirebaseAuth = FirebaseAuth.getInstance();
//            Activity caller = callerActivity;
            sAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null){
                        sFirebaseUtil.signIn(callerActivity);
                    }else {

                    Toast.makeText(callerActivity.getBaseContext(), "Welcome Back", Toast.LENGTH_LONG).show();
                    }
                }
            };
        }
        sDeals = new ArrayList<TravelDeal>();
        sDatabaseref = sFirebasedb.getReference().child(ref);
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

}
