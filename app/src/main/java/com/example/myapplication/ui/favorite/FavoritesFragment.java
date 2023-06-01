package com.example.myapplication.ui.favorite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.FavoriteBook;
import com.example.myapplication.MoreBook;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {
    private FavoritesViewModel favoritesViewModel;
    SharedPreferences sharedPreferences;
    String username;
    ListView ls;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://fireapp-4a2be-default-rtdb.firebaseio.com/");
    ArrayList<FavoriteBook> favoriteBooks=new ArrayList<FavoriteBook>();
    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_favorites,container,false);
        sharedPreferences = getActivity().getSharedPreferences("Myprefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username","empty");

        ls= (ListView) root.findViewById(R.id.booksList);
        // MyAdapter myAdapter=new MyAdapter(favoriteBooks);
      //   myAdapter.getView(0,root,container);
        MyAdapter myAdapter = new MyAdapter(favoriteBooks);
        ls.setAdapter(myAdapter);

       databaseReference.child("favorites").addListenerForSingleValueEvent(new ValueEventListener() {
           @Override

            public void onDataChange(@NonNull DataSnapshot snapshot) {

                  for(DataSnapshot data : snapshot.getChildren()){
                    String key = data.getKey();
                    Object value = data.getValue();

                    String user = (String) data.child("username").getValue();
                    String name = (String) data.child("name").getValue();
                    String img = (String) data.child("img").getValue();

                    if(username.equals(user)) {
                         FavoriteBook book = new FavoriteBook(key, name, img, username);
                         favoriteBooks.add(book);
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


    class MyAdapter extends BaseAdapter{
        ArrayList<FavoriteBook>FBooks=new ArrayList<>();
        MyAdapter(ArrayList<FavoriteBook>Books){
            this.FBooks=Books;
        }

        @Override
        public int getCount() {
            return FBooks.size();
        }
        @Override
        public String getItem(int i) {
            return FBooks.get(i).getName();
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public String getImg(int i) {
            return FBooks.get(i).getImg();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater Linflater = getLayoutInflater();
            View view1 = Linflater.inflate(R.layout.favoritesview, null);
            TextView bookTitle = view1.findViewById(R.id.BookTitle);
            ImageView bookImage = view1.findViewById(R.id.bookImage);
            Button removeFromFavorites = view1.findViewById(R.id.removeFromFavorites);
            Button more = view1.findViewById(R.id.more);
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent1 = new Intent(getActivity(), MoreBook.class);
                    intent1.putExtra("bookID", favoriteBooks.get(i).getId());
                    startActivity(intent1);
                }
            });
            bookTitle.setText(favoriteBooks.get(i).getName());
            String imageUrl = favoriteBooks.get(i).getImg();
            Picasso.get().load(imageUrl).into(bookImage);
            removeFromFavorites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    databaseReference.child("favorites").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String key = favoriteBooks.get(i).getId();
                            if(snapshot.hasChild(key)){
                                databaseReference.child("favorites").child(key).removeValue();
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