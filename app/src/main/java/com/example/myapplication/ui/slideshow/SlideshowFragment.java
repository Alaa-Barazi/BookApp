package com.example.myapplication.ui.slideshow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.Book;
import com.example.myapplication.BookList;
import com.example.myapplication.FavoriteBook;
import com.example.myapplication.MoreBook;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentSlideshowBinding;
import com.example.myapplication.ui.favorite.FavoritesFragment;
import com.example.myapplication.ui.home.HomeFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SlideshowFragment extends Fragment {
    String username;
    SharedPreferences sharedPreferences;
    ArrayList<BookList> Books=new ArrayList<>();
    ListView ls;
    private FragmentSlideshowBinding binding;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://fireapp-4a2be-default-rtdb.firebaseio.com/");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_slideshow,container,false);
        sharedPreferences = getActivity().getSharedPreferences("Myprefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username","empty");

        ls= (ListView) root.findViewById(R.id.booksList);
        SlideshowFragment.MyAdapter myAdapter = new SlideshowFragment.MyAdapter(Books);
        ls.setAdapter(myAdapter);
        databaseReference.child("RList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot data : snapshot.getChildren()){
                    String key = data.getKey();
                    Object value = data.getValue();
                    String id = (String) data.child("id").getValue();
                    String user = (String) data.child("username").getValue();
                    String name = (String) data.child("name").getValue();
                    String img = (String) data.child("img").getValue();
                    String status = (String) data.child("status").getValue();

                    if(username.equals(user)) {
                        BookList book = new BookList(id,name,img,username,status);
                        Books.add(book);
                    }
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return root;
    }
    class MyAdapter extends BaseAdapter {
        ArrayList<BookList> Books = new ArrayList<>();

        MyAdapter(ArrayList<BookList> Books) {
            this.Books = Books;
        }

        @Override
        public int getCount() {
            return Books.size();
        }

        @Override
        public String getItem(int i) {
            return Books.get(i).getName();
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public String getImg(int i) {
            return Books.get(i).getImg();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater Linflater = getLayoutInflater();
            View view1 = Linflater.inflate(R.layout.readinglistview, null);
            ImageView bkImage = view1.findViewById(R.id.bkImage);
            TextView bkTitle = view1.findViewById(R.id.bkTitle);
            Button bkDone = view1.findViewById(R.id.bkDone);
            Button bkDelete = view1.findViewById(R.id.bkDelete);
          String status = Books.get(i).getStatus();

        //    Toast.makeText(getActivity(), Books.get(i).getStatus(), Toast.LENGTH_SHORT).show();
            if(Books.get(i).getStatus().equals("false")){
                bkDone.setVisibility(View.VISIBLE);
                bkDelete.setVisibility(View.VISIBLE);
            }
            else{
                bkDone.setVisibility(View.GONE);
                bkDelete.setVisibility(View.GONE);
            }
            String img = Books.get(i).getImg();
            Picasso.get().load(img).into(bkImage);
            bkTitle.setText(Books.get(i).getName());
            bkDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    databaseReference.child("RList").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String key = Books.get(i).getId();
                            Books.get(i).setStatus("true");
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("id", key);
                            updates.put("name", Books.get(i).getName());
                            updates.put("img", Books.get(i).getImg());
                            updates.put("username", username);
                            updates.put("status","true");
                            databaseReference.child("RList").child(key).updateChildren(updates);
                            bkDone.setVisibility(View.GONE);
                            bkDelete.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
            bkDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    databaseReference.child("RList").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String key = Books.get(i).getId();
                            if (snapshot.hasChild(key)) {
                                databaseReference.child("RList").child(key).removeValue();
                                Toast.makeText(getActivity(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(getActivity().getIntent());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
            return view1;
        }
    }
}