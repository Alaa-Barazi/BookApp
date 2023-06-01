package com.example.myapplication.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentGalleryBinding;
import com.squareup.picasso.Picasso;

public class GalleryFragment extends Fragment {
    int [] images = new int[]{R.drawable.book1,R.drawable.book2,R.drawable.book3,R.drawable.book4,
            R.drawable.book5,R.drawable.book6,R.drawable.book7,R.drawable.book8};
    int index = 0;
    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_gallery,container,false);
        ImageView img = (ImageView) root.findViewById(R.id.img);
        Button btnNext = (Button) root.findViewById(R.id.btnNext);
        Button btnPrev = (Button) root.findViewById(R.id.btnPrev);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Picasso.get().load(images[index]).into(img);
                //img.setImageResource(images[index]);
                index++;
                if(index>images.length-1)
                    index=0;
            }
        });
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index--;
                if(index<0)
                    index= images.length-1;
                //img.setImageResource(images[index]);
                Picasso.get().load(images[index]).into(img);


            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}