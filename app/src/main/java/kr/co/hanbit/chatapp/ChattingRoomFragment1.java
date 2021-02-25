package kr.co.hanbit.chatapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class ChattingRoomFragment1 extends Fragment {


    ArrayList<String> items;//채팅방 참가한 사람의 이름(필요없는거 같음)

    HashSet<String> nameSet;
    ArrayList<Contents> contentItems;

    Context context = getContext();
    RequestManager requestManager;


    Fragment1CustomAdapter fragment1CustomAdapter;
    RecyclerView fragment1_recyclerView;
    LinearLayoutManager linearLayoutManager;

    EditText chattingroom_fragment1_editText;
    Button chattingroom_fragment1_button;



    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;
    DatabaseReference databaseReference3;

    String temp;

    public ChattingRoomFragment1(ArrayList<String> items, HashSet<String> nameSet) {
        this.items = items;
        this.nameSet = nameSet;
    }//채팅방 참가한 사람의 이름

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestManager = Glide.with(getActivity());
        contentItems = new ArrayList<>();
        Log.d("Fragment2", "ChattingRoomFragment1 onCreate()호출");
        //setInit();

    }

    private void setInit() {
        if(contentItems.get(0).getName().equals("first")){//처음에 first제거하기
            contentItems.remove(0);
        }
        fragment1CustomAdapter = new Fragment1CustomAdapter(context, items, requestManager, contentItems);
        Log.d("Path", "contentItems 사이즈 = " + contentItems.size());
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        fragment1_recyclerView.setLayoutManager(linearLayoutManager);
        fragment1_recyclerView.setAdapter(fragment1CustomAdapter);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chattingroom_fragment1, container, false);
        fragment1_recyclerView = (RecyclerView)view.findViewById(R.id.fragment1_recyclerView);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        //nameSet을 이용하여 목표이름으로 접근하고 Contents객체를 contentItems에 넣은후 CustomAdapter에 보낸다.
        databaseReference.child("ChatApp").child("ChatRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    //String temp = dataSnapshot.getKey();
                    temp = dataSnapshot.getKey();
                    HashSet<String> tempSet = new HashSet<>();
                    String[] res = temp.split(",");

                    for(int i = 0; i < res.length; i++){
                        Log.d("Friends", "res = " + res[i]);
                        tempSet.add(res[i]);
                    }
                    Log.d("Friends", "tempSet 사이즈 = " + tempSet.size());
                    if(nameSet.equals(tempSet)){//내가 선택한 nameSet과 동일하면 데이터를 읽어들인다.
                        databaseReference2 = FirebaseDatabase.getInstance().getReference();
                        databaseReference2.child("ChatApp").child("ChatRoom").child(""+temp).child("Chat").child("Contents").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                    //Contents contents = new Contents();
                                    Contents contents = dataSnapshot1.getValue(Contents.class);
                                    contentItems.add(contents);
                                    Log.d("Friends", "contents의 name = " + contents.getName());
                                    Log.d("Friends", "contentItems의 첫번째 요소 name = " + contentItems.get(0).getName());
                                    //여기까지는 일단 값 제대로 들어감....
                                }
                                //어댑터설정
                                setInit();
                                /*fragment1CustomAdapter = new Fragment1CustomAdapter(context, items, requestManager, contentItems);
                                linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                                fragment1_recyclerView.setLayoutManager(linearLayoutManager);
                                fragment1_recyclerView.setAdapter(fragment1CustomAdapter);*/

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                        break;//for문 탈출
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        

        //fragment1CustomAdapter = new Fragment1CustomAdapter(context, items, requestManager);

        chattingroom_fragment1_editText = view.findViewById(R.id.chattingroom_fragment1_editText);
        chattingroom_fragment1_button = view.findViewById(R.id.chattingroom_fragment1_button);
        chattingroom_fragment1_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = new SimpleDateFormat("yy/MM/dd HH:mm").format(new Date());
                Contents inputData = new Contents(Login.MY_NAME, chattingroom_fragment1_editText.getText().toString(), date);
                Log.d("Friends", "input Name = " + inputData.getName());
                Log.d("Friends", "input Contents = " + inputData.getContent());
                Log.d("Friends", "input Time = " + inputData.getTime());
                chattingroom_fragment1_editText.setText("");

                //DB에 입력한 데이터 올리기
                databaseReference3 = FirebaseDatabase.getInstance().getReference();
                databaseReference3.child("ChatApp").child("ChatRoom").child(""+temp).child("Chat").child("Contents").push().setValue(inputData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

                //Adapter에 추가하기
                fragment1CustomAdapter.addItems(inputData);//어댑터에 Content 데이터 추가
                setInit();
            }
        });


        return view;
    }
}
