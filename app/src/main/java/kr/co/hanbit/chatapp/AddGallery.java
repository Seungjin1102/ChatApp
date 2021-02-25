package kr.co.hanbit.chatapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

public class AddGallery extends AppCompatActivity {//사진을 추가하는 화면


    public static final int PICK_IMAGE = 1;
    EditText addgallery_title;
    ImageView addgallery_photo;
    Button addgallery_ok;
    Button addgallery_cancel;

    DatabaseReference databaseReference;

    HashSet<String> nameSet;
    Iterator<String> iterator;

    private Uri photoUri;

    ArrayList<String> items = new ArrayList<>();
    String chatroomName = "";
    String imageString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addgallery_activity);

        addgallery_title = findViewById(R.id.addgallery_title);
        addgallery_photo = findViewById(R.id.addgallery_photo);
        addgallery_ok = findViewById(R.id.addgallery_ok);
        addgallery_cancel = findViewById(R.id.addgallery_cancel);

        Intent getIntent = getIntent();
        nameSet = (HashSet<String>) getIntent.getSerializableExtra("nameSet"); //채팅방 참여자의 이름이 들어간 집합 받는다.
        iterator = nameSet.iterator();

        while (iterator.hasNext()){
            items.add(iterator.next());//items에 nameSet에 있는 원소들을 저장
        }
        Collections.sort(items);//이름을 알파벳 순서에 따라서 정렬

        for(int i = 0; i < items.size(); i++){
            if(i == items.size() - 1){
                chatroomName+=(items.get(i));
            }else{
                chatroomName+=(items.get(i))+(",");
            }
        }


        addgallery_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGalleryImage();
            }
        });



        addgallery_ok.setOnClickListener(new View.OnClickListener() {//ok버튼 누르면 이미지 세팅시작
            @Override
            public void onClick(View v) {
                setGallery();
                finish();

            }
        });


        addgallery_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setGallery() {
        databaseReference = FirebaseDatabase.getInstance().getReference();

        Photo photo = new Photo(addgallery_title.getText().toString(), imageString);
        ChattingRoomFragment2.fragment2CustomAdapter.addItem(photo);

        databaseReference.child("ChatApp").child("ChatRoom").child(chatroomName).child("Gallery").child("Photo").push().setValue(photo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Gallery", "데이터 추가 성공");
            }
        });

    }

    private void getGalleryImage() {//갤러리에서 이미지 선택
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);//갤러리화면으로 이동
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE){
            if(data.getData() != null){
                photoUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                    imageString = BitmapToString(bitmap);
                    addgallery_photo.setImageBitmap(bitmap);//회원가입 이미지에 선택한 이미지를 설정
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



    }
    public static String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); //바이트 배열을 차례대로 읽어 들이기위한 ByteArrayOutputStream클래스 선언
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);//bitmap을 압축
        byte[] bytes = baos.toByteArray();//해당 bitmap을 byte배열로 바꿔준다.
        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);//Base 64 방식으로byte 배열을 String으로 변환
        return temp;//String을 retrurn
    }



}
