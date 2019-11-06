package com.chat.tntchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistryActivity extends AppCompatActivity {

    TextInputEditText email,pass,user;
    FloatingActionButton register;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);
        email=findViewById(R.id.email);
        pass=findViewById(R.id.pass);
        user=findViewById(R.id.user_name);
        register=findViewById(R.id.register);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ChatApp");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email=email.getText().toString();
                String password=pass.getText().toString();
                String userName=user.getText().toString();

                if(!Email.isEmpty()||!password.isEmpty()||!userName.isEmpty()) {
                    register(Email, password, userName);
                    Toast.makeText(RegistryActivity.this, "Reached!!!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(RegistryActivity.this, "Enter the details", Toast.LENGTH_SHORT).show();
                    }
                if (password.length() < 6)
                    Toast.makeText(RegistryActivity.this, "Password must contain atleast 6 digits", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void register(final String email, String password,final String userName){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            String userId=user.getUid();

                            reference= FirebaseDatabase.getInstance().getReference("Users").child(userId);


                            HashMap<String,String> hashMap=new HashMap<>();
                            hashMap.put("id",userId);
                            hashMap.put("email",email);
                            hashMap.put("userName",userName);
                            hashMap.put("imageUrl","default");
                            hashMap.put("search",userName.toLowerCase());


                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        //Toast.makeText(RegistryActivity.this, "Reached", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(RegistryActivity.this,MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                           /* FirebaseAuthException exception=(FirebaseAuthException)task.getException();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegistryActivity.this, "You can't register with this email or password\n"+exception, Toast.LENGTH_SHORT).show();*/

                        }

                        // ...
                    }
                });
    }
}
