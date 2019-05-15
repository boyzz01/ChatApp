package developer.ard.chatapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import developer.ard.chatapp.R;
import developer.ard.chatapp.UserProfil;
import developer.ard.chatapp.model.Pesan;
import developer.ard.chatapp.model.PesanGrup;

public class PesanGrupAdapter extends RecyclerView.Adapter < PesanGrupAdapter.ViewHolder> {

    public static final int pesan_kiri = 0;
    public static final  int pesan_kanan = 1;

    private Context context;
    private List<PesanGrup> pesans;
    public CircleImageView profile_image;
    private String imageurl;
    private String nama;
    DatabaseReference user;

    String userId;
    FirebaseUser firebaseUser;

    public PesanGrupAdapter(Context context, List<PesanGrup> pesans, String imageurl, String nama, String userId) {
        this.context = context;
        this.pesans = pesans;
        this.imageurl = imageurl;
        this.nama = nama;
        this.userId= userId;

    }

    @NonNull
    @Override
    public  PesanGrupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == pesan_kanan) {
            View view = LayoutInflater.from(context).inflate(R.layout.pesan_grup_kanan, parent, false);

            return new PesanGrupAdapter.ViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.pesan_grup_kiri, parent, false);

            return new PesanGrupAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final PesanGrupAdapter.ViewHolder holder, int position) {

        final PesanGrup pesan = pesans.get(position);
        String typePesan = pesan.getType();


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(pesan.getPengirim());
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nama = dataSnapshot.child("nama").getValue().toString();
                holder.namaGrup.setText(nama);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });









        if (imageurl.equals("default"))
        {
            profile_image.setImageResource(R.drawable.usera);
        }
        else {
            Picasso.with(context).load(imageurl).placeholder(R.drawable.usera).into(profile_image);
        }

//        profile_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent profil = new Intent(context,UserProfil.class);
//                profil.putExtra("id",userId);
//                context.startActivity(profil);
//            }
//        });



        if (typePesan.equals("text"))
        {
            holder.waktu.setVisibility(View.VISIBLE);
            holder.lihat_pesan.setVisibility(View.VISIBLE);
            holder.txt_seen.setVisibility(View.VISIBLE);

            holder.downloadFile.setVisibility(View.GONE);
            holder.ukuranFile.setVisibility(View.GONE);


            holder.pesanGambar.setVisibility(View.GONE);
            holder.waktuGambar.setVisibility(View.GONE);
            holder.bacaGambar.setVisibility(View.GONE);


            holder.waktu.setText(new SimpleDateFormat("HH:mm").format(pesan.getWaktu()));
            holder.lihat_pesan.setText(pesan.getPesan());

            holder.lihat_pesan.setTextIsSelectable(false);
            holder.lihat_pesan.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final CharSequence options[] = new CharSequence[]{"Hapus Pesan"};

                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setTitle("Hapus Pesan?");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, final int i) {


                            if(i == 0){

                                Log.d("path"," "+pesan.getId()+" "+pesan.getPushid());

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("GroupChats").child(pesan.getId()).child(pesan.getPushid());

                                databaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        notifyDataSetChanged();
                                    }
                                });

                            }



                        }
                    });

                    builder.show();

                    return false;
                }
            });

        }

        else if (typePesan.equals("image"))
        {
            final String url = pesan.getPesan();
            holder.waktu.setVisibility(View.GONE);
            holder.lihat_pesan.setVisibility(View.GONE);
            holder.txt_seen.setVisibility(View.GONE);

            holder.downloadFile.setVisibility(View.GONE);
            holder.ukuranFile.setVisibility(View.GONE);



            holder.pesanGambar.setVisibility(View.VISIBLE);
            holder.waktuGambar.setVisibility(View.VISIBLE);
            holder.bacaGambar.setVisibility(View.VISIBLE);

            holder.waktuGambar.setText(new SimpleDateFormat("HH:mm").format(pesan.getWaktu()));
            Picasso.with(context).load(url).networkPolicy(NetworkPolicy.OFFLINE).priority(Picasso.Priority.HIGH).placeholder(R.drawable.usera).rotate(90).into(holder.pesanGambar, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(url).placeholder(R.drawable.usera).rotate(90).into(holder.pesanGambar);
                }
            });


            holder.pesanGambar.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final CharSequence options[] = new CharSequence[]{"Download Gambar","Hapus Gambar"};

                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setTitle("Select Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, final int i) {

                            //Click Event for each item.


//                            if(i == 0){
//
//                                Intent intent = new Intent(v.getContext(), zoom.class);
//                                intent.putExtra("image", c.getMessage());
//                                v.getContext().startActivity(intent);
//
//                            }

                            if(i == 0){

                                Bitmap bm=((BitmapDrawable)holder.pesanGambar.getDrawable()).getBitmap();
                                saveImageFile(bm);
                                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                //change mCurrentPhotoPath for your imagepath
                                File f = new File(Environment.getExternalStorageDirectory()
                                        .getPath(), "Chat App");
                                Uri contentUri = Uri.fromFile(f);
                                mediaScanIntent.setData(contentUri);
                                context.sendBroadcast(mediaScanIntent);

                                Toast.makeText(context,"Download Berhasil",Toast.LENGTH_SHORT).show();

                            }
                            if (i==1)
                            {
                                Log.d("path"," "+pesan.getId()+" "+pesan.getPushid());

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("GroupChats").child(pesan.getId()).child(pesan.getPushid());

                                databaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        notifyDataSetChanged();
                                    }
                                });
                            }


                        }
                    });

                    builder.show();

                    return false;
                }
            });

        }

        else if (typePesan.equals("file"))
        {
            final String namafile = pesan.getPesan();
            String ukuran = pesan.getUkuran();
            final String url = pesan.getUrl();

            holder.waktu.setVisibility(View.VISIBLE);
            holder.lihat_pesan.setVisibility(View.VISIBLE);
            holder.txt_seen.setVisibility(View.VISIBLE);

            holder.downloadFile.setVisibility(View.VISIBLE);
            holder.ukuranFile.setVisibility(View.VISIBLE);


            holder.pesanGambar.setVisibility(View.GONE);
            holder.waktuGambar.setVisibility(View.GONE);
            holder.bacaGambar.setVisibility(View.GONE);

            holder.waktu.setText(new SimpleDateFormat("HH:mm").format(pesan.getWaktu()));
            holder.lihat_pesan.setText(namafile);
            holder.ukuranFile.setText(ukuran);



            holder.downloadFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    download(url,namafile);
                }
            });

            holder.lihat_pesan.setTextIsSelectable(false);
            holder.lihat_pesan.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final CharSequence options[] = new CharSequence[]{"Hapus Pesan"};

                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setTitle("Hapus Pesan?");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, final int i) {


                            if(i == 0){



                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("GroupChats").child(pesan.getId()).child(pesan.getPushid());

                                databaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        notifyDataSetChanged();
                                    }
                                });

                            }



                        }
                    });

                    builder.show();

                    return false;
                }
            });
        }





    }

    public String saveImageFile(Bitmap bitmap) {
        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filename;
    }
    private String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory()
                .getPath(), "Chat App");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/"
                + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }

    private void download(String url,String nama) {
        StorageReference Downloadfile ;
        StorageReference fileStorage = FirebaseStorage.getInstance().getReference();

        Downloadfile = fileStorage.child(""+url);

        File file = new File(Environment.getExternalStorageDirectory().getPath(),"Chat APP");
        if (!file.exists()) {
            file.mkdirs();
        }

        File Output = new File(file,nama);


        Downloadfile.getFile(Output).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(context,"Berhasil Download,Cek Folder Chat PM",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Gagal ,Error : "+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return pesans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView lihat_pesan;
        public TextView waktu,waktuGambar,bacaGambar;
        public TextView txt_seen;
        public TextView ukuranFile;
        public ImageButton downloadFile;
        public ImageView pesanGambar;
        public TextView namaGrup;



        public ViewHolder(View itemView) {
            super(itemView);

            ukuranFile = itemView.findViewById(R.id.ukuranFile);
            downloadFile = itemView.findViewById(R.id.downloadBtn);
            pesanGambar = itemView.findViewById(R.id.gambarChat);
            waktu = itemView.findViewById(R.id.waktu);
            lihat_pesan = itemView.findViewById(R.id.message);
            profile_image = itemView.findViewById(R.id.profil);
            txt_seen = itemView.findViewById(R.id.baca);
            waktuGambar = itemView.findViewById(R.id.waktuGambar);
            bacaGambar = itemView.findViewById(R.id.bacaGambar);
            namaGrup = itemView.findViewById(R.id.namaGrup);


        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (pesans.get(position).getPengirim().equals(firebaseUser.getUid()))
        {

            return pesan_kanan;
        }
        else
        {
            return  pesan_kiri;
        }
    }
}