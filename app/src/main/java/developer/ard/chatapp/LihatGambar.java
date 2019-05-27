package developer.ard.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class LihatGambar extends AppCompatActivity {

    ImageView gambar ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_gambar);

        gambar = findViewById(R.id.gambar);

        Intent intent = getIntent();

        final String url = intent.getStringExtra("url");

        Picasso.with(LihatGambar.this).load(url).placeholder(R.drawable.usera).rotate(90).into(gambar, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(LihatGambar.this).load(url).placeholder(R.drawable.usera).rotate(90).into(gambar);
            }
        });
    }
}
