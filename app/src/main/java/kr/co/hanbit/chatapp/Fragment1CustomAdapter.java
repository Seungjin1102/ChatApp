package kr.co.hanbit.chatapp;

import android.content.Context;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Fragment1CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<String> items;//채팅방 사용자 이름
    RequestManager requestManager;
    ArrayList<Contents> contentItems;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    public Fragment1CustomAdapter(Context context, ArrayList<String> items, RequestManager requestManager, ArrayList<Contents> contentItems) {
        this.context = context;
        this.items = items;
        this.requestManager = requestManager;
        this.contentItems = contentItems;
    }

    @Override
    public int getItemViewType(int position) {
        Contents content = contentItems.get(position);
        if(content.getName().equals(Login.MY_NAME)){//내가 입력한 채팅일경우
             return 0;
        }else{
            return 1;
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = null;
        int i =0;
        if(viewType == 0){
            Log.d("ViewHolder", "onCreateViewHolder viewType = 0");
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chattingroom2, parent, false);
            i = 0;
        }else{
            Log.d("ViewHolder", "onCreateViewHolder viewType = 1");
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chattingroom, parent, false);
            i=1;
        }

        if(i==0){
            return new RightViewHolder(itemView);
        }else{
            return new LeftViewHolder(itemView);
        }
    }




    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof RightViewHolder){
            Log.d("ViewHolder", "onBindViewHolder = RightViewHolder");
            ((RightViewHolder) holder).chattingroom2_content.setText(contentItems.get(position).getContent());
            ((RightViewHolder) holder).chattingroom2_time.setText(contentItems.get(position).getTime());
        }else{
            Log.d("ViewHolder", "onBindViewHolder = LeftViewHolder");
            ((LeftViewHolder) holder).chattingroom_name.setText(contentItems.get(position).getName());
            ((LeftViewHolder) holder).chattingroom_content.setText(contentItems.get(position).getContent());
            ((LeftViewHolder) holder).chattingroom_time.setText(contentItems.get(position).getTime());
            StorageReference storageReference = firebaseStorage.getReference().child("userProfiles/"+contentItems.get(position).getName());
            storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        requestManager.load(task.getResult()).into(((LeftViewHolder) holder).chattingroom_imageView);
                    }else{
                        Log.d("Friends", "else 이미지 로딩실패");
                    }
                }
            });
        }


    }

    public void addItems(Contents item){
        contentItems.add(item);
    }

    @Override
    public int getItemCount() {
        return contentItems.size();
    }

    public class LeftViewHolder extends RecyclerView.ViewHolder {

        ImageView chattingroom_imageView;
        TextView chattingroom_name;
        TextView chattingroom_content;
        TextView chattingroom_time;
        public LeftViewHolder(@NonNull View itemView) {
            super(itemView);
            chattingroom_imageView = itemView.findViewById(R.id.chattingroom_imageView);
            chattingroom_name = itemView.findViewById(R.id.chattingroom_name);
            chattingroom_content = itemView.findViewById(R.id.chattingroom_content);
            chattingroom_time = itemView.findViewById(R.id.chattingroom_time);
        }
    }

    public class RightViewHolder extends RecyclerView.ViewHolder{

        TextView chattingroom2_content;
        TextView chattingroom2_time;
        public RightViewHolder(@NonNull View itemView) {
            super(itemView);
            chattingroom2_content = itemView.findViewById(R.id.chattingroom2_content);
            chattingroom2_time = itemView.findViewById(R.id.chattingroom2_time);
        }
    }

}

