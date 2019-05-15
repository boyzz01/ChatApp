package developer.ard.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import developer.ard.chatapp.adapter.AdapterTambah;
import developer.ard.chatapp.adapter.UserAdapter;
import developer.ard.chatapp.list.ChatList;
import developer.ard.chatapp.list.MemberGrupList;
import developer.ard.chatapp.model.Users;
import developer.ard.chatapp.notifikasi.Token;

public class TambahAnggota extends AppCompatActivity {


    private RecyclerView recyclerView;
    private AdapterTambah adapterTambah;
    private List<Users> mUser;

    private List<MemberGrupList> usersList;
    FirebaseUser firebaseUser;
    Intent intent;
    FloatingActionButton floatingActionButton;
    public boolean pilih=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_anggota);

        floatingActionButton = findViewById(R.id.fabAdd);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = findViewById(R.id.userlist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(TambahAnggota.this));



        usersList = new ArrayList<>();






        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {   intent = getIntent();
                final String grupId = intent.getStringExtra("grupId");
                for (Users model : mUser)
                {
                    if (model.isSelected)
                    {
                        pilih=true;
                    }
                }

                if (pilih)
                {

                    for (final Users model : mUser) {
                        if (model.isSelected()) {

                            final DatabaseReference grupref = FirebaseDatabase.getInstance().getReference("GroupMember").child(grupId).child(model.getId());
                            final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("GroupList").child(model.getId()).child(grupId);

                            grupref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        grupref.child("id").setValue(model.getId());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            chatRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        chatRef.child("id").setValue(grupId);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }

                    }
                    finish();

                }
                else
                {
                    Toast.makeText(TambahAnggota.this,"Belum Ada Data Yang dipilih",Toast.LENGTH_SHORT).show();
                }

                }





            });

        daftarAnggota();

    }

    private void daftarAnggota()
    {
        intent = getIntent();
        final String grupId = intent.getStringExtra("grupId");
        mUser = new ArrayList<>();

        DatabaseReference users = FirebaseDatabase.getInstance().getReference("Users");
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("GroupMember").child(grupId);


        mUser = new ArrayList<>();
        mUser.clear();
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        final DatabaseReference memberDatabase = FirebaseDatabase.getInstance().getReference().child("GroupMember").child(grupId);

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    final Users user = snapshot.getValue(Users.class);
                    memberDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.child(user.getId()).exists())
                            {
                                mUser.add(user);
                            }
                            adapterTambah = new AdapterTambah(TambahAnggota.this, mUser, true);
                            recyclerView.setAdapter(adapterTambah);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
    public void onStart() {

        super.onStart();





    }

    private void updateToken(String token)
    {
        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }
}
