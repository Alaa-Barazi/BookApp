package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddRec extends AppCompatActivity {
    EditText title,desc;
    RatingBar ratingBar;
    Button addbtn;
    String bookID;
    Intent intent;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://fireapp-4a2be-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rec);
        title=(EditText) findViewById(R.id.Btitle);
        desc=(EditText) findViewById(R.id.Bdesc);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        addbtn = findViewById(R.id.btnAdd);
        bookID = getIntent().getStringExtra("bookID");

        //       databaseReference.child("favorites").addListenerForSingleValueEvent(new ValueEventListener() {
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("recommendation").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String key = databaseReference.push().getKey();
                        String Rtitle = title.getText().toString();
                        String Rdesc = desc.getText().toString();
                        int rating = ratingBar.getNumStars();
                        Toast.makeText(AddRec.this, Rtitle+" "+Rdesc, Toast.LENGTH_SHORT).show();
                        Rec rec = new Rec(Rtitle,Rdesc,Integer.toString(rating),bookID);
                        //databaseReference.child("users").child(name).setValue(user1);
                        databaseReference.child("recommendation").child(key).setValue(rec);
                        Toast.makeText(AddRec.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }
}