package kr.co.hanbit.chatapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.Script;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Join extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;

    private EditText join_name;
    private EditText join_password;
    private EditText join_passwordck;
    private ImageView join_imageView;
    private Button join_ok;
    private Button join_cancel;
    private Uri photoUri;
    //String str = "testFirends";

    String name;
    String password;
    String passwordck;
    DatabaseReference databaseReference;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();//참조 만들기

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_activity);

        join_name = findViewById(R.id.join_name);
        join_password = findViewById(R.id.join_password);
        join_passwordck = findViewById(R.id.join_passwordck);
        join_imageView = findViewById(R.id.join_imageView);
        join_ok = findViewById(R.id.join_ok);
        join_cancel = findViewById(R.id.join_cancel);


        join_imageView.setOnClickListener(new View.OnClickListener() {//이미지 클릭
            @Override
            public void onClick(View v) {
                getGalleryImage();
            }
        });



        join_ok.setOnClickListener(new View.OnClickListener() {//확인버튼 누를시 데이터 추가 실시
            @Override
            public void onClick(View v) {
                try {
                    saveJoin();
                    finish();//액티비티 종료
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }



    private void saveJoin() throws IOException {//입력된 사용자 정보로 회원가입, 파이어베이스에 데이터 저장

        name = join_name.getText().toString();//사용자 이름
        password = join_password.getText().toString();//비밀번호
        passwordck = join_passwordck.getText().toString();//비밀번호 확인

        if(password.equals(passwordck)){//비밀번호와 비밀번화 확인 일치시
            uploadFile();//이미지 파일을 스토리지에 업로드
            MyData myData = new MyData(name, password);
            databaseReference = FirebaseDatabase.getInstance().getReference();//데이터베이스에 로그인정보 올리기
            databaseReference.child("ChatApp").child("LoginData").child(""+name).setValue(myData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {//데이터 추가 완료시
                    Toast.makeText(getApplicationContext(), "데이터 추가 완료", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {//데이터 추가 실패시
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "데이터 추가 실패", Toast.LENGTH_SHORT).show();
                }
            });

            databaseReference.child("ChatApp").child("LoginData").child(""+name).child("Friends_" +name).child(""+name).setValue(""+name).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }else{
            Toast.makeText(getApplicationContext(), "비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
            //입력창 초기화
            join_name.setText("");
            join_password.setText("");
            join_passwordck.setText("");
        }

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
                    join_imageView.setImageBitmap(bitmap);//회원가입 이미지에 선택한 이미지를 설정
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



    }

    public void uploadFile() throws IOException {//이미지 업로드 메소드
        if(photoUri != null){//uri가 있으면 업로드 시작

            StorageReference profileRef = storageRef.child("userProfiles/" + name);// '/'붙이면 폴더 안으로, 사용자 이름으로 Profile 저장
            UploadTask uploadTask = profileRef.putFile(photoUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "이미지 업로드 성공", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

