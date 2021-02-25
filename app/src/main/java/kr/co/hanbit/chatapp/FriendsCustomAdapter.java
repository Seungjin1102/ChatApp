package kr.co.hanbit.chatapp;

import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashSet;

public class FriendsCustomAdapter extends RecyclerView.Adapter<FriendsCustomAdapter.ViewHolder> {

    ArrayList<String> items;//친구의 이름을 저장하는 배열
    Context context;
    RequestManager requestManager;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    DatabaseReference databaseReference;
    Context mainContext;

    public FriendsCustomAdapter(ArrayList<String> items, Context context, RequestManager requestManager, Context mainContext) {
        this.items = items;
        this.context = context;
        this.requestManager = requestManager;
        this.mainContext = mainContext;
    }

    @NonNull
    @Override
    public FriendsCustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_friends, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsCustomAdapter.ViewHolder holder, int position) {

        StorageReference storageReference = firebaseStorage.getReference().child("userProfiles/"+items.get(position));
        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    requestManager.load(task.getResult()).into(holder.item_friends_imageView);
                }else{
                    Log.d("Friends", "else 이미지 로딩실패");
                }
            }
        });

        holder.item_friends_textView.setText(""+items.get(position));
        Log.d("Friends", "holder.item_friends_textView.setText(\"\"+items.get(position));");



        //채팅아이콘 클릭시
        holder.item_friends_chatimageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChatRoom = false;//같은 채팅방이 있으면 true 없으면 false
                Log.d("Friends", "position = " +position);
                HashSet<String> nameSet = new HashSet<>();
                nameSet.add(Login.MY_NAME);
                nameSet.add(items.get(position));
                Log.d("Friends", "chatRoomList 사이즈 = " + MainActivity.chatRoomList.size());


                for (int i = 0; i < MainActivity.chatRoomList.size(); i++) {
                    if (nameSet.equals(MainActivity.chatRoomList.get(i))) {
                        Log.d("Friends", "같은 채팅방이 존재 합니다 위치는 " + i);
                        isChatRoom = true;
                    }
                }

                if(isChatRoom == false){//기존에 있던 채팅방이 없을 경우
                    Contents contents = new Contents("first", "first", "first");
                    databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("ChatApp").child("ChatRoom").child(""+Login.MY_NAME+","+items.get(position)).child("Chat").child("Contents").push().setValue(contents).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Friends", "채팅방 없을때 first 데이터 추가 성공");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Friends", "채팅방 없을때 first 데이터 추가 실패");
                        }
                    });
                    MainActivity.chatRoomList.add(nameSet);//기존에 없던 채팅방을 추가시 ChatRoomList에 추가
                    Intent intent = new Intent(mainContext, ChattingRoom.class);//채팅방 화면으로 이동
                    intent.putExtra("nameSet", nameSet);//선택한 친구와, 자신의 이름을 HashSet에 담아서 보낸다
                    mainContext.startActivity(intent);//여기도 바꿈(원래는 그냥 context)
                }else if(isChatRoom == true){
                    Log.d("Friends", "기존 채팅방 있을때");
                    Intent intent = new Intent(mainContext, ChattingRoom.class);//채팅방 화면으로 이동
                    intent.putExtra("nameSet", nameSet);//선택한 친구와, 자신의 이름을 HashSet에 담아서 보낸다
                    mainContext.startActivity(intent);
                }

            }


        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView item_friends_textView;
        ImageView item_friends_imageView;
        ImageView item_friends_chatimageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_friends_textView = itemView.findViewById(R.id.item_friends_textView);
            item_friends_imageView = itemView.findViewById(R.id.item_friends_imageView);
            item_friends_chatimageView = itemView.findViewById(R.id.item_friends_chatimgeView);
        }
    }
}
