package developer.ard.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class login extends AppCompatActivity {

    Button masuk;
    EditText username,pass;
    ProgressDialog pd;
    TextView register;

    private FirebaseAuth mAuth;
    DatabaseReference mUser;
    private DatabaseReference mUserRef;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pd=new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        register=findViewById(R.id.txtRegister);

        masuk=findViewById(R.id.login_btn);
        username=findViewById(R.id.EtUsername);
        pass=findViewById(R.id.EtPassword);

        mUser = FirebaseDatabase.getInstance().getReference().child("Users");

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(login.this, Daftar.class);
                startActivity(startIntent);

            }
        });


        masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user=username.getText().toString();
                String pw = pass.getText().toString();

                if(!TextUtils.isEmpty(user) || (!TextUtils.isEmpty(pw) ))
                {

                        pd.setTitle("Logging In");
                        pd.setMessage("Please Wait!");
                        pd.setCanceledOnTouchOutside(false);
                        pd.show();
                        in(user,pw);





                }
                else
                {
                    Toast.makeText(login.this,"Data Tidak Boleh Kosong",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void in(final String user, final String pass)
    {
        mAuth.signInWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    final String cu = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    final DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(cu);
                    final DatabaseReference tesdatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(cu);

                    String deviceToken= FirebaseInstanceId.getInstance().getToken();
                    mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
                    mUserRef.child("device_token").setValue(deviceToken);
                    //mUserRef.child("online").setValue("true");

                    mUserDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            MyApp.level = dataSnapshot.child("level").getValue().toString();


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    pd.dismiss();
                    Toast.makeText(login.this,"Login Berhasil",Toast.LENGTH_SHORT).show();

                    Intent startIntent = new Intent(login.this, MainActivity.class);
                    startActivity(startIntent);
                    finish();


                }
                else
                {

                    String error="";

                    try{
                        throw task.getException();
                    }catch (Exception e) {
                        error=e.getMessage();
                        e.printStackTrace();
                        Log.d("login error :",error);
                        pd.hide();
                        Toast.makeText(login.this,error,Toast.LENGTH_SHORT).show();
                    }





                }
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();


        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {


            //  awal=true;
            // FirebaseMessagingService.value=0;

            //Toast.makeText(this,""+awal,Toast.LENGTH_SHORT).show();
            String deviceToken = FirebaseInstanceId.getInstance().getToken();


            Intent startIntent = new Intent(login.this, MainActivity.class);
            startActivity(startIntent);
            finish();


        }
    }



}
