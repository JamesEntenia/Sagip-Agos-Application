package com.example.weareperpared;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.jar.Attributes;

public class LoginActivity extends AppCompatActivity {

    private EditText LoginNumber,LoginPassword;
    private Button LoginBtn,LoginRegisterBtn;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference barangayRef,residentChild,residentRef,rescuerRef,adminRef;

    boolean passwordIsCorrect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginBtn = findViewById(R.id.LoginBtn);
        LoginNumber = findViewById(R.id.LoginNumber);
        LoginPassword = findViewById(R.id.LoginPassword);
        LoginRegisterBtn = findViewById(R.id.LoginRegisterBtn);

        firebaseDatabase = FirebaseDatabase.getInstance();
        barangayRef = firebaseDatabase.getReference("Barangay");

        //residentRef = barangayRef.child("Resident");
        //rescuerRef = barangayRef.child("Rescuer");
        //adminRef = barangayRef.child("Admin");

        register();
        Login();
    }

    private void register() {
        LoginRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);


            }
        });
    }

    private void Login() {
        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                barangayRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // for each loop that reads all children
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            Users users = dataSnapshot.getValue(Users.class);

                            //check if password and number is correct
                            if(users.cellphoneNum.equals(LoginNumber.getText().toString())&&users.password.equals(LoginPassword.getText().toString())){

                                passwordIsCorrect = true;
                                Intent intent = new Intent(getApplicationContext(), Map_Activity.class);
                                intent.putExtra("name",users.name);
                                intent.putExtra("userType",users.userType);
                                startActivity(intent);

                                LoginNumber.setText(null);
                                LoginPassword.setText(null);
                                break;

                            }
                        }

                        if(!passwordIsCorrect){
                            Toast.makeText(LoginActivity.this,"wrong password",Toast.LENGTH_LONG).show();
                            LoginPassword.setText(null);

                        }
                        passwordIsCorrect = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });








                /*
                barangayRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(int counter = 0;counter<snapshot.getChildrenCount();counter++){

                            if((snapshot.child("Resident"+counter).child("cellphoneNum").getValue(String.class).equalsIgnoreCase(LoginNumber.getText().toString()))
                                    &&(snapshot.child("Resident"+counter).child("password").getValue(String.class).equals(LoginPassword.getText().toString()))){


                                passwordIsCorrect = true;
                                Intent intent = new Intent(getApplicationContext(), Map_Activity.class);
                                intent.putExtra("ResidentNumber","Resident"+counter);
                                startActivity(intent);

                                LoginNumber.setText(null);
                                LoginPassword.setText(null);
                                break;

                            }

                        }
                        if(!passwordIsCorrect){
                            Toast.makeText(LoginActivity.this,"wrong password",Toast.LENGTH_LONG).show();
                            LoginPassword.setText(null);

                        }
                        passwordIsCorrect = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/

            }
        });
    }
}