package developer.ard.chatapp.fragment;


import android.app.Activity;
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
import developer.ard.chatapp.adapter.chatAdapter;
import developer.ard.chatapp.list.ChatList;

import developer.ard.chatapp.model.ChatUsers;
import developer.ard.chatapp.notifikasi.Token;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {



    private RecyclerView recyclerView;
    private chatAdapter userAdapter;
    private List<ChatUsers> mUser,searchList;
    FirebaseUser firebaseUser;
    private SearchAdapter searchAdapter;

    private List<ChatList> ChatUsersList;

    public ChatFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_user,container,false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = view.findViewById(R.id.friends_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ChatUsersList= new ArrayList<>();
        searchList = new ArrayList<>();

        daftarChat();


        updateToken(FirebaseInstanceId.getInstance().getToken());
        return view;


    }

    private void daftarChat()
    {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chatlist").child(firebaseUser.getUid());
        reference.keepSynced(true);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               ChatUsersList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatList chatList = snapshot.getValue(ChatList.class);
                    ChatUsersList.add(chatList);
                }

                mUser = new ArrayList<>();
                DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference("Users");
                userDatabase.keepSynced(true);
                userDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mUser.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            ChatUsers user = snapshot.getValue(ChatUsers.class);
                            for (ChatList chatlist : ChatUsersList){
                            //    Log.d("tes","User "+user.getId()+" Member "+chatlist.getId());
                                if (user.getId().equals(chatlist.getId())){
                                    mUser.add(user);
                                }
                            }
                        }
                        userAdapter = new chatAdapter(getContext(), mUser, true);
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


    @Override
    public void onStart() {

        super.onStart();





    }

//    public void cari(String hasil)
//    {
//        Log.d("tes","berhasil");
//        searchList.clear();
//        searchList.addAll(userAdapter.filter(hasil));
//        searchAdapter = new SearchAdapter(getContext(),searchList,false);
//        recyclerView.setAdapter(searchAdapter);
//    }

    private void updateToken(String token)
    {
        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }



}
