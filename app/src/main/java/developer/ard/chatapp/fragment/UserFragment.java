package developer.ard.chatapp.fragment;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import developer.ard.chatapp.MainActivity;
import developer.ard.chatapp.R;
import developer.ard.chatapp.adapter.SearchAdapter;
import developer.ard.chatapp.adapter.UserAdapter;
import developer.ard.chatapp.model.Users;
import developer.ard.chatapp.notifikasi.Token;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter adapterfriend;
    private SearchAdapter searchAdapter;
    private List<Users> friendsList,searchList;
    FirebaseUser firebaseUser;

    MainActivity mainActivity;


    public UserFragment() {
        // Required empty public constructor

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_user,container,false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = view.findViewById(R.id.friends_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        friendsList = new ArrayList<>();
        searchList = new ArrayList<>();

        SearchView searchView;
        searchView = MainActivity.searchView;


        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String hasil) {
                // filter recycler view when query submitted
                searchList.clear();
                searchList.addAll(adapterfriend.filter(hasil));
                searchAdapter = new SearchAdapter(getContext(),searchList,false);
                recyclerView.setAdapter(searchAdapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String hasil) {
                // filter recycler view when text is changed
                searchList.clear();
                searchList.addAll(adapterfriend.filter(hasil));
                searchAdapter = new SearchAdapter(getContext(),searchList,false);
                recyclerView.setAdapter(searchAdapter);
                return false;
            }
        });










        daftarteman();


        updateToken(FirebaseInstanceId.getInstance().getToken());
        return view;


    }

    private void daftarteman()
    {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.keepSynced(true);



        reference.orderByChild("nama").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendsList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Users friends = snapshot.getValue(Users.class);

                    assert friends!=null;
                    assert firebaseUser !=null;

                    if (!friends.getId().equals(firebaseUser.getUid()))
                    {

                        friendsList.add(friends);



                    }
                }

                adapterfriend = new UserAdapter(getContext(),friendsList,false);
                recyclerView.setAdapter(adapterfriend);
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


    public void setTestString(String text) {

    }





}
