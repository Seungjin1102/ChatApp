package kr.co.hanbit.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class Login extends AppCompatActivity {

    private Button button_login;
    private Button button_join;
    private EditText name;
    private EditText password;
    private DatabaseReference databaseReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();


    public static String MY_NAME;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        button_login = findViewById(R.id.button_login);
        button_join = findViewById(R.id.button_join);
        name = findViewById(R.id.name);
        password = findViewById(R.id.password);

        button_join.setOnClickListener(new View.OnClickListener() {//회원가입창으로 이동
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Join.class);
                startActivity(intent);
            }
        });



        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String inputName = name.getText().toString();
                String inputPassword = password.getText().toString();
                databaseReference = database.getReference();
                databaseReference.child("ChatApp").child("LoginData").child("" + inputName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        MyData temp = snapshot.getValue(MyData.class);
                        Log.d("MainActivity", "2. name = " + temp.getName() + ", password = " + temp.getPassword());//데이터 정상적으로 가져와짐
                        if(inputName.equals(temp.getName()) && inputPassword.equals(temp.getPassword())){//로그인정보가 일치할시
                            MY_NAME = inputName;
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);

                        }else {
                            name.setText("");
                            password.setText("");
                            Toast.makeText(getApplicationContext(), "이름과 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "데이터 다운로드 실패", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }





}
