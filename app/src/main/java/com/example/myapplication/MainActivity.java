package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    Intent intent;
    SharedPreferences sharedPreferences;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://fireapp-4a2be-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText name = (EditText) findViewById(R.id.phone);
        EditText password = (EditText) findViewById(R.id.password);
        Button loginBtn = (Button) findViewById(R.id.loginBtn);
            sharedPreferences = getSharedPreferences("Myprefs", Context.MODE_PRIVATE);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameTxt = name.getText().toString();
                String passwordTxt = password.getText().toString();
                if(nameTxt.isEmpty() || passwordTxt.isEmpty()){
                    Toast.makeText(MainActivity.this, "hii,Please enter your phone or password!!", Toast.LENGTH_LONG).show();
                }
                else{
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild(nameTxt)) {
                                        String getPassword = snapshot.child(nameTxt).child("password").getValue(String.class);
                                        String getEmail = snapshot.child(nameTxt).child("email").getValue(String.class);
                                        SharedPreferences.Editor myedit = sharedPreferences.edit();
                                        myedit.putString("username",nameTxt);
                                        myedit.putString("password",passwordTxt);
                                        myedit.putString("email",getEmail);
                                        myedit.commit();
                                        //the second string is the name of the field in the table in our database
                                        if (getPassword.equals(passwordTxt)) {
                                            //Save user's details on local Storage

                                            Toast.makeText(MainActivity.this, "hii,Successfully Logged in", Toast.LENGTH_SHORT).show();

                                            startActivity(new Intent(MainActivity.this,navbar.class));
                                        } else {
                                            Toast.makeText(MainActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                                        }
                                    } else
                                        Toast.makeText(MainActivity.this, "User Not Found", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }
        });
    }
    public void changeToRegister(View view) {
        intent = new Intent(MainActivity.this, Register.class);
        startActivity(intent);
    }
}