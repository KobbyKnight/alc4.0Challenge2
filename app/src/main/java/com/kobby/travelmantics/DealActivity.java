package com.kobby.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.Resource;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class DealActivity extends AppCompatActivity {
    private FirebaseDatabase aFirebaseDatabase;
    private DatabaseReference aDatabaseReference;
    EditText txtTitle, txtPrice,txtDescription;
    private TravelDeal aDeals;
    ImageView aImageView;
   private static final int REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
//        FirebaseUtil.FirebaseRef("travelDeals",this);
        aFirebaseDatabase = FirebaseUtil.sFirebasedb;
        aDatabaseReference = FirebaseUtil.sDatabaseref;
        txtTitle = findViewById(R.id.txtTitle);
        txtPrice = findViewById(R.id.txtPrice);
        txtDescription = findViewById(R.id.txtDesc);
        aImageView = findViewById(R.id.img_upload);
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
        showImage(aDeals.getImageUrl());

        Button btnImage = findViewById(R.id.btn_upload);
        btnImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(intent.createChooser(intent,"Insert Picture"),REQUEST_CODE);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save,menu);

        if (FirebaseUtil.isAdmin){
            menu.findItem(R.id.del_btn).setVisible(true);
            menu.findItem(R.id.save_btn).setVisible(true);
            enabledText(true);
            findViewById(R.id.btn_upload).setEnabled(true);

        }else{
            menu.findItem(R.id.del_btn).setVisible(false);
            menu.findItem(R.id.save_btn).setVisible(false);
            enabledText(false);
            findViewById(R.id.btn_upload).setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_btn){
            saveInfo();
            //showImage(aDeals.getImageUrl());
            backToActivity();
        }

        if(item.getItemId() == R.id.del_btn)
                delete();
                backToActivity();
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            Uri file = Objects.requireNonNull(data).getData();
            final StorageReference ref = FirebaseUtil.sStorageReference.child(file.getLastPathSegment());
            UploadTask uploadTask = ref.putFile(file);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String url = Objects.requireNonNull(downloadUri).toString();
                        String fileName = downloadUri.getPath();
                        aDeals.setImageUrl(url);
                        Log.d("ImageUrl",url);
                        aDeals.setImageName(fileName);
                        showImage(url);

                    } else {
                        Toast.makeText(DealActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                        // Handle failures
                        // ...
                    }
                }
            });
//            if (data!=null){
             //Uri file = Objects.requireNonNull(data).getData();
//             final StorageReference reference = FirebaseUtil.sStorageReference.child(file.getLastPathSegment());
//            reference.putFile(file).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    String url = reference.getDownloadUrl().toString();
//                    String fileName = taskSnapshot.getStorage().getPath();
//                    aDeals.setImageUrl(url);
//                    Log.d("ImageUrl",url);
//                    aDeals.setImageName(fileName);
//                    showImage(url);
//
//                }
//            });
//            }else{
//                Toast.makeText(this, "Empty Data", Toast.LENGTH_LONG).show();
//            }

        }
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

        if (aDeals.getImageName() != null && !aDeals.getImageName().isEmpty()){
            StorageReference ref = FirebaseUtil.sStorageReference.child(aDeals.getImageName());
            ref.delete().addOnSuccessListener(new OnSuccessListener<Void>(){
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(DealActivity.this, "Image Deleted", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DealActivity.this, "Could not delete Image", Toast.LENGTH_LONG).show();
                }
            });
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
//        if (txtTitle!=null ||txtPrice!=null||txtDescription!=null){
//
//        new AlertDialog.Builder(this)
//                .setTitle("Notice")
//                .setMessage("This will save Deal by default")
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        saveInfo();
//                        dialog.dismiss();
//                    }})
//                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.dismiss();
//                    }
//                }).show();
//        }
    }

    //    Clears the input text fields
    private void clear() {
        txtDescription.setText("");
        txtTitle.setText("");
        txtPrice.setText("");
        txtTitle.requestFocus();
    }


    private void enabledText(boolean isEnabled){
        txtTitle.setEnabled(isEnabled);
        txtDescription.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
    }

    private void showImage(String url){
        if (url != null && !url.isEmpty()){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(url)
                    .resize(width,width*2/3)
                    .centerCrop()
                    .into(aImageView);
            Log.d("Image", url);
        }
    }
}
