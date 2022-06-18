package com.cmp354.bookly;

import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class BookSliderAdapter extends RecyclerView.Adapter<BookSliderAdapter.BookViewHolder>{

    private List<Book> books;

    public BookSliderAdapter(List<Book> books) {
        this.books = books;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_container,
                parent,
                false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        holder.setBookData(books.get(position));
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder{

        private KenBurnsView kbvBook;
        private TextView textTitle, textBook, textStarRating;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);

            kbvBook = itemView.findViewById(R.id.kbvBook);
            textTitle = itemView.findViewById(R.id.textTitle);
            textBook = itemView.findViewById(R.id.textBook);
            textStarRating = itemView.findViewById(R.id.textStarRating);
        }

        void setBookData (Book book){
            if(book.uri != null){
                Picasso.get().load(book.uri).into(kbvBook);
            }
            else {
                kbvBook.setImageBitmap(book.image);
            }
            textTitle.setText(book.title);
            textBook.setText(book.author);
            textStarRating.setText(book.year+"");


        }
    }
}
