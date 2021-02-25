package kr.co.hanbit.chatapp;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class ChatCustomAdapter extends RecyclerView.Adapter<ChatCustomAdapter.ViewHolder> {
    ArrayList<HashSet<String>> items = new ArrayList<>();//나의 이름이 들어간 채팅방 집합
    Context mainContext;
    RequestManager requestManager;

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    public ChatCustomAdapter(Context mainContext, ArrayList<HashSet<String>> items, RequestManager requestManager) {
        this.mainContext = mainContext;
        this.items = items;
        this.requestManager = requestManager;
    }


    @NonNull
    @Override
    public ChatCustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatCustomAdapter.ViewHolder holder, int position) {
        String temp = "";//해쉬셋에서 자기자신 이름을 빼고 상대방의 이름만 저장
        Iterator<String> iterator = items.get(position).iterator();
        while(iterator.hasNext()){
            temp = iterator.next();
            if(!temp.equals(Login.MY_NAME)){
                break;
            }
        }
        Log.d("Chat", "temp = " + temp);
        holder.item_chat_textView.setText(""+temp);//채팅상대의 이름을 출력

        //상대방의 이미지 가져오기
        StorageReference storageReference = firebaseStorage.getReference().child("userProfiles/" + temp);
        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    requestManager.load(task.getResult()).into(holder.item_chat_imageView);
                }else{
                    Log.d("Chat", "이미지 로딩 실패!!");
                }
            }
        });

        holder.item_chat_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainContext, ChattingRoom.class);
                intent.putExtra("nameSet", items.get(position));
                mainContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView item_chat_imageView;
        TextView item_chat_textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_chat_imageView = itemView.findViewById(R.id.item_chat_imageView);
            item_chat_textView = itemView.findViewById(R.id.item_chat_textView);
        }
    }
}
