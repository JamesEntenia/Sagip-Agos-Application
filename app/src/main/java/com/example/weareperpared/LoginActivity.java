package com.example.weareperpared;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.jar.Attributes;

public class LoginActivity extends AppCompatActivity {

    private EditText LoginNumber,LoginPassword;
    private Button LoginBtn,LoginRegisterBtn;
    private ImageView connectionStatus;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference barangayRef,residentChild,residentRef,rescuerRef,adminRef;

    DBHelper db;

    boolean passwordIsCorrect = false,redirecting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        LoginBtn = findViewById(R.id.LoginBtn);
        LoginNumber = findViewById(R.id.LoginNumber);
        LoginPassword = findViewById(R.id.LoginPassword);
        connectionStatus = findViewById(R.id.loginConnection);
        LoginRegisterBtn = findViewById(R.id.LoginRegisterBtn);

        firebaseDatabase = FirebaseDatabase.getInstance();
        barangayRef = firebaseDatabase.getReference("Barangay");

        //residentRef = barangayRef.child("Resident");
        //rescuerRef = barangayRef.child("Rescuer");
        //adminRef = barangayRef.child("Admin");

        //List<login_register_db> data = dataBaseHelper.getData();
        //Toast.makeText(LoginActivity.this,dataBaseHelper.getData().toString(),Toast.LENGTH_LONG).show();

        //delete
        //login_register_db loginRegisterDb = (login_register_db) ;
        //dataBaseHelper.deleteOne()


        checkIfFirebaseIsConnected();
        register();
        Login();
    }


    public void checkIfFirebaseIsConnected(){

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                boolean connected = snapshot.getValue(Boolean.class);


                if (connected) {
                    connectionStatus.setBackgroundResource(R.drawable.internet_connected);
                } else {
                    connectionStatus.setBackgroundResource(R.drawable.internet_disconnected);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void register() {
        LoginRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
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
                        if (!redirecting) {


                            // for each loop that reads all children
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Users users = dataSnapshot.getValue(Users.class);

                                //check if password and number is correct
                                if (users.cellphoneNum.equals(LoginNumber.getText().toString()) && users.password.equals(LoginPassword.getText().toString())) {

                                    passwordIsCorrect = true;

                                    //INSERTM DATA TO SQLITE
                                    db = new DBHelper(LoginActivity.this);

                                    boolean inserted = db.insert(users.name,users.password,users.userType, users.cellphoneNum);

                                    if(inserted){
                                        //Toast.makeText(LoginActivity.this,"Logged in",Toast.LENGTH_LONG).show();
                                    }

                                    //INTENT
                                    Intent intent = new Intent(LoginActivity.this, Map_Activity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra("name", users.name);
                                    intent.putExtra("userType", users.userType);
                                    startActivity(intent);

                                    //redirecting = true;

                                    LoginNumber.setText(null);
                                    LoginPassword.setText(null);


                                    break;

                                }
                            }

                            if (!passwordIsCorrect) {

                                //redirecting = false;
                                Toast.makeText(LoginActivity.this, "wrong password", Toast.LENGTH_LONG).show();
                                LoginPassword.setText(null);

                            }
                            passwordIsCorrect = false;
                        }
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


    private long backPressedTime = 0L;
    @Override
    public void onBackPressed() {

        if((backPressedTime+2000) > System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }else{
            Toast.makeText(LoginActivity.this,"Press again to exit",Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();

    }

}