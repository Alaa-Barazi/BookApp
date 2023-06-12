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

import com.example.myapplication.Book;
import com.example.myapplication.CartBook;
import com.example.myapplication.FavoriteBook;
import com.example.myapplication.MainActivity;
import com.example.myapplication.MoreBook;
import com.example.myapplication.R;
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
import java.util.Random;

public class CartFragment extends Fragment {

    private CartViewModel mViewModel;
    SharedPreferences sharedPreferences;

int update =0;
    String username;
    int bookTotal=0;
    ListView ls;
    Button btnPay;
    int total=0;
    TextView totalTxt;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://fireapp-4a2be-default-rtdb.firebaseio.com/");
    int updateAmount=0;
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
        btnPay = (Button) root.findViewById(R.id.btnPay);
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("cart").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (CartBook cartBook : cartBooks) {
                            int cartBookAmount = cartBook.getAmount();
                            DatabaseReference bookReference = databaseReference.child("books").child(cartBook.getId());
                            bookReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot booksSnapshot) {
                                    if (booksSnapshot.exists()) {
                                        int bookTotal = Integer.parseInt(booksSnapshot.child("total").getValue(String.class));
                                        int updatedAmount = bookTotal - cartBookAmount;
                                        //add to hare prefernce or something to save it cause now its not saving its value
                                        updateAmount = bookTotal - cartBookAmount;
                                        bookReference.child("total").setValue(Integer.toString(updatedAmount));
                                    }
                                }
                                @Override

                                public void onCancelled(@NonNull DatabaseError error) {
                                }

                            });
                            for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                                String cartBookId = cartSnapshot.child("id").getValue(String.class);
                                int cartAmount = cartSnapshot.child("amount").getValue(Integer.class);
                                String cartUsername = cartSnapshot.child("username").getValue(String.class);
                                if (!cartUsername.equals(username) && cartBookId.equals(cartBook.getId())) {
                                    int update = Math.abs(cartBookAmount - cartAmount);
                                    String cartKey = cartSnapshot.getKey();
                                    Toast.makeText(getActivity(), cartAmount+"-"+cartBookAmount+"-"+updateAmount, Toast.LENGTH_SHORT).show();

                                        //if (update <= 0) {
                                         //   databaseReference.child("cart").child(cartKey).removeValue();
                                       // } else {
                                          //  if (cartAmount > cartBookAmount) {
                                           /* Map<String, Object> updates = new HashMap<>();
                                            String key = cartBook.getId();
                                            int i = Integer.parseInt(key);
                                            updates.put("id", key);
                                            updates.put("name", cartBooks.get(i).getName());
                                            updates.put("img", cartBooks.get(i).getImg());
                                            updates.put("price", cartBooks.get(i).getPrice());
                                            updates.put("username", cartBooks.get(i).getUsername());
                                            updates.put("amount", updateAmount);
                                            databaseReference.child("cart").child(cartKey).updateChildren(updates);*/
                                            databaseReference.child("cart").child(cartKey).child("amount").setValue(update);
                                           // }
                                        //}

                                }
                            }
                        }
                        // Update quantities of other products based on the new quantities in the cart
                        for (CartBook cartBook : cartBooks) {
                            String cartBookId = cartBook.getId();
                            int cartBookAmount = cartBook.getAmount();
                            DatabaseReference booksReference = databaseReference.child("books").child(cartBookId);
                            booksReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override

                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        int bookTotal = Integer.parseInt(snapshot.child("total").getValue(String.class));
                                        int updatedAmount = bookTotal - cartBookAmount;
                                        if(updatedAmount < 0 ) updatedAmount=0;
                                        booksReference.child("total").setValue(Integer.toString(updatedAmount));
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                        // Remove cart items for the current user
                        databaseReference.child("cart").orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String cartKey = dataSnapshot.getKey();
                                    databaseReference.child("cart").child(cartKey).removeValue();
                                }
                                Toast.makeText(getActivity(), "Cart Payed!!", Toast.LENGTH_SHORT).show();
                              //  startActivity(new Intent(getActivity(), HomeFragment.class));
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
        /*btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("cart").addListenerForSingleValueEvent(new ValueEventListener() {
                    int  bookAmount=0;
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // before that the amount of each book in this cart
                        // get the total of the book related to book in the cart and then should check the cart database
                        //whenever i have a cart book with same id update its amount if the amount is zero then just delete it
                        //then update the qty of the original book and then delete this user's cart
                        for (CartBook cartBook : cartBooks) {
                            int cartbookamount = cartBook.getAmount();
                            DatabaseReference booksReference = databaseReference.child("books").child(cartBook.getId());
                            booksReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot booksSnapshot) {
                                    if (booksSnapshot.exists()) {
                                        bookAmount = Integer.parseInt(booksSnapshot.child("total").getValue(String.class));
                                       update = bookAmount -  cartbookamount;
                                        booksReference.child("total").setValue(Integer.toString(bookAmount));

                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}
                            });

                     databaseReference.child("cart").addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot snapshot) {
                             for(DataSnapshot dataSnapshot :snapshot.getChildren()){
                                    if(!dataSnapshot.child("username").equals(username)){
                                        if(dataSnapshot.child("id").equals(cartBook.getId())){
                                            String keyy = dataSnapshot.getKey();
                                            int CartAmount = dataSnapshot.child("amount").getValue(Integer.class);

                                            if(CartAmount!=bookAmount){
                                               // int updatedAmount = Math.abs(dataSnapshot.child("amount").getValue(Integer.class)-bookAmount);
                                                if(bookAmount==0){
                                                    databaseReference.child("cart").child(keyy).removeValue();
                                                }
                                                else {
                                                    if (CartAmount > bookAmount) {
                                                        Map<String, Object> updates = new HashMap<>();
                                                        String thekey = dataSnapshot.getKey();
                                                        String key = cartBook.getId();
                                                        int i = Integer.parseInt(key);
                                                        updates.put("id", key);
                                                        updates.put("name", cartBooks.get(i).getName());
                                                        updates.put("img", cartBooks.get(i).getImg());
                                                        updates.put("price", cartBooks.get(i).getPrice());
                                                        updates.put("username", cartBooks.get(i).getUsername());
                                                        updates.put("amount", update);
                                                        databaseReference.child("cart").child(thekey).updateChildren(updates);
                                                    }
                                                }
                                            }
                                        }
                                    }
                             }
                         }
                         @Override
                         public void onCancelled(@NonNull DatabaseError error) {}
                     });
                        }


                        //delete cart for this user
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String thekey = dataSnapshot.getKey();
                            String key = dataSnapshot.child("id").getValue(String.class);
                            String user = (String) dataSnapshot.child("username").getValue();
                            if (username.equals(user)) {
                                databaseReference.child("cart").child(thekey).removeValue();

                            }

                        }
                        Toast.makeText(getActivity(), "Cart Payed!!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity().getIntent()));

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                     });
            }
        });*/
        ls= (ListView) root.findViewById(R.id.cartLS);
        CartFragment.MyAdapter myAdapter = new CartFragment.MyAdapter(cartBooks);
        ls.setAdapter(myAdapter);
        databaseReference.child("cart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot data : snapshot.getChildren()){
                    String key = data.child("id").getValue(String.class);
                   DataSnapshot dataSnapshot = snapshot.child(key);
                    int amount = data.child("amount").getValue(Integer.class);
                   String id = data.child("id").getValue(String.class);
                    int price = data.child("price").getValue(Integer.class);
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
        int bookTotal;
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
            String bookId = cartBooks.get(i).getId();
            TextView bookTitle = view1.findViewById(R.id.BookTitle);
            TextView price = view1.findViewById(R.id.price);
            ImageView bookImage = view1.findViewById(R.id.bookImage);
            TextView amount = (TextView) view1.findViewById(R.id.amount);
            Button more = view1.findViewById(R.id.more);
            Button less = view1.findViewById(R.id.less);
            price.setText("Price: " + cartBooks.get(i).getPrice() + "$");
            amount.setText("Amount:" + cartBooks.get(i).getAmount());
            bookTitle.setText(cartBooks.get(i).getName());
            String imageUrl = cartBooks.get(i).getImg();
            Picasso.get().load(imageUrl).into(bookImage);
                more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        databaseReference.child("books").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot bookSnapshot : snapshot.getChildren()) {
                                    String key = bookSnapshot.getKey();

                                    String total;
                                    if (key.equals(CBooks.get(i).getId())) {
                                        total = (String) bookSnapshot.child("total").getValue();
                                        bookTotal = Integer.parseInt(total);
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle any errors that occur
                            }
                        });
                        if (bookTotal != 0 && cartBooks.get(i).getAmount()<bookTotal) {
                        databaseReference.child("cart").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String BookID = cartBooks.get(i).getId();
                                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                    String key = childSnapshot.getKey();
                                    String title = (String) childSnapshot.child("username").getValue();
                                    Toast.makeText(getActivity(), "1 "+childSnapshot.child("username")+" "+username, Toast.LENGTH_SHORT).show();

                                    if (title.equals(username)) {
                                        Toast.makeText(getActivity(), "key " + childSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                                        int bookAmountt = childSnapshot.child("amount").getValue(Integer.class);
                                    if (bookAmountt < bookTotal) {
                                        bookAmountt++;
                                        childSnapshot.getRef().child("amount").setValue(bookAmountt);
                                        Toast.makeText(getActivity(), "Amount changed", Toast.LENGTH_SHORT).show();
                                        amount.setText("Amount:" + bookAmountt);
                                        cartBooks.get(i).setAmount(bookAmountt);
                                        total = total + cartBooks.get(i).getPrice();
                                        totalTxt.setText("Total: " + total);

                                    } else {
                                    Toast.makeText(getActivity(), "key "+childSnapshot.getKey(), Toast.LENGTH_SHORT).show();

                                    Toast.makeText(getActivity(), "Maximum quantity reached", Toast.LENGTH_SHORT).show();
                                    }
                                    }

//                                    booksReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot booksSnapshot) {
//                                            if (booksSnapshot.exists()) {
//                                                int bookAmount = Integer.parseInt(booksSnapshot.child("total").getValue(String.class));
//                                                int updatedAmount = bookAmount - 1;
//                                                bookTotal = updatedAmount;
//                                                booksReference.child("total").setValue(Integer.toString(updatedAmount));
//
//
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError error) {
//
//                                        }
//                                    });
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }

                        });
                    }
                        else{
                            if(bookTotal==0) {
                                Toast.makeText(getActivity(), bookTotal+" "+cartBooks.get(i).getAmount(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(getActivity(), "Sold OUT", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getActivity(), bookTotal+" "+cartBooks.get(i).getAmount(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(getActivity(), "Maximum qty", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            less.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    databaseReference.child("cart").addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String BookID = cartBooks.get(i).getId();
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                String key = childSnapshot.getKey();
                                String user = (String) childSnapshot.child("username").getValue();
                                    DataSnapshot dataSnapshot = snapshot.child(key);
                                    int Bookamountt = dataSnapshot.child("amount").getValue(Integer.class);
                                    if (Bookamountt == 1) {
                                        total = total + (cartBooks.get(i).getPrice() * cartBooks.get(i).getAmount());
                                        totalTxt.setText("Total: " + total);
                                    }
                                    Bookamountt--;
                                    //total=total-(cartBooks.get(i).getPrice()*cartBooks.get(i).getAmount());
                                    //totalTxt.setText("Total: "+total);
                                    if (Bookamountt == 0) {
                                        databaseReference.child("cart").child(key).removeValue();
                                        Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                                        amount.setText("Amount:" + Bookamountt);
                                        startActivity(new Intent(getActivity().getIntent()));
                                    } else {
                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put("id", key);
                                        updates.put("name", cartBooks.get(i).getName());
                                        updates.put("img", cartBooks.get(i).getImg());
                                        updates.put("price", cartBooks.get(i).getPrice());
                                        updates.put("username", username);
                                        updates.put("amount", Bookamountt);
                                        databaseReference.child("cart").child(key).updateChildren(updates);
                                        Toast.makeText(getActivity(), "Amount changed", Toast.LENGTH_SHORT).show();
                                        amount.setText("Amount:" + Bookamountt);
                                        total = total - (cartBooks.get(i).getPrice());
                                        totalTxt.setText("Total: " + total);
                                    }
//                                booksReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot booksSnapshot) {
//                                        if (booksSnapshot.exists()) {
//                                            int bookAmount = Integer.parseInt(booksSnapshot.child("total").getValue(String.class));
//                                            int updatedAmount = bookAmount + 1;
//                                            bookTotal = updatedAmount;
//                                            booksReference.child("total").setValue(Integer.toString(updatedAmount));
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });


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