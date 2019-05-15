package developer.ard.chatapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import developer.ard.chatapp.GroupChat;
import developer.ard.chatapp.R;
import developer.ard.chatapp.model.Grup;
import developer.ard.chatapp.model.PesanGrup;
import developer.ard.chatapp.model.Users;
import developer.ard.chatapp.model.Pesan;

public class grupadapter extends RecyclerView.Adapter <grupadapter.ViewHolder>{

    private Context context;
    private List<Grup> mGrup,itemsCopy;
    public CircleImageView profile_image;
    String pesanterakhir;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    private boolean isChat;

    public grupadapter(Context context, List<Grup> mGrup,boolean isChat)
    {
        this.context = context;
        this.mGrup = mGrup;
        this.isChat = isChat;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_single_layout,parent,false);

        return new grupadapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Grup grup = mGrup.get(position);
        holder.nama.setText(grup.getNama());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (grup.getImage().equals("default"))
        {
            profile_image.setImageResource(R.drawable.usera);
        }
        else {
            Picasso.with(context).load(grup.getImage()).placeholder(R.drawable.usera).into(profile_image);
        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,GroupChat.class);
                intent.putExtra("id",grup.getId());
                intent.putExtra("nama",grup.getNama());
                intent.putExtra("image",grup.getImage());
                context.startActivity(intent);
            }
        });

        if (isChat) {
            String idpesan;


                pesanTerakhir(grup.getId(), holder.pesanTxt);


        }
        else
        {
            holder.pesanTxt.setText("Belum Ada Pesan");
        }


    }

    @Override
    public int getItemCount() {
        return mGrup.size();
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
    public List filter(String text) {


        Log.d("tes",""+itemsCopy.size());

        if(text.isEmpty()){
            mGrup.clear();
            mGrup.addAll(itemsCopy);
            Log.d("tes",""+itemsCopy.size());
        } else{
            mGrup.clear();
            text = text.toLowerCase();
            for(Grup item: itemsCopy){
                if(item.nama.toLowerCase().contains(text)){
                    mGrup.add(item);
                }
            }
        }

        return mGrup;


    }
    private String typePesan;
    private void pesanTerakhir(String idgrup, final TextView last_msg)
    {
        pesanterakhir="default";

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("GroupChats").child(idgrup);


        reference.limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    PesanGrup pesan = snapshot.getValue(PesanGrup.class);

                    pesanterakhir = pesan.getPesan();
                    typePesan = pesan.getType();




                }





                switch (pesanterakhir) {
                    case "default":
                        last_msg.setText("Belum Ada Pesan");
                        break;

                    default: {
                        if (typePesan.equals("image"))
                        {
                            last_msg.setText("foto");
                        }else {
                            last_msg.setText(pesanterakhir);
                            break;
                        }
                    }


                }

                pesanterakhir="default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
