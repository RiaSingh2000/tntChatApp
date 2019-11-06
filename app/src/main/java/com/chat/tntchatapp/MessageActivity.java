package com.chat.tntchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.bumptech.glide.Glide;
import com.chat.tntchatapp.Adapters.MessageAdapter;
import com.chat.tntchatapp.Models.Chats;
import com.chat.tntchatapp.Models.Users;
import com.chat.tntchatapp.Notifications.APIService;
import com.chat.tntchatapp.Notifications.Client;
import com.chat.tntchatapp.Notifications.Data;
import com.chat.tntchatapp.Notifications.MyResponse;
import com.chat.tntchatapp.Notifications.Sender;
import com.chat.tntchatapp.Notifications.Token;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MessageActivity extends AppCompatActivity {
    CircleImageView image;
    TextView user_name;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    ImageButton send;
    EditText text;
    ImageView camera;
    String mUri,url;
    Bitmap bitmap;

    MessageAdapter messageAdapter;
    ArrayList<Chats>msg_list;

    RecyclerView recyclerView;

    APIService apiService;
    String userId;
    boolean notify=false;
    StorageReference storageReference;

    private static final int IMAGE_REQUEST=1;
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        image=findViewById(R.id.image);
        user_name=findViewById(R.id.userName);
        text=findViewById(R.id.text);
        send=findViewById(R.id.send);
        camera=findViewById(R.id.camera);

        storageReference= FirebaseStorage.getInstance().getReference("uploads");

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        recyclerView=findViewById(R.id.messages);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        Intent intent=getIntent();
        userId=intent.getStringExtra("userId");
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(userId);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify=true;
                uploadImage();
                text.setText("");
                image_uri=null;
                camera.setImageResource(R.drawable.camera);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users=dataSnapshot.getValue(Users.class);
                user_name.setText(users.getUserName());

                if(users.getImageUrl().equals("default"))
                    image.setImageResource(R.drawable.images);
                else
                    Glide.with(MessageActivity.this).load(users.getImageUrl()).into(image);

                receiveMessage(firebaseUser.getUid(),userId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MessageActivity.this, "Error\n"+databaseError, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getMimeTypeFromExtension(contentResolver.getType(uri));
    }
    Uri image_uri;
    private void uploadImage(){
        final String msg=text.getText().toString();
        if(image_uri!=null){
            final ProgressDialog pd=new ProgressDialog(this);
            pd.setTitle("Uploading...");
            pd.show();
                final StorageReference fileReference=storageReference.child(System.currentTimeMillis()+"."
                        +getFileExtension(image_uri));
                uploadTask=fileReference.putFile(image_uri);
                uploadTask.continueWithTask(new Continuation< UploadTask.TaskSnapshot,Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task task) throws Exception {
                        if(!task.isSuccessful())
                            throw task.getException();

                        return fileReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            Uri downloadUri=task.getResult();
                            mUri=downloadUri.toString();
                            Toast.makeText(MessageActivity.this, "mUri"+mUri, Toast.LENGTH_SHORT).show();
                            sendMessage(msg,userId, firebaseUser.getUid(), mUri);
                            pd.dismiss();
                        }
                        else {
                            Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MessageActivity.this, "Failed!!!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });
        }else {
            sendMessage(msg,userId, firebaseUser.getUid(), "");
            //Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            image_uri = data.getData();
            try {
               bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image_uri);
               camera.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(String message,final String receiver,String sender,String mUri){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        if(mUri=="")
            hashMap.put("image_uri","");
        else
            hashMap.put("image_uri",mUri);


        databaseReference.child("Chats").push().setValue(hashMap);

        final DatabaseReference chatRef=FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(userId);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatRef.child("id").setValue(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        final String msg=message;
        databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users=dataSnapshot.getValue(Users.class);
                if(notify) {
                    sendNotifications(receiver, users.getUserName(), msg);
                    notify = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendNotifications(String receiver, final String userName, final String msg) {
        DatabaseReference token=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=token.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Token token=snapshot.getValue(Token.class);
                    Data data=new Data(firebaseUser.getUid(),userName+":"+msg,"New Notification",
                            userId,R.mipmap.ic_launcher);

                    assert token != null;
                    Sender sender=new Sender(data,token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code()==200){
                                        if(response.body().success!=1){
                                            Toast.makeText(MessageActivity.this, "Failed:(", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Toast.makeText(MessageActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void receiveMessage(final String myId, final String userId){
        msg_list=new ArrayList<>();
         databaseReference=FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                msg_list.clear();;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    Chats chats = snapshot.getValue(Chats.class);
                    assert chats != null;
                    if((chats.getReceiver().equals(myId)&&chats.getSender().equals(userId))||
                            ((chats.getReceiver().equals(userId)&&chats.getSender().equals(myId)))){
                        msg_list.add(chats);
                    }
                    messageAdapter=new MessageAdapter(MessageActivity.this,msg_list);
                    recyclerView.setAdapter(messageAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void status(String status){
        databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);

        databaseReference.updateChildren(hashMap);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
