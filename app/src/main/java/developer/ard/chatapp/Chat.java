package developer.ard.chatapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import developer.ard.chatapp.adapter.PesanAdapter;
import developer.ard.chatapp.fragment.APIService;
import developer.ard.chatapp.model.Users;
import developer.ard.chatapp.model.Pesan;
import developer.ard.chatapp.notifikasi.Client;
import developer.ard.chatapp.notifikasi.Data;
import developer.ard.chatapp.notifikasi.MyResponse;
import developer.ard.chatapp.notifikasi.Sender;
import developer.ard.chatapp.notifikasi.Token;
import id.zelory.compressor.Compressor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Chat extends AppCompatActivity {


    private TextView username;
    private TextView mLastSeenView;
    private CircleImageView mProfileImage;

    PesanAdapter pesanAdapter;
    List<Pesan> pesanList;

    FirebaseUser firebaseUser;
    DatabaseReference reference, user;
    private StorageReference FileStorage;
    ImageButton kirim, addFile;
    EditText pesan_text;

    Intent intent;
    RecyclerView recyclerView;

    APIService apiService;
    private String IdPenerima;
    private String IdPengirim,namaPengirim;
    private String idpesan;

    String currentPhotoPath;


    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private static final int UPLOAD_FILE = 3;

    ValueEventListener seenListener;

    private String uriString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FileStorage = FirebaseStorage.getInstance().getReference();
        Toolbar toolbar = findViewById(R.id.chat_app_bar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);

        actionBar.setCustomView(action_bar_view);
        username = (TextView) findViewById(R.id.custom_bar_title);
        mLastSeenView = (TextView) findViewById(R.id.custom_bar_seen);
        mProfileImage = (CircleImageView) findViewById(R.id.custom_bar_image);

        recyclerView = findViewById(R.id.messages_list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((getApplicationContext()));
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);


        addFile = findViewById(R.id.addFile);

        kirim = findViewById(R.id.chat_send_btn);
        pesan_text = findViewById(R.id.chat_message_text);


        intent = getIntent();
        IdPenerima = intent.getStringExtra("id");

        IdPengirim = firebaseUser.getUid();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(IdPengirim);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                namaPengirim = dataSnapshot.child("nama").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (IdPengirim.compareTo(IdPenerima) < 0) {
            idpesan = firebaseUser.getUid() + IdPenerima;
        } else {
            idpesan = IdPenerima + firebaseUser.getUid();
        }
        Log.d("tes1", "" + idpesan);


        kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String pesan = pesan_text.getText().toString();
                if (!pesan.equals("")) {

                    kirimPesan(IdPengirim, IdPenerima, pesan, false);
                } else {

                }
                pesan_text.setText("");
            }
        });

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profil = new Intent(Chat.this,UserProfil.class);
                profil.putExtra("id",IdPenerima);
               startActivity(profil);
            }
        });

        addFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                CharSequence options[] = new CharSequence[]{"Ambil Gambar", "Pilih Gambar dari Galeri", "Upload File"};

                final AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);

                builder.setTitle("Select Options");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, final int i) {

                        //Click Event for each item.


                        if (i == 0) {
                            try {
                                 Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                                    File photoFile = null;
                                    try {
                                        photoFile = createImageFile();
                                    } catch (IOException ex) {
                                        // Error occurred while creating the File
                                    }
                                    // Continue only if the File was successfully created
                                    if (photoFile != null) {
                                        Uri photoURI = FileProvider.getUriForFile(Chat.this,
                                                "com.example.android.fileprovider",
                                                photoFile);
                                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                        startActivityForResult(takePictureIntent, REQUEST_CAMERA);

                                    }
                                }
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(Chat.this, "Gagal Membuka Kamera", Toast.LENGTH_SHORT).show();
                            }


                        }

                        if (i == 1) {


                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            try {
                                startActivityForResult(pickPhoto, SELECT_FILE);//
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(Chat.this, "Gagal Membuka Galeri", Toast.LENGTH_SHORT).show();
                            }
                        }

                        if (i == 2) {
                            Intent pickFile = new Intent(Intent.ACTION_GET_CONTENT);
                            pickFile.setType("*/*");
                            pickFile.addCategory(Intent.CATEGORY_OPENABLE);
                            try {
                                startActivityForResult(Intent.createChooser(pickFile, "Pilih File Yang Akan di Upload"), UPLOAD_FILE);

                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(Chat.this, "Gagal Membuka File Manajer", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });

                builder.show();


            }
        });


        reference = FirebaseDatabase.getInstance().getReference("Users").child(IdPenerima);
        user = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users friends = dataSnapshot.getValue(Users.class);
                username.setText(friends.getNama());
                long online = friends.getOnline();

                if (online == 1) {

                    mLastSeenView.setText("Online");

                } else {

                    GetTimeAgo getTimeAgo = new GetTimeAgo();

                    long lastTime = online;

                    String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, getApplicationContext());

                    mLastSeenView.setText(lastSeenTime);

                }


                if (friends.getThumb_image().equals("default")) {
                    mProfileImage.setImageResource(R.drawable.usera);
                } else {
                    Picasso.with(Chat.this).load(friends.getThumb_image()).placeholder(R.drawable.usera).into(mProfileImage);
                }

                bacaPesan(IdPengirim, IdPenerima, friends.getThumb_image(), friends.getNama());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seen(IdPenerima, idpesan);


    }


    private File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

        private void seen(final String userid, String idpesan) {
        Log.d("tes1", "" + idpesan);
        reference = FirebaseDatabase.getInstance().getReference("Chats").child(idpesan);
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Pesan pesan = snapshot.getValue(Pesan.class);
                    if (pesan.getPenerima().equals(firebaseUser.getUid()) && pesan.getPengirim().equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("seen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void kirimPesan(String pengirim, final String penerima, final String pesan, boolean seen) {
        String idpesan;

        if (pengirim.compareTo(penerima) < 0) {
            idpesan = pengirim + penerima;
        } else {
            idpesan = penerima + pengirim;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        //DatabaseReference listpesan = FirebaseDatabase.getInstance().getReference();

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(penerima);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(penerima);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(penerima)
                .child(firebaseUser.getUid());

        chatRefReceiver.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRefReceiver.child("id").setValue(firebaseUser.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String pushId = reference.child("Chats").child(idpesan).push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("pengirim", pengirim);
        hashMap.put("penerima", penerima);
        hashMap.put("pesan", pesan);
        hashMap.put("id",idpesan);
        hashMap.put("pushid",pushId);
        hashMap.put("type", "text");
        hashMap.put("waktu", ServerValue.TIMESTAMP);
        hashMap.put("seen", false);

        reference.child("Chats").child(idpesan).child(pushId).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sendNotification(penerima, namaPengirim, pesan);
            }
        });


    }

    private void sendNotification(String penerima, final String nama, final String pesan) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(penerima);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("notif", "1");
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, nama + ": " + pesan, "Pesan Baru", IdPenerima);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code() == 200) {

                                if (response.body().sukses != 1) {
                                    // Log.d("notif","3");
                                    //   Toast.makeText(Chat.this, "failed", Toast.LENGTH_SHORT).show();


                                }
                            }
                            // Log.d("notif",""+response);
                            //   Log.d("notif",""+response.code());
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                            Log.d("notif", "5");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void bacaPesan(final String myid, final String userid, final String imageurl, final String nama) {
        pesanList = new ArrayList<>();
        String idpesan;

        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        if (myid.compareTo(userid) < 0) {
            idpesan = myid + userid;
        } else {
            idpesan = userid + myid;
        }



        Log.d("tes", "" + idpesan);
        reference = FirebaseDatabase.getInstance().getReference("Chats").child(idpesan);
        reference.keepSynced(true);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pesanList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Pesan pesan = snapshot.getValue(Pesan.class);
                    if (pesan.getPenerima().equals(myid) && pesan.getPengirim().equals(userid) || pesan.getPenerima().equals(userid) && pesan.getPengirim().equals(myid)) {
                        pesanList.add(pesan);
                    }

                    pesanAdapter = new PesanAdapter(Chat.this, pesanList, imageurl, nama,userid);

                    recyclerView.setAdapter(pesanAdapter);
                    pesanAdapter.notifyDataSetChanged();



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onPause() {

        super.onPause();
        reference.removeEventListener(seenListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //

        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK) {


                    File file = new File(currentPhotoPath);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(Chat.this.getContentResolver(), Uri.fromFile(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (bitmap != null) {

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                        byte[] thumb_byte = baos.toByteArray();

                        pushFoto(thumb_byte);

                    }


                }

            }
            break;
            case 2: {
                if (resultCode == RESULT_OK) {

                    Uri imageUri = data.getData();

                    File path = new File(imageUri.getPath());

                    Bitmap bmp = null;
                    try {
                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                    byte[] thumb_byte = baos.toByteArray();

                    pushFoto(thumb_byte);


                }
            }
            break;

            case 3: {
                if (resultCode == RESULT_OK) {
                    Uri fileUri = data.getData();

                    String uriString = fileUri.toString();

                    File myFile = new File(uriString);
                    long length = myFile.length();
                    length = length / 1024;

                    Log.d("tes","1");
                    String displayname = null;
                    long besar = 0;

                    if (uriString.startsWith("content://"))
                    {
                        Cursor cursor = null;
                        try {
                            Log.d("tes","disini 1");
                            cursor = Chat.this.getContentResolver().query(fileUri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayname = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                besar = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        displayname = myFile.getName();
                    }

                        Log.d("tes","disini 2");

                        final DatabaseReference user_message_push = FirebaseDatabase.getInstance().getReference().child("Chats").child(idpesan);

                        user_message_push.keepSynced(false);

                        final String push_id = user_message_push.push().getKey();

                        final StorageReference riversRef = FileStorage.child("files/" + push_id + displayname);


                        final String finalDisplayname = displayname;
                        riversRef.putFile(fileUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                        // Get a URL to the uploaded content

                                        String ex = taskSnapshot.getMetadata().getContentType();
                                        String ukuran = taskSnapshot.getTotalByteCount() / (1024) + " kb";

                                        Map messageMap = new HashMap();
                                        messageMap.put("pengirim", IdPengirim);
                                        messageMap.put("penerima", IdPenerima);
                                        messageMap.put("id",idpesan);
                                        messageMap.put("pushid",push_id);
                                        messageMap.put("pesan", finalDisplayname);
                                        messageMap.put("type", "file");
                                        messageMap.put("waktu", ServerValue.TIMESTAMP);
                                        messageMap.put("seen", false);
                                        messageMap.put("ukuran", ukuran);
                                        messageMap.put("url", "files/" + push_id + finalDisplayname);
                                        messageMap.put("extension", ex);

                                        user_message_push.child(push_id).setValue(messageMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                sendNotification(IdPengirim, namaPengirim, finalDisplayname);
                                                Toast.makeText(Chat.this, "Berhasil Upload", Toast.LENGTH_SHORT).show();

                                            }
                                        });

                                        pesan_text.setText("");

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                        // ...
                                    }
                                });

                    }
                }
                break;


            }




        super.onActivityResult(requestCode, resultCode, data);

    }



    void pushFoto(byte[] file){
        final DatabaseReference user_message_push = FirebaseDatabase.getInstance().getReference().child("Chats").child(idpesan);

        user_message_push.keepSynced(false);

        final String push_id = user_message_push.push().getKey();

        final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("message_images").child(push_id + ".jpg");

        final UploadTask uploadTask = filepath.putBytes(file);

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("pengirim", IdPengirim);
                            hashMap.put("penerima", IdPenerima);
                            hashMap.put("id", idpesan);
                            hashMap.put("pushid", push_id);
                            hashMap.put("pesan", uri.toString());
                            hashMap.put("type", "image");
                            hashMap.put("waktu", ServerValue.TIMESTAMP);
                            hashMap.put("seen", false);
                            user_message_push.child(push_id).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    sendNotification(IdPengirim, namaPengirim, "Foto");
                                    Toast.makeText(Chat.this, "Berhasil Upload", Toast.LENGTH_SHORT).show();

                                }
                            });

                            pesan_text.setText("");
                        }
                    });
                }
            }
        });




    }


}
