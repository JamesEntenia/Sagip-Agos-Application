package com.example.weareperpared;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    private ImageView connectionStatus;
    private EditText Name, Address, CellphoneNumber, Password;
    private Button Enter;
    private Users users;
    private Spinner UserType;

    boolean accountIsExisting = false;

    FirebaseDatabase database;
    DatabaseReference barangayRef, residentChild, connectedRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        connectionStatus = findViewById(R.id.regConnectionStatus);
        Name = findViewById(R.id.Name);
        Address = findViewById(R.id.Address);
        CellphoneNumber = findViewById(R.id.CellphoneNumber);
        Enter = findViewById(R.id.enter);
        Password = findViewById(R.id.Password);
        UserType = findViewById(R.id.UserType);


        database = FirebaseDatabase.getInstance();
        barangayRef = database.getReference("Barangay");
        //residentRef = barangayRef.child("Resident");
        //rescuerRef = barangayRef.child("Rescuer");
        //adminRef = barangayRef.child("Admin");


        checkIfFirebaseIsConnected();
        registerAccount();
        //RequestToEnableGPS();

    }

    public void checkIfFirebaseIsConnected() {

        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @SuppressLint("ResourceAsColor")
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


    int yourNumber = 0;

    public void registerAccount() {

        Enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                accountIsExisting = false;

                //Value Listener for barangay to read all children
                barangayRef.addListenerForSingleValueEvent(new ValueEventListener() {


                    @SuppressLint("ShowToast")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        yourNumber = (int) snapshot.getChildrenCount();

                        //loop to check the number and name of all registered account
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            //snapshot.hasChild("");
                            users = dataSnapshot.getValue(Users.class);
                            //if name is existing

                            if (users.name.equals(Name.getText().toString())) {

                                Toast.makeText(RegisterActivity.this, "Name is already registered", Toast.LENGTH_LONG).show();
                                Name.setText(null);
                                Name.setSelected(true);
                                accountIsExisting = true;
                                break;
                                //if number is existing
                            } else if (users.cellphoneNum.equals(CellphoneNumber.getText().toString())) {
                                Toast.makeText(RegisterActivity.this, "Number is already registered", Toast.LENGTH_LONG).show();
                                CellphoneNumber.setText(null);
                                accountIsExisting = true;
                                break;
                            }
                        }

                        try {
                            //if account is not existing, then register
                            if (!accountIsExisting) {
                                accountIsExisting = true;

                                UserHelper userHelper;
                                if (UserType.getSelectedItem().toString().equals("Rescuer")) {

                                    userHelper = new UserHelper(Name.getText().toString(), Address.getText().toString(),
                                            CellphoneNumber.getText().toString(), "no", "", "", Password.getText().toString(),
                                            UserType.getSelectedItem().toString(), "No rescuer assigned", "Not assigned yet", "", "online");

                                } else {
                                    userHelper = new UserHelper(
                                            Name.getText().toString(),
                                            Address.getText().toString(),
                                            CellphoneNumber.getText().toString(),
                                            "no",
                                            "", "",
                                            Password.getText().toString(),
                                            UserType.getSelectedItem().toString(),
                                            "No rescuer assigned",
                                            "online");
                                }
                                residentChild = barangayRef.child(Name.getText().toString());
                                residentChild.setValue(userHelper);


                                Intent intent = new Intent(getApplicationContext(), Map_Activity.class);
                                intent.putExtra("name", Name.getText().toString());
                                intent.putExtra("userType", UserType.getSelectedItem().toString());
                                startActivity(intent);
                            }

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Please avoid using ('.','#','$','[',']')", Toast.LENGTH_LONG).show();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(RegisterActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

    }


}