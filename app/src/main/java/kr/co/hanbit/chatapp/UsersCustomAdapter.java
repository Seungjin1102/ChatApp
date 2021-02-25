package kr.co.hanbit.chatapp;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

//사용자목록을 뿌려주는 RecyclerViewAdapter
public class UsersCustomAdapter extends RecyclerView.Adapter<UsersCustomAdapter.ViewHolder> {

    ArrayList<String> items;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    Context context;
    RequestManager requestManager;



    public UsersCustomAdapter(ArrayList<String> items, Context context, RequestManager requestManager) {
        this.items = items;
        this.context = context;
        this.requestManager = requestManager;
    }

    @NonNull
    @Override
    public UsersCustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_users, parent, false);
        Log.d("UsersCustomAdapter", "onCreateViewHolder호출");
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersCustomAdapter.ViewHolder holder, int position) {

        StorageReference storageReference = firebaseStorage.getReference().child("userProfiles/" + items.get(position));
        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {//사용자 프로필사진 업로드
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    requestManager.load(task.getResult()).into(holder.item_users_imageView);
                }else {
                    Log.d("User", "else 이미지로딩실패");
                }
            }
        });
        holder.item_users_textView.setText(""+items.get(position));//이름 표시


        //추가한거
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("ChatApp").child("LoginData").child(""+items.get(position)).child("Friends_"+items.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {//그 사람의 친구목록에 나의 이름이 있으면 친구추가 버튼을 표시
                boolean flag =true;                                     //없으면 표시를 안한다.
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    String temp = dataSnapshot.getKey();
                    if(temp.equals(Login.MY_NAME)){
                        flag = false;
                    }
                }
                if(flag){
                    holder.item_users_addimageView.setImageResource(R.drawable.ic_baseline_person_add_24);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //친구추가버튼 누를시
        holder.item_users_addimageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("ChatApp").child("LoginData").child(""+items.get(position)).child("Friends_" + items.get(position)).child(""+Login.MY_NAME).setValue(""+Login.MY_NAME);
                databaseReference.child("ChatApp").child("LoginData").child(""+Login.MY_NAME).child("Friends_" + Login.MY_NAME).child(""+items.get(position)).setValue(""+items.get(position));
                holder.item_users_addimageView.setImageResource(0);
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView item_users_imageView;
        TextView item_users_textView;
        ImageView item_users_addimageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_users_imageView = itemView.findViewById(R.id.item_users_imageView);
            item_users_textView = itemView.findViewById(R.id.item_users_textView);
            item_users_addimageView = itemView.findViewById(R.id.item_users_addimgeView);
        }
    }
}
