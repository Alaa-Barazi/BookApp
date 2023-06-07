package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class Register extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://fireapp-4a2be-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button Registerbtn = (Button) findViewById(R.id.Registerbtn);
        EditText txtName = (EditText) findViewById(R.id.txtName);
        EditText txtEmail = (EditText) findViewById(R.id.txtEmail);
        EditText txtPass = (EditText) findViewById(R.id.txtPass);
        EditText txtRPass = (EditText) findViewById(R.id.txtRPass);

        Registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass=txtPass.getText().toString(),rpass=txtRPass.getText().toString();
                if(!pass.equals(rpass)){
                    Toast.makeText(Register.this, "Different passwords!! ", Toast.LENGTH_SHORT).show();
                } else  {
                    String name = txtName.getText().toString();
                    String email = txtEmail.getText().toString();

                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                           if (snapshot.hasChild(name)) {
                                Toast.makeText(Register.this, "User Already Exists", Toast.LENGTH_SHORT).show();
                            }
                          else {

                                User user1 = new User(email,pass);

                               databaseReference.child("users").child(name).setValue(user1);


//                               CartBook cartBook = new CartBook(Books.get(i).getName(),imageUrl,Integer.toString(BookID),Books.get(i).getPrice(),username,1);
//                               databaseReference.child("cart").child(Integer.toString(BookID)).setValue(cartBook);
//                               Toast.makeText(getActivity(), "Added Successfully", Toast.LENGTH_LONG).show();
                               Toast.makeText(Register.this, "You are in!! Now Login ", Toast.LENGTH_LONG).show();
                              startActivity(new Intent(Register.this,MainActivity.class));
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
        });
    }


}