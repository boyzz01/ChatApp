package developer.ard.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import developer.ard.chatapp.R;

public class UserProfil extends AppCompatActivity {

    TextView mNama,mStatus;
    CircleImageView mDisplayImage;
    private StorageReference mImageStorage;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profil);

        mNama = findViewById(R.id.namaProfilTxt);
        mStatus = findViewById(R.id.statusProfilTxt);
        mDisplayImage = findViewById(R.id.fotoProfil);

        Intent intent =  getIntent();
        String userId = intent.getStringExtra("id");

        mImageStorage = FirebaseStorage.getInstance().getReference();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        mUserDatabase.keepSynced(true);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.hasChild("nama") && dataSnapshot.hasChild("email") && dataSnapshot.hasChild("image") && dataSnapshot.hasChild("status")))
                {
                    String dname = dataSnapshot.child("nama").getValue().toString();
                    final String image = dataSnapshot.child("image").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();

                    mNama.setText(dname);
                    mStatus.setText(status);

                    if (!image.equals("default")) {

                        Picasso.with(UserProfil.this).load(image).placeholder(R.drawable.usera).into(mDisplayImage);

                        Picasso.with(UserProfil.this).load(image).placeholder(R.drawable.usera).into(mDisplayImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                                Picasso.with(UserProfil.this).load(image).placeholder(R.drawable.usera).into(mDisplayImage);

                            }
                        });
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }
}
