package kr.co.hanbit.chatapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

public class Fragment2CustomAdapter extends RecyclerView.Adapter<Fragment2CustomAdapter.ViewHolder> {

    ArrayList<Photo> photoItems;
    RequestManager requestManager;
    Context context;
    HashSet<String> nameSet;
    DatabaseReference databaseReference;
    String chatroomName;



    public Fragment2CustomAdapter(ArrayList<Photo> photoItems, RequestManager requestManager, Context context, HashSet<String> nameSet, String chatroomName) {
        this.photoItems = photoItems;
        this.requestManager = requestManager;
        this.context = context;
        this.nameSet = nameSet;
        this.chatroomName = chatroomName;
    }

    @NonNull
    @Override
    public Fragment2CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_gallery, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Fragment2CustomAdapter.ViewHolder holder, int position) {

        holder.item_gallery_title.setText(photoItems.get(position).getTitle());
        holder.item_gallery_time.setText(photoItems.get(position).getTime());

        Bitmap bitmap = StringToBitMap(photoItems.get(position).getImage());
        holder.item_gallery_photo.setImageBitmap(bitmap);

        holder.item_gallery_photo.setOnLongClickListener(new View.OnLongClickListener() {//이미지 길게 클릭시 삭제기능
            @Override
            public boolean onLongClick(View v) {


                //데이터 삭제
                databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("ChatApp").child("ChatRoom").child(chatroomName).child("Gallery").child("Photo").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Photo photo = dataSnapshot.getValue(Photo.class);
                            if(photo.getTitle().equals(photoItems.get(position).getTitle())){
                                Log.d("Fragment2", "photo.getTitle() = " + photo.getTitle());
                                dataSnapshot.getRef().removeValue();//삭제됨
                                break;
                            }
                        }
                        photoItems.remove(position);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                return false;
            }
        });



    }

    @Override
    public int getItemCount() {
        return photoItems.size();
    }

    public void addItem(Photo item){
        photoItems.add(item);
        notifyItemChanged(0);

    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView item_gallery_photo;
        TextView item_gallery_title;
        TextView item_gallery_time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_gallery_photo = itemView.findViewById(R.id.item_gallery_photo);
            item_gallery_title = itemView.findViewById(R.id.item_gallery_title);
            item_gallery_time = itemView.findViewById(R.id.item_gallery_time);
        }
    }



}
