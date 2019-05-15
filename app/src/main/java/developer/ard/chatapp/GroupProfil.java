package developer.ard.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import developer.ard.chatapp.adapter.UserAdapter;
import developer.ard.chatapp.list.MemberGrupList;
import developer.ard.chatapp.model.Users;

public class GroupProfil extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<Users> mUser;
    FirebaseUser firebaseUser;
    private List<MemberGrupList> memberGrupList;

    TextView namaGrupTxt;
    ImageButton editNama,gantiFoto;
    ImageView gambarGrup;
    public String grupId="";
    String m_Text;

    // Storage Firebase
    private StorageReference mImageStorage;
    private DatabaseReference mGrupDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profil);

        Intent intent;
        intent = getIntent();
        grupId = intent.getStringExtra("id");

        namaGrupTxt = findViewById(R.id.namaGrupTxtGP);
        gambarGrup = findViewById(R.id.fotoGP);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = findViewById(R.id.anggotalistGP);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(GroupProfil.this));

        mImageStorage = FirebaseStorage.getInstance().getReference();
        memberGrupList= new ArrayList<>();

        mGrupDatabase = FirebaseDatabase.getInstance().getReference().child("Group").child(grupId);
        mGrupDatabase.keepSynced(true);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Group").child(grupId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                namaGrupTxt.setText(dataSnapshot.child("nama").getValue().toString() );
                final String image = dataSnapshot.child("image").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                if (!image.equals("default")) {

                    Picasso.with(GroupProfil.this).load(image).placeholder(R.drawable.usera).into(gambarGrup);

                    Picasso.with(GroupProfil.this).load(image).placeholder(R.drawable.usera).into(gambarGrup, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d("tes","sukses");
                        }

                        @Override
                        public void onError() {
                            Log.d("tes","error");
                            Picasso.with(GroupProfil.this).load(image).placeholder(R.drawable.usera).into(gambarGrup);

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

    private void daftarAnggota(String grupId)
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
                        userAdapter = new UserAdapter(GroupProfil.this, mUser, false);
                        recyclerView.setAdapter(userAdapter);
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
}
