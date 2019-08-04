package com.kobby.travelmantics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DealActivity extends AppCompatActivity {
    private FirebaseDatabase aFirebaseDatabase;
    private DatabaseReference aDatabaseReference;
    EditText txtTitle, txtPrice,txtDescription;
    private TravelDeal aDeals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        FirebaseUtil.FirebaseRef("travelDeals",this);
        aFirebaseDatabase = FirebaseUtil.sFirebasedb;
        aDatabaseReference = FirebaseUtil.sDatabaseref;
        txtTitle = findViewById(R.id.txtTitle);
        txtPrice = findViewById(R.id.txtPrice);
        txtDescription = findViewById(R.id.txtDesc);
        Log.d("Activity Name","DEAL ACTIVITY");
        Intent i =  getIntent();
        TravelDeal travelDeal = (TravelDeal) i.getSerializableExtra("Deal");
           if (travelDeal == null){
               travelDeal =new TravelDeal();
           }
           
           this.aDeals = travelDeal;
        txtTitle.setText(aDeals.getTitle());
        txtDescription.setText(aDeals.getDescription());
        txtPrice.setText(aDeals.getPrice());


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_btn){
            saveInfo();
            backToActivity();
        }

        if(item.getItemId() == R.id.del_btn)
                delete();
                backToActivity();
        return super.onOptionsItemSelected(item);

    }

//    Saves the travel info on Save menu option click event
    private void saveInfo() {
        aDeals.setPrice(txtPrice.getText().toString());
        aDeals.setTitle(txtTitle.getText().toString());
        aDeals.setDescription(txtDescription.getText().toString());

        if(aDeals.getId()== null)
        {
            aDatabaseReference.push().setValue(aDeals);
            // Make the user see toast if successful
            Toast.makeText(this, "Deal Saved Successfully", Toast.LENGTH_LONG).show();
          //  call to clear input fields
            clear();
        }else {
            aDatabaseReference.child(aDeals.getId()).setValue(aDeals);
        }
    }

//    Delete Travel Info
    public void delete(){
        if (aDeals==null){
            Toast.makeText(this, "Please save the deal before deleting", Toast.LENGTH_LONG).show();
            return ;
        }
        aDatabaseReference.child(aDeals.getId()).removeValue();
        Toast.makeText(this, "Deal Deleted", Toast.LENGTH_LONG).show();

    }
    public void backToActivity(){
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (txtTitle!=null ||txtPrice!=null||txtDescription!=null){

        new AlertDialog.Builder(this)
                .setTitle("Notice")
                .setMessage("This will save Deal by default")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        saveInfo();
                        dialog.dismiss();
                    }})
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                    }
                }).show();
        }
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
