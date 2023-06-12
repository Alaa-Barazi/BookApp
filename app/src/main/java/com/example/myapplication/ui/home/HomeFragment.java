package com.example.myapplication.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.myapplication.CartBook;
import com.example.myapplication.FavoriteBook;
import com.example.myapplication.MoreBook;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentHomeBinding;

import com.example.myapplication.ui.slideshow.SlideshowFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {
    ArrayList<Book> Books=new ArrayList<>();
    ListView ls;
    private FragmentHomeBinding binding;
    SharedPreferences sharedPreferences;
    String username;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://fireapp-4a2be-default-rtdb.firebaseio.com/");
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //ls=new ListView(null);
        Books=new ArrayList<>();
        sharedPreferences = getActivity().getSharedPreferences("Myprefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username","empty");
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        //View root=inflater.inflate(R.layout.fragment_home,container,false);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ls=(ListView) binding.bookList;
        final MyAdapter myAdapter=new MyAdapter(Books);
        ls.setAdapter(myAdapter);
        String img = "https://img.freepik.com/free-vector/open-book-isolated_1284-43075.jpg?w=2000";
        //loop for getting all from the db and inserting them to the list
        databaseReference.child("books").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot data : snapshot.getChildren()){
                    String key = data.getKey();
                    Object value = data.getValue();
                    // String fieldValue = (String) childSnapshot.child("field_name").getValue();
                    String name = (String) data.child("name").getValue();
                    String img = (String) data.child("img").getValue();
                    String price = (String) data.child("price").getValue();
                    int realPrice = Integer.parseInt(price);
                    String total = (String) data.child("total").getValue();
                    int realTotal = Integer.parseInt(total);
                    Book book = new Book(Integer.parseInt(key),name,img,realPrice,realTotal);
                    Books.add(book);
                }
                myAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        Book b= new Book(3,"book1",img);
//        Books.add(b);
//        Book b1= new Book(4,"book2",img);
//        Books.add(b1);
       /* final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);*/
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    class MyAdapter extends BaseAdapter {
        ArrayList<Book>Books=new ArrayList<>();
        MyAdapter(ArrayList<Book>Books){
            this.Books=Books;
        }
        @Override
        public int getCount() {
            return Books.size();
        }
        @Override
        public String getItem(int i) {
            return Books.get(i).getName();
        }

        public String getImg(int i) {
            return Books.get(i).getImg();
        }
        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater Linflater = getLayoutInflater();
            View view1 = Linflater.inflate(R.layout.bookview, null);
            TextView bookTitle = view1.findViewById(R.id.BookTitle);
            TextView price = view1.findViewById(R.id.price);
            ImageView bookImage = view1.findViewById(R.id.bookImage);
            Button addReadingList = view1.findViewById(R.id.addReadingList);
            Button addToFavorites = view1.findViewById(R.id.removeFromFavorites);
            Button more = view1.findViewById(R.id.more);
            Button cart = view1.findViewById(R.id.cart);
            bookTitle.setText(Books.get(i).getName());
            price.setText("Price: "+Books.get(i).getPrice()+"$");
            String imageUrl = Books.get(i).getImg();
            Picasso.get().load(imageUrl).into(bookImage);

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int BookID= Books.get(i).getId();
                   Intent intent= new Intent(getActivity(), MoreBook.class);
//                   Toast.makeText(getActivity(), Integer.toString(BookID), Toast.LENGTH_SHORT).show();
//                   // intent.putExtra("bookID",Integer.toString(BookID));
//                    startActivity(intent);
                    Intent intent2 = new Intent(getActivity(), MoreBook.class);
                    intent.putExtra("bookID",Integer.toString(BookID));
                    intent.putExtra("bookName",Books.get(i).getName());

                    startActivity(intent);
                }
            });
            cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    databaseReference.child("cart").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int BookID = Books.get(i).getId();
                            if (Books.get(i).getTotal() <= 0) {
                                Toast.makeText(getActivity(), "This book is sold out", Toast.LENGTH_SHORT).show();
                            } else {
                                if (snapshot.hasChild(Integer.toString(BookID)) && snapshot.child("username").equals(username)) {
                                    String key = Integer.toString(Books.get(i).getId());
                                    DataSnapshot dataSnapshot = snapshot.child(key);
                                    int amount = dataSnapshot.child("amount").getValue(Integer.class);

                                    amount++;
                                    if (amount <= Books.get(i).getTotal()){
                                        Map<String, Object> updates = new HashMap<>();
                                    updates.put("id", key);
                                    updates.put("name", Books.get(i).getName());
                                    updates.put("img", Books.get(i).getImg());
                                    updates.put("price", Books.get(i).getPrice());
                                    updates.put("username", username);
                                    updates.put("amount", amount);
                                    databaseReference.child("cart").child(key).updateChildren(updates);
                                    Toast.makeText(getActivity(), "Already Exists, amount changed", Toast.LENGTH_SHORT).show();
                                }

                                    else{
                                        Toast.makeText(getActivity(), "Maximum Qty", Toast.LENGTH_SHORT).show();
                                    }
                                } else if (snapshot.child("username").equals(username)==false) {
                                    String key = databaseReference.push().getKey();
                                    CartBook cartBook = new CartBook(Books.get(i).getName(), imageUrl, Integer.toString(BookID), Books.get(i).getPrice(), username, 1);
                                    databaseReference.child("cart").child(key).setValue(cartBook);
                                    Toast.makeText(getActivity(), "Added Successfully", Toast.LENGTH_LONG).show();
                                }
//                                Books.get(i).setTotal(Books.get(i).getTotal()-1);
//                                Map<String, Object> updates = new HashMap<>();
//                                updates.put("img", Books.get(i).getImg());
//                                updates.put("name", Books.get(i).getName());
//                                updates.put("price", Integer.toString(Books.get(i).getPrice()));
//                                updates.put("total", Integer.toString(Books.get(i).getTotal()));
//                                databaseReference.child("books").child(Integer.toString(BookID)).setValue(updates);

                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
            addToFavorites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    databaseReference.child("favorites").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int BookID= Books.get(i).getId();
                            if(snapshot.hasChild(Integer.toString(BookID))){
                                Toast.makeText(getActivity(), "Already Exists", Toast.LENGTH_SHORT).show();
                            }
                            else{

                                FavoriteBook book = new FavoriteBook(Integer.toString(BookID),Books.get(i).getName(),imageUrl,username);
                                databaseReference.child("favorites").child(Integer.toString(BookID)).setValue(book);
                                Toast.makeText(getActivity(), "Added Successfully", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            });
            addReadingList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    databaseReference.child("RList").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int BookID= Books.get(i).getId();
                            if(snapshot.hasChild(Integer.toString(BookID))){
                                Toast.makeText(getActivity(), "Already there", Toast.LENGTH_SHORT).show();

                            }
                            else {
                                BookList book = new BookList(Integer.toString(BookID), Books.get(i).getName(),
                                        Books.get(i).getImg(), username, "false");
                                databaseReference.child("RList").child(Integer.toString(BookID)).setValue(book);
                                Toast.makeText(getActivity(), "Added Successfully", Toast.LENGTH_LONG).show();
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