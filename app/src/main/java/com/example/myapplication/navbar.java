package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityNavbarBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class navbar extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityNavbarBinding binding;
    SharedPreferences sharedPreferences;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://fireapp-4a2be-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("Myprefs", Context.MODE_PRIVATE);
        binding = ActivityNavbarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarNavbar.toolbar);
        binding.appBarNavbar.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,R.id.nav_favorites,R.id.nav_cart)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navbar);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navbar, menu);
        TextView txtNameUser = (TextView) findViewById(R.id.txtNameUser);
        TextView txtEmailUser = (TextView) findViewById(R.id.txtEmailUser);
        String name = sharedPreferences.getString("username","empty");
        String mail = sharedPreferences.getString("email","empty");
        txtEmailUser.setText(mail);
        txtNameUser.setText("Welcome: "+name);
        Button EditProfile = (Button) findViewById(R.id.EditProfile) ;
        ImageButton logoutBtn = (ImageButton) findViewById(R.id.logoutBtn);
        EditText userUpdate = (EditText)findViewById(R.id.txtUsernameDialog);
        EditText emailUpdate = (EditText)findViewById(R.id.txtEmailDialog);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int1 = new Intent(navbar.this, MainActivity.class);
                startActivity(int1);
            }
        });
        EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(navbar.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.edit_profile, null);
                alert.setView(dialogView);

                EditText userUpdate = dialogView.findViewById(R.id.txtUsernameDialog);
                EditText emailUpdate = dialogView.findViewById(R.id.txtEmailDialog);

                alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String usernameUpdate = userUpdate.getText().toString();
                        String emailUserUpdate = emailUpdate.getText().toString();

                        if (!usernameUpdate.isEmpty() && !emailUserUpdate.isEmpty()) {
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("email", emailUserUpdate);
                            updates.put("password", usernameUpdate);

                            String name = sharedPreferences.getString("username", "empty");

                            databaseReference.child("users").child(name).updateChildren(updates)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("email", emailUserUpdate);
                                            editor.commit();
                                            Toast.makeText(navbar.this, "User information updated", Toast.LENGTH_SHORT).show();
                                            finish();
                                            startActivity(getIntent());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(navbar.this, "Failed to update user information", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Handle cancel action if needed
                    }
                });

                alert.show();
            }
        });


        //change profile details with name and email
        // on click of the photo edit details maybe with dialog

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navbar);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}