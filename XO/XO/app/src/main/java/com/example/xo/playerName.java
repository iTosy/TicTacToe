package com.example.xo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class playerName extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_name);


        final EditText name=findViewById( R.id.playername);
        final AppCompatButton start=findViewById( R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String player=name.getText().toString();
                if(player.isEmpty()){
                    Toast.makeText(playerName.this,"Enter a name" ,Toast.LENGTH_SHORT).show();
                }
                else{
                    //create intent to open mainActivity
                    Intent intent=new Intent(playerName.this,MainActivity.class);
                    Log.d("Tesss", "mashy");
                    //adding player name along with intent
                    intent.putExtra("player",player);
                    Log.d("Tesss", "maashy");

                    startActivity(intent);
                    Log.d("Tesss", "maaashy");

                    finish();
                }

            }
        });

    }
}