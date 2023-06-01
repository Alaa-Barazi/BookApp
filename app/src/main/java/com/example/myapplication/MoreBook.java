package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MoreBook extends AppCompatActivity {

    String bookID,bookName;
    Intent intent;
    ListView ls;
    ArrayList<Rec> rec = new ArrayList<>();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://fireapp-4a2be-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_book);
        TextView BookName = (TextView) findViewById(R.id.bookName);
        intent = getIntent();
        bookID = getIntent().getStringExtra("bookID");
        bookName = getIntent().getStringExtra("bookName");
        BookName.setText(bookName);
        ls= (ListView) findViewById(R.id.booksList);
        MyAdapter myAdapter = new MyAdapter(rec);
        ls.setAdapter(myAdapter);
        databaseReference.child("recommendation").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String snapshotBookID  = (String) dataSnapshot.child("bookID").getValue();
                    if(snapshotBookID .equals(bookID)){
                        String title = (String) dataSnapshot.child("title").getValue();
                        String desc = (String) dataSnapshot.child("desc").getValue();
                        String rating = (String) dataSnapshot.child("rating").getValue().toString();
                        String therating = rating;
                        Rec rec1 = new Rec(title,desc,therating,bookID);
                        rec.add(rec1);
                    }
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Button addRecButton = findViewById(R.id.AddRecom);
        addRecButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MoreBook.this, AddRec.class);
                intent1.putExtra("bookID", bookID);
                startActivity(intent1);
            }
        });

    }





    class MyAdapter extends BaseAdapter{
        ArrayList<Rec> rec = new ArrayList<>();
        MyAdapter(ArrayList<Rec>rec){
            this.rec=rec;
        }

        @Override
        public int getCount() {
            return rec.size();
        }

        @Override
        public Object getItem(int i) {
            return rec.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater Linflater = getLayoutInflater();
            View view1 = Linflater.inflate(R.layout.reviewview, null);
            TextView title = (TextView) view1.findViewById(R.id.RTitle);
            TextView desc = (TextView) view1.findViewById(R.id.subtitle);
            RatingBar ratingBar = (RatingBar) view1.findViewById(R.id.ratingBar);
            title.setText(rec.get(i).getTitle());
            desc.setText(rec.get(i).getDesc());
            float rating = (float) (Integer.parseInt(rec.get(i).getRating()));
            ratingBar.setRating(rating);
//            bookTitle.setText(favoriteBooks.get(i).getName());
//            String imageUrl = favoriteBooks.get(i).getImg();


            return view1;
        }
    }

    }
