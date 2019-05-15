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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class PesanAdapter  extends RecyclerView.Adapter < PesanAdapter.ViewHolder> {

    public static final int pesan_kiri = 0;
    public static final  int pesan_kanan = 1;

    private Context context;
    private List<Pesan> pesans;
    public CircleImageView profile_image;
    private String imageurl;
    private String nama;
    DatabaseReference user;

    String userId;
    FirebaseUser firebaseUser;

    public  PesanAdapter(Context context, List<Pesan> pesans,String imageurl,String nama,String userId) {
        this.context = context;
        this.pesans = pesans;
        this.imageurl = imageurl;
        this.nama = nama;
        this.userId= userId;

    }

    @NonNull
    @Override
    public  PesanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == pesan_kanan) {
            View view = LayoutInflater.from(context).inflate(R.layout.pesan_kanan, parent, false);

            return new PesanAdapter.ViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.pesan_kiri, parent, false);

            return new PesanAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final PesanAdapter.ViewHolder holder, int position) {

        final Pesan pesan = pesans.get(position);
        String typePesan = pesan.getType();








        if (imageurl.equals("default"))
        {
            profile_image.setImageResource(R.drawable.usera);
        }
        else {
            Picasso.with(context).load(imageurl).placeholder(R.drawable.usera).into(profile_image);
        }

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profil = new Intent(context,UserProfil.class);
                profil.putExtra("id",userId);
                context.startActivity(profil);
            }
        });



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
            if (pesan.isSeen())
            {

                holder.txt_seen.setText("Dibaca");
            }
            else
            {

                holder.txt_seen.setText("Dikirim");
            }

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

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Chats").child(pesan.getId()).child(pesan.getPushid());

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


         //   Picasso.with(context).load(url).placeholder(R.drawable.usera).into(holder.pesanGambar);

            if (pesan.isSeen())
            {

                holder.bacaGambar.setText("Dibaca");
            }
            else
            {

                holder.bacaGambar.setText("Dikirim");
            }


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

                                Toast.makeText(context,"Download Berhasil Folder Chat App",Toast.LENGTH_SHORT).show();

                            }

                            if (i==1)
                            {
                                Log.d("path"," "+pesan.getId()+" "+pesan.getPushid());

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Chats").child(pesan.getId()).child(pesan.getPushid());

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

            if (pesan.isSeen())
            {

                holder.txt_seen.setText("Dibaca");
            }
            else
            {

                holder.txt_seen.setText("Dikirim");
            }

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

                                Log.d("path"," "+pesan.getId()+" "+pesan.getPushid());

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Chats").child(pesan.getId()).child(pesan.getPushid());

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
                Toast.makeText(context,"Berhasil Download,Cek Folder Chat App",Toast.LENGTH_SHORT).show();
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