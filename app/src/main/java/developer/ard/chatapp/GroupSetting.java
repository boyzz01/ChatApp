package developer.ard.chatapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import developer.ard.chatapp.adapter.UserAdapter;
import developer.ard.chatapp.adapter.UserGrupAdapter;
import developer.ard.chatapp.list.MemberGrupList;
import developer.ard.chatapp.model.Users;
import id.zelory.compressor.Compressor;

public class GroupSetting extends AppCompatActivity {

    private static final int GALLERY_PICK = 1;
    private RecyclerView recyclerView;
    private UserGrupAdapter userGrupAdapter;
    private List<Users> mUser;
    FirebaseUser firebaseUser;
    private List<MemberGrupList> memberGrupList;

    TextView namaGrupTxt;
    ImageButton editNama,gantiFoto;
    ImageView gambarGrup;
    FloatingActionButton addAnggota;
    ProgressDialog mProgressDialog;
    public String grupId="";
    String m_Text;

    // Storage Firebase
    private StorageReference mImageStorage;
    private DatabaseReference mGrupDatabase;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_setting);

        Intent intent;
        intent = getIntent();
         grupId = intent.getStringExtra("id");

        namaGrupTxt = findViewById(R.id.namaGrup);
        editNama = findViewById(R.id.editNamaGrup);
        gambarGrup = findViewById(R.id.FotoGrup);
        addAnggota = findViewById(R.id.Tambah);
        gantiFoto = findViewById(R.id.gantiFotoGrup);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = findViewById(R.id.anggotalist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(GroupSetting.this));

        mImageStorage = FirebaseStorage.getInstance().getReference();
        memberGrupList= new ArrayList<>();

        mGrupDatabase = FirebaseDatabase.getInstance().getReference().child("Group").child(grupId);
        mGrupDatabase.keepSynced(true);


        editNama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Group").child(grupId);
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupSetting.this);
                builder.setTitle("Ganti Status");

                final EditText input = new EditText(GroupSetting.this);
                input.setText(namaGrupTxt.getText().toString());


                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        mProgressDialog = new ProgressDialog(GroupSetting.this);
                        mProgressDialog.setTitle("Saving Changes");
                        mProgressDialog.setMessage("Please wait while we save the changes");
                        mProgressDialog.show();
                        databaseReference.child("nama").setValue(m_Text).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    mProgressDialog.dismiss();

                                } else {

                                    Toast.makeText(getApplicationContext(), "Gagal Mengganti Nama", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
        addAnggota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add = new Intent(GroupSetting.this,TambahAnggota.class);
                add.putExtra("grupId",grupId);
                startActivity(add);

            }
        });
        gantiFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Group").child(grupId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                namaGrupTxt.setText(dataSnapshot.child("nama").getValue().toString() );
                final String image = dataSnapshot.child("image").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                if (!image.equals("default")) {

                    Picasso.with(GroupSetting.this).load(image).placeholder(R.drawable.usera).into(gambarGrup);

                    Picasso.with(GroupSetting.this).load(image).placeholder(R.drawable.usera).into(gambarGrup, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d("tes","sukses");
                        }

                        @Override
                        public void onError() {
                            Log.d("tes","error");
                            Picasso.with(GroupSetting.this).load(image).placeholder(R.drawable.usera).into(gambarGrup);

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




                daftarAnggota(grupId);
    }


    private void daftarAnggota(final String grupId)
    {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("GroupMember").child(grupId);
        reference.keepSynced(true);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                memberGrupList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MemberGrupList anggota = snapshot.getValue(MemberGrupList.class);
                    memberGrupList.add(anggota);
                }

                mUser = new ArrayList<>();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mUser.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Users user = snapshot.getValue(Users.class);
                            for (MemberGrupList anggota :  memberGrupList){
                                if (user.getId().equals(anggota.getId())){
                                    mUser.add(user);
                                }
                            }
                        }
                        userGrupAdapter = new UserGrupAdapter(GroupSetting.this, mUser, false,grupId);
                        recyclerView.setAdapter(userGrupAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);

            //Toast.makeText(SettingsActivity.this, imageUri, Toast.LENGTH_LONG).show();

        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {


                mProgressDialog = new ProgressDialog(GroupSetting.this);
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("Please wait....");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();


                Uri resultUri = result.getUri();

                File thumb_filePath = new File(resultUri.getPath());




                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_filePath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();


                final StorageReference filepath = mImageStorage.child("profile_image").child(grupId + ".jpg");
                final StorageReference thumb_filepath = mImageStorage.child("profile_image").child("thumbs").child(grupId+ ".jpg");




                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {





                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {



                                    if (thumb_task.isSuccessful()) {

                                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String download_url = uri.toString();
                                                Map update_hashMap = new HashMap();
                                                update_hashMap.put("image", ""+download_url);
                                                mGrupDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()) {

                                                        }

                                                    }
                                                });
                                            }
                                        });

                                        thumb_filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String thumb_downloadUrl = uri.toString();
                                                Map update_hashMap = new HashMap();
                                                update_hashMap.put("thumb_image", ""+thumb_downloadUrl);
                                                mGrupDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()) {

                                                            mProgressDialog.dismiss();
                                                            Toast.makeText(GroupSetting.this, "Berhasil Mengganti Foto.", Toast.LENGTH_LONG).show();

                                                        }

                                                    }
                                                });
                                            }
                                        });







                                    } else {

                                        Toast.makeText(GroupSetting.this, "Gagal mengganti foto", Toast.LENGTH_LONG).show();
                                        mProgressDialog.dismiss();

                                    }


                                }
                            });


                        } else {

                            Toast.makeText(GroupSetting.this, "Error in uploading.", Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();

                        }

                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }
}
