package developer.ard.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

import developer.ard.chatapp.notifikasi.FirebaseIdService;

public class Daftar extends AppCompatActivity {

    EditText nama,email,password;
    Button register;

    FirebaseAuth firebaseAuth;
    ProgressDialog pd;

    DatabaseReference firebaseDatabase,userdatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);
        nama = findViewById(R.id.EtNama);
        email = findViewById(R.id.EtEmail);
        password = findViewById(R.id.EtPassword);
        register = findViewById(R.id.btnRegister);

        pd=new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String semail,spassword,snama;
                semail = email.getText().toString();
                spassword = password.getText().toString();
                snama=nama.getText().toString();

                if ( semail.equals("") || spassword.equals("") || snama.equals(""))
                {

                    Toast.makeText(Daftar.this,"Data Tidak Boleh Kosong",Toast.LENGTH_SHORT).show();
                }

                else
                {

                    pd.setTitle("Mendaftarkan");
                    pd.setMessage("Please Wait!");
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();

                    firebaseAuth.createUserWithEmailAndPassword(semail,spassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                                String uid = current_user.getUid();
                                firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                                HashMap<String, String> userMap = new HashMap<>();
                                userMap.put("id", uid);
                                userMap.put("email", semail);
                                // userMap.put("level",level);
                                userMap.put("image", "default");
                                userMap.put("level","biasa");
                                userMap.put("thumb_image", "default");
                                userMap.put("status", "Hallo Semua Salam Kenal");
                                // userMap.put("password", pass);
                                userMap.put("nama", snama);
                                userMap.put("login", "tidak");


                                firebaseDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        final String cid= FirebaseAuth.getInstance().getCurrentUser().getUid();

                                        userdatabase = FirebaseDatabase.getInstance().getReference().child("kontak").child(cid);
                                        HashMap<String, String> userMap = new HashMap<>();
                                        userMap.put("id",cid);
                                        userMap.put("nama",snama);
                                        userdatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });

                                        firebaseDatabase.child("online").setValue(ServerValue.TIMESTAMP);

                                        Toast.makeText(Daftar.this,"Pendaftaran Berhasil",Toast.LENGTH_SHORT).show();
                                        Intent startIntent = new Intent(Daftar.this, MainActivity.class);
                                        startActivity(startIntent);
                                        finish();
                                        pd.dismiss();
                                    }



                                });
                            }

                            else {
                                String error="";

                                try{
                                    throw task.getException();
                                }catch (Exception e) {
                                    error=e.getMessage();
                                    e.printStackTrace();
                                    Log.d("daftar error :",error);
                                    pd.hide();
                                    Toast.makeText(Daftar.this,error,Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    });

                }

            }
        });


    }
}
