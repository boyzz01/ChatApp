package developer.ard.chatapp.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import developer.ard.chatapp.adapter.grupadapter;
import developer.ard.chatapp.model.Grup;
import developer.ard.chatapp.list.GrupList;
import developer.ard.chatapp.model.Users;
import developer.ard.chatapp.notifikasi.Token;


/**
 * A simple {@link Fragment} subclass.
 */
public class GrupFragment extends Fragment {

    private RecyclerView recyclerView;
    private grupadapter grupadapter;
    private List<Grup> mGrup;
    FirebaseUser firebaseUser;
    private List<Users> friendsList,searchList;
    private SearchAdapter searchAdapter;

    private List<GrupList> grupLists;






    public GrupFragment() {
        // Required empty public constructor

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_grup,container,false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = view.findViewById(R.id.friends_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        grupLists = new ArrayList<>();

        daftargrup();
//        SearchView searchView;
//        searchView = MainActivity.searchView;
//

        // listening to search query text change
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String hasil) {
//                // filter recycler view when query submitted
//                searchList.clear();
//                searchList.addAll(grupadapter.filter(hasil));
//                searchAdapter = new SearchAdapter(getContext(),searchList,false);
//                recyclerView.setAdapter(searchAdapter);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String hasil) {
//                // filter recycler view when text is changed
//                searchList.clear();
//                searchList.addAll(grupadapter.filter(hasil));
//                searchAdapter = new SearchAdapter(getContext(),searchList,false);
//                recyclerView.setAdapter(searchAdapter);
//                return false;
//            }
//        });

        updateToken(FirebaseInstanceId.getInstance().getToken());
        return view;


    }

    private void daftargrup()
    {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("GroupList").child(firebaseUser.getUid());
        reference.keepSynced(true);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                grupLists.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GrupList grupList = snapshot.getValue(GrupList.class);
                    grupLists.add(grupList);
                }

                mGrup = new ArrayList<>();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Group");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mGrup.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Grup grup = snapshot.getValue(Grup.class);
                            for (GrupList grupList: grupLists){
                                if (grup.getId().equals(grupList.getId())){
                                    mGrup.add(grup);
                                }
                            }
                        }
                        grupadapter = new grupadapter(getContext(), mGrup, true);
                        recyclerView.setAdapter(grupadapter);
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

    private void updateToken(String token)
    {
        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }








}
