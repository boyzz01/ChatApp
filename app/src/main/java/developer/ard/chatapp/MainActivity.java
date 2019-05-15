package developer.ard.chatapp;

import android.app.AlertDialog;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private   String m_Text = "";

    private ViewPager mViewPager;
    private SectionPagerAdapter mSectionsPagerAdapter;

    private DatabaseReference mUserRef;

    private TabLayout mTabLayout;

    private DatabaseReference mUserDatabase;
    ProgressDialog pd,mProgressDialog;
    private DatabaseReference chatDatabase;
    private String level;


    public static SearchView searchView;
    public String query;

    private int position;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.tb);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);






        mAuth = FirebaseAuth.getInstance();



        mViewPager = findViewById(R.id.mainpager);
        mSectionsPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = findViewById(R.id.maintab);
        mTabLayout.setupWithViewPager(mViewPager);




        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                position = tab.getPosition();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



    }




    @Override
    public void onStart() {
        super.onStart();



        if (mAuth.getCurrentUser() != null)
        {
       //  Toast.makeText(this,mAuth.getCurrentUser().getUid().toString(),Toast.LENGTH_SHORT).show();

            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());


            mUserRef.child("login").setValue("iya");
            String deviceToken= FirebaseInstanceId.getInstance().getToken();
            mUserRef.child("online").setValue(1);
            mUserRef.child("device_token").setValue(deviceToken);


        }
        else
        {
            // Toast.makeText(this,"3",Toast.LENGTH_SHORT).show();
            sendToStart();
        }
    }

    private void sendToStart() {

        Intent startIntent = new Intent(MainActivity.this, login.class);
        startActivity(startIntent);
        finish();

    }



    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);


        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        if (mUserRef != null) {

            String level = MyApp.level;


            if (level.equals("Admin")) {
                menu.clear();
                getMenuInflater().inflate(R.menu.menu_admin, menu);
                SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
                searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                searchView.setMaxWidth(Integer.MAX_VALUE);

//
            } else {
                menu.clear();
                getMenuInflater().inflate(R.menu.menu_utama, menu);
                SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
                // SearchView searchView;
                searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                searchView.setMaxWidth(Integer.MAX_VALUE);

//            mUserRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.hasChild("level")) {
//                        final String level = dataSnapshot.child("level").getValue().toString();
//
//                        if (level.equals("Admin")) {
//                            menu.clear();
//                            getMenuInflater().inflate(R.menu.menu_admin, menu);
//                            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//                            searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
//                            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//                            searchView.setMaxWidth(Integer.MAX_VALUE);
//
////
//                        } else {
//                            menu.clear();
//                            getMenuInflater().inflate(R.menu.menu_utama, menu);
//                            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//                           // SearchView searchView;
//                            searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
//                            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//                            searchView.setMaxWidth(Integer.MAX_VALUE);
//
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });

            }
            // Associate searchable configuration with the SearchView

        }
            return true;
        }


//    private void cari(String query)
//    {
//        ChatFragment chatFragment;
//        switch (position)
//        {
//            case 0: {
//                chatFragment = (ChatFragment) mSectionsPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());
//                chatFragment.cari(query);
//            }
//            case 1:
//            {
//                GrupFragment grupFragment = (GrupFragment) getSupportFragmentManager().findFragmentById(R.id.grupLayout);
//                // grupFragment.cari(hasil);
//            }
//            case 2:
//            {
//                UserFragment userFragment = (UserFragment) getSupportFragmentManager().findFragmentById(R.id.userLayout);
//                // userFragment.cari(hasil);
//            }
//
//        }
        //  }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);


        if (item.getItemId() == R.id.keluar) {


            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
            mUserRef.child("login").setValue("tidak");
            mUserRef.child("device_token").setValue("");
            MyApp.level = "";

            FirebaseAuth.getInstance().signOut();
            sendToStart();

        }

        else
        if (item.getItemId() == R.id.setting) {


            Intent startIntent = new Intent(MainActivity.this, UserSetting.class);
            startActivity(startIntent);


        }

        else
            if (item.getItemId()== R.id.buatGrup)
            {
                BuatGrup();
            }

        return true;

    }

    private void BuatGrup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Buat Grup");


        final EditText input = new EditText(MainActivity.this);
        input.setHint("Nama Grup");


        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                mProgressDialog = new ProgressDialog(MainActivity.this);
                mProgressDialog.setTitle("Membuat Grup");
                mProgressDialog.setMessage("Please wait... ");
                mProgressDialog.show();

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Group").push();
                final String idGroup = reference.getKey();

                final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("GroupList").child(mAuth.getCurrentUser().getUid()).child(idGroup);

                final DatabaseReference grupref = FirebaseDatabase.getInstance().getReference("GroupMember").child(idGroup).child(mAuth.getCurrentUser().getUid());

                grupref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists())
                        {
                            grupref.child("id").setValue(mAuth.getCurrentUser().getUid());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                chatRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists())
                        {
                            chatRef.child("id").setValue(idGroup);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                HashMap<String, String> grupMap = new HashMap<>();
                grupMap.put("id",reference.getKey());
                grupMap.put("admin",mAuth.getCurrentUser().getUid());
                grupMap.put("nama",m_Text);
                grupMap.put("image", "default");
                grupMap.put("thumb_image", "default");
                grupMap.put("status", "Hallo Semua Salam Kenal");

                reference.setValue(grupMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProgressDialog.dismiss();
                        Toast.makeText(MainActivity.this,"Berhasil Membuat Grup Baru",Toast.LENGTH_SHORT).show();

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


}
