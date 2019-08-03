package com.kobby.travelmantics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InsertActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebasedb;
    private DatabaseReference mDatabaseref;
    EditText txtTitle, txtPrice,txtDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        mFirebasedb =  FirebaseDatabase.getInstance();
        mDatabaseref = mFirebasedb.getReference().child("travelDeals");
        txtTitle = findViewById(R.id.txtTitle);
        txtPrice = findViewById(R.id.txtPrice);
        txtDescription = findViewById(R.id.txtDesc);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_btn){
            saveInfo();
        }
        return super.onOptionsItemSelected(item);

    }

//    Saves the travel info on Save menu option click event
    private void saveInfo() {
        String price = txtPrice.getText().toString();
        String title = txtTitle.getText().toString();
        String desc = txtDescription.getText().toString();
        TravelDeal travelDeal = new TravelDeal(title,price,desc,"");
        mDatabaseref.push().setValue(travelDeal);

//        Make the user see toast if successful
        Toast.makeText(this, "Deal Saved Successfully", Toast.LENGTH_LONG).show();

//        call to clear input fields
        clear();
    }

//    Clears the input text fields
    private void clear() {
        txtDescription.setText("");
        txtTitle.setText("");
        txtPrice.setText("");
        txtTitle.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save,menu);

        return true;
    }
}
