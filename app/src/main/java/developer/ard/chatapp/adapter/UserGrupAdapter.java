package developer.ard.chatapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import developer.ard.chatapp.Chat;
import developer.ard.chatapp.MyApp;
import developer.ard.chatapp.R;
import developer.ard.chatapp.UserProfil;
import developer.ard.chatapp.model.Pesan;
import developer.ard.chatapp.model.Users;

public class UserGrupAdapter extends RecyclerView.Adapter <UserGrupAdapter.ViewHolder>{
    private Context context;
    private List<Users> friend,itemsCopy;
    public CircleImageView profile_image;
    String pesanterakhir;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    private boolean isChat;
    String grupId;

    public UserGrupAdapter(Context context, List<Users> friend, boolean isChat,String grupId)
    {
        this.context = context;
        this.friend = friend;
        this.isChat = isChat;
        this.itemsCopy = new ArrayList<>();
        this.grupId = grupId;
        itemsCopy.addAll(friend);
    }

    @NonNull
    @Override
    public UserGrupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_single_layout,parent,false);

        return new UserGrupAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserGrupAdapter.ViewHolder holder, int position) {
        final Users friends = friend.get(position);
        holder.nama.setText(friends.getNama());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (friends.getThumb_image().equals("default"))
        {
            profile_image.setImageResource(R.drawable.usera);
        }
        else {
            Picasso.with(context).load(friends.getThumb_image()).placeholder(R.drawable.usera).into(profile_image);
        }

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profil = new Intent(context,UserProfil.class);
                profil.putExtra("id",friends.getId());
                context.startActivity(profil);
            }
        });



        if (!firebaseUser.getUid().equals(friends.getId())) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final CharSequence options[] = new CharSequence[]{"Hapus Dari Grup"};

                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setTitle("Hapus Dari Grup?");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, final int i) {


                            if (i == 0) {

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("GroupMember").child(grupId).child(friends.getId());
                                databaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        DatabaseReference out = FirebaseDatabase.getInstance().getReference().child("GroupList").child(friends.getId()).child(grupId);
                                        out.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(context, "Berhasil Dihapus Dari Grup", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });

                            }


                        }
                    });

                    builder.show();
                    return false;
                }
            });


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, Chat.class);
                    intent.putExtra("id", friends.getId());
                    intent.putExtra("nama", friends.getNama());
                    intent.putExtra("image", friends.getThumb_image());
                    context.startActivity(intent);
                }
            });
        }

            if (isChat) {
                String idpesan;


                firebaseAuth = FirebaseAuth.getInstance();
                if (firebaseAuth.getCurrentUser() != null) {
                    if (firebaseUser.getUid().compareTo(friends.getId()) < 0) {
                        idpesan = firebaseUser.getUid() + friends.getId();
                    } else {
                        idpesan = friends.getId() + firebaseUser.getUid();
                    }
                    pesanTerakhir(friends.getId(), idpesan, holder.pesanTxt);

                }
            } else {
                holder.pesanTxt.setText(friends.status);
            }


    }

    @Override
    public int getItemCount() {
        return friend.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView nama;
        public TextView pesanTxt;



        public ViewHolder(View itemView) {
            super(itemView);

            nama= itemView.findViewById(R.id.user_single_name);
            profile_image = itemView.findViewById(R.id.user_single_image);
            pesanTxt=itemView.findViewById(R.id.last_message);



        }
    }

    private void pesanTerakhir(String userid, String idpesan, final TextView last_msg)
    {
        pesanterakhir="default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(idpesan);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Pesan pesan = snapshot.getValue(Pesan.class);

                    pesanterakhir = pesan.getPesan();


                }



                switch (pesanterakhir)
                {
                    case "default":
                        last_msg.setText("Belum Ada Pesan");
                        break;

                    default:
                        last_msg.setText(pesanterakhir);
                        break;


                }
                pesanterakhir="default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
