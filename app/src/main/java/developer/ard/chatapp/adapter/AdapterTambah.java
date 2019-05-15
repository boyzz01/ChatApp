package developer.ard.chatapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import developer.ard.chatapp.Chat;
import developer.ard.chatapp.R;
import developer.ard.chatapp.TambahAnggota;
import developer.ard.chatapp.model.Pesan;
import developer.ard.chatapp.model.Users;

public class AdapterTambah extends RecyclerView.Adapter <AdapterTambah.ViewHolder>{
    private Context context;
    private List<Users> friend;
    public CircleImageView profile_image;
    String pesanterakhir;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    private boolean isChat;



    public AdapterTambah(Context context, List<Users> friend, boolean isChat)
    {
        this.context = context;
        this.friend = friend;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public AdapterTambah.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_single_layout,parent,false);

        return new AdapterTambah.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterTambah.ViewHolder holder, int position) {
        final Users friends = friend.get(position);
        holder.nama.setText(friends.getNama());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        if (friends.getImage().equals("default"))
        {
            profile_image.setImageResource(R.drawable.usera);
        }
        else {
            Picasso.with(context).load(friends.getImage()).placeholder(R.drawable.usera).into(profile_image);
        }

        holder.itemView.setBackgroundColor(friends.isSelected() ? Color.BLUE : Color.WHITE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               friends.setSelected(!friends.isSelected());
                holder.itemView.setBackgroundColor(friends.isSelected() ? Color.BLUE : Color.WHITE);


            }
        });




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


}
