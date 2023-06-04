package com.example.myapplication.ui.cart;

import androidx.lifecycle.ViewModelProvider;

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

import com.example.myapplication.CartBook;
import com.example.myapplication.FavoriteBook;
import com.example.myapplication.MoreBook;
import com.example.myapplication.R;
import com.example.myapplication.ui.favorite.FavoritesFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartFragment extends Fragment {

    private CartViewModel mViewModel;
    SharedPreferences sharedPreferences;
    String username;
    ListView ls;
    Button btnPay;
    int total=0;
    TextView totalTxt;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://fireapp-4a2be-default-rtdb.firebaseio.com/");
    ArrayList<CartBook> cartBooks=new ArrayList<CartBook>();

    public static CartFragment newInstance() {
        return new CartFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        cartBooks=new ArrayList<>();
        total=0;

        View root=inflater.inflate(R.layout.fragment_cart,container,false);
        sharedPreferences = getActivity().getSharedPreferences("Myprefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username","empty");
        totalTxt = (TextView) root.findViewById(R.id.totalTxt);
        //totalTxt.setText(total);
        btnPay = (Button) root.findViewById(R.id.btnPay);
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("cart").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot :snapshot.getChildren()){
                            String key = dataSnapshot.getKey();
                            String user = (String) dataSnapshot.child("username").getValue();
                            if(username.equals(user)) {
                                databaseReference.child("cart").child(key).removeValue();
                                Toast.makeText(getActivity(), "Cart Payed!!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getActivity().getIntent()));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        ls= (ListView) root.findViewById(R.id.cartLS);
        CartFragment.MyAdapter myAdapter = new CartFragment.MyAdapter(cartBooks);
        ls.setAdapter(myAdapter);
        databaseReference.child("cart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot data : snapshot.getChildren()){
                    String key = data.getKey();
                   DataSnapshot dataSnapshot = snapshot.child(key);
                    int amount = dataSnapshot.child("amount").getValue(Integer.class);
                    String id = dataSnapshot.child("id").getValue(String.class);
                    int price = dataSnapshot.child("price").getValue(Integer.class);
                   String user = (String) data.child("username").getValue();
                    String name = (String) data.child("name").getValue();
                    String img = (String) data.child("img").getValue();

                    if(username.equals(user)) {
                        total=total+(amount*price);
                        totalTxt.setText("Total: "+total);
                        CartBook cartBook = new CartBook(name,img,id,price,username,amount);
                        cartBooks.add(cartBook);
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
        ArrayList<CartBook>CBooks=new ArrayList<>();
        MyAdapter(ArrayList<CartBook>Books){
            this.CBooks=Books;
        }

        @Override
        public int getCount() {
            return CBooks.size();
        }
        @Override
        public String getItem(int i) {
            return CBooks.get(i).getName();
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public String getImg(int i) {
            return CBooks.get(i).getImg();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater Linflater = getLayoutInflater();
            View view1 = Linflater.inflate(R.layout.cartview, null);
            TextView bookTitle = view1.findViewById(R.id.BookTitle);
            TextView price =view1.findViewById(R.id.price);
            ImageView bookImage = view1.findViewById(R.id.bookImage);
            TextView amount = (TextView) view1.findViewById(R.id.amount);
           // Button removeFromFavorites = view1.findViewById(R.id.removeFromFavorites);
            Button more = view1.findViewById(R.id.more);
            Button less = view1.findViewById(R.id.less);
            price.setText("Price: "+cartBooks.get(i).getPrice()+"$");
            amount.setText("Amount:"+cartBooks.get(i).getAmount());
            bookTitle.setText(cartBooks.get(i).getName());
            String imageUrl = cartBooks.get(i).getImg();

            Picasso.get().load(imageUrl).into(bookImage);
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    databaseReference.child("cart").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String BookID= cartBooks.get(i).getId();
                            if(snapshot.hasChild(BookID)){
                                String key = cartBooks.get(i).getId();
                                DataSnapshot dataSnapshot = snapshot.child(key);
                                int Bookamountt = dataSnapshot.child("amount").getValue(Integer.class);
                                Bookamountt++;
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("id", key);
                                updates.put("name", cartBooks.get(i).getName());
                                updates.put("img", cartBooks.get(i).getImg());
                                updates.put("price", cartBooks.get(i).getPrice());
                                updates.put("username", username);
                                updates.put("amount",Bookamountt);
                                databaseReference.child("cart").child(key).updateChildren(updates);
                                Toast.makeText(getActivity(), "Amount changed", Toast.LENGTH_SHORT).show();
                                amount.setText("Amount:"+Bookamountt);
                                total=total+(cartBooks.get(i).getPrice());
                                totalTxt.setText("Total: "+total);

                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            });
            less.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    databaseReference.child("cart").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String BookID= cartBooks.get(i).getId();
                            if(snapshot.hasChild(BookID)){
                                String key = cartBooks.get(i).getId();
                                DataSnapshot dataSnapshot = snapshot.child(key);
                                int Bookamountt = dataSnapshot.child("amount").getValue(Integer.class);
                                if(Bookamountt==1){
                                    total=total+(cartBooks.get(i).getPrice()*cartBooks.get(i).getAmount());
                                    totalTxt.setText("Total: "+total);
                                }
                                Bookamountt--;
                                //total=total-(cartBooks.get(i).getPrice()*cartBooks.get(i).getAmount());
                                //totalTxt.setText("Total: "+total);
                                if(Bookamountt == 0){
                                    databaseReference.child("cart").child(key).removeValue();
                                    Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();


                                    amount.setText("Amount:"+Bookamountt);
                                    startActivity(new Intent(getActivity().getIntent()));
                                }
                                else {
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("id", key);
                                    updates.put("name", cartBooks.get(i).getName());
                                    updates.put("img", cartBooks.get(i).getImg());
                                    updates.put("price", cartBooks.get(i).getPrice());
                                    updates.put("username", username);
                                    updates.put("amount", Bookamountt);
                                    databaseReference.child("cart").child(key).updateChildren(updates);
                                    Toast.makeText(getActivity(), "Amount changed", Toast.LENGTH_SHORT).show();
                                    amount.setText("Amount:"+Bookamountt);
                                    total=total-(cartBooks.get(i).getPrice());
                                    totalTxt.setText("Total: "+total);
                                }
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