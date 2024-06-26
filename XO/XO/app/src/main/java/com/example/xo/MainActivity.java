package com.example.xo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppComp0
        atActivity {
    private ImageView i1,i2,i3,i4,i5,i6,i7,i8,i9;
    private List<int[]> winlist=new ArrayList<>();
    private final List<String> disablebox = new ArrayList<>();
    private String playerid="0";
    private TextView p1,p2;
    private LinearLayout p1l,p2l;
    // getting firebase database reference from URL
    FirebaseDatabase fire;
    DatabaseReference databasereference;
    //true when opponent will be found to play
    private boolean oppofound=false;
    //unique opponent id
    private String oppoid="0";

    private String status="matching";
    private String playerturn="";
    private String conid = "";
    ValueEventListener turnseventlistener, woneventlistener;
    private final String[] boxesselectedby = {"","","","","","","","",""};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fire = FirebaseDatabase.getInstance();
        databasereference = fire.getReference();
        Log.d("Tesss", "mashy1");


        final String player = getIntent().getStringExtra("player");
        Log.d("Tesss", "mashy2");

        p1 = findViewById(R.id.p1);
        p2 = findViewById(R.id.p2);
        p1l = findViewById(R.id.p1layout);
        p2l = findViewById(R.id.p2layout);
        i1 = findViewById(R.id.i1);
        i2 = findViewById(R.id.i2);
        i3 = findViewById(R.id.i3);
        i4 = findViewById(R.id.i4);
        i5 = findViewById(R.id.i5);
        i6 = findViewById(R.id.i6);
        i7 = findViewById(R.id.i7);
        i8 = findViewById(R.id.i8);
        i9 = findViewById(R.id.i9);
        winlist.add(new int[]{0, 1, 2});
        winlist.add(new int[]{0, 4, 5});
        winlist.add(new int[]{6, 7, 8});
        winlist.add(new int[]{0, 3, 6});
        winlist.add(new int[]{1, 4, 7});
        winlist.add(new int[]{2, 5, 8});
        winlist.add(new int[]{2, 4, 6});
        winlist.add(new int[]{0, 4, 8});
        p1.setText(player);
        Log.d("Tesss", "mashy3");

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Waiting for Opponent");
        progressDialog.show();
        Log.d("Tesss", "mashy4");

        // generate player unique id.Player will be identified by this id.
        playerid = String.valueOf(System.currentTimeMillis());
        databasereference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild("connections")){
                    databasereference.child("connections").setValue("hi");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databasereference.child("conncetions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!oppofound) {
                    //check if there are others in the firebase
                    if (snapshot.hasChildren()) {
                        for (DataSnapshot connections : snapshot.getChildren()) {
                            String conId = (connections.getKey());
                            int getplayercount = (int) connections.getChildrenCount();
                            if (status.equals("waiting")) {
                                if (getplayercount == 2) {
                                    playerturn = playerid;
                                    //applyplayerturn(playerturn);
                                    boolean playerfound = false;

                                    for(DataSnapshot players: connections.getChildren()){
                                        String get_player_id = players.getKey();
                                        if(get_player_id.equals(playerid)){
                                            playerfound = true;
                                        }
                                        else if(playerfound){
                                            String getrivalname = players.child("player_name").getValue(String.class);
                                            oppoid = players.getKey();
                                            p2.setText(getrivalname);
                                            conid = conId;
                                            oppofound = true;

                                            databasereference.child("turns").child(conid).addValueEventListener(turnseventlistener);
                                            databasereference.child("won").child(conid).addValueEventListener(woneventlistener);

                                            if(progressDialog.isShowing()){
                                                progressDialog.dismiss();
                                            }
                                            databasereference.child("connections").removeEventListener(this);

                                            break;

                                        }
                                    }
                                }
                            }
                            else{
                                if(getplayercount == 1){
                                    connections.child(playerid).child("player_name").getRef().setValue(player);

                                    for(DataSnapshot players : connections.getChildren()){
                                        String getrivalname = players.child("player_name").getValue(String.class);
                                        oppoid = players.getKey();
                                        playerturn = oppoid;
                                        p2.setText(getrivalname);
                                        conid = conId;
                                        oppofound = true;

                                        databasereference.child("turns").child(conid).addValueEventListener(turnseventlistener);
                                        databasereference.child("won").child(conid).addValueEventListener(woneventlistener);

                                        if(progressDialog.isShowing()){
                                            progressDialog.dismiss();
                                        }
                                        databasereference.child("connections").removeEventListener(this);
                                        break;

                                    }
                                }
                            }
                        }
                        if(!oppofound && !status.equals("waiting")){
                            String connectionid = String.valueOf(System.currentTimeMillis());
                            snapshot.child(connectionid).child(playerid).child("player_name").getRef().setValue(player);
                            status = "waiting";
                        }


                    } else {
                        String connectionid = String.valueOf(System.currentTimeMillis());
                        snapshot.child(connectionid).child(playerid).child("player_name").getRef().setValue(player);
                        status = "waiting";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        turnseventlistener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if(dataSnapshot.getChildrenCount() == 2){
                        final int getboxpose = Integer.parseInt(dataSnapshot.child("boxpose").getValue(String.class));
                        final String getplayerid = dataSnapshot.child("player_id").getValue(String.class);
                        if(!disablebox.contains(String.valueOf(getboxpose))){
                            disablebox.add(String.valueOf(getboxpose));
                            switch(getboxpose){
                                case 1:selectbox(i1,getboxpose,getplayerid);break;
                                case 2:selectbox(i2,getboxpose,getplayerid);break;
                                case 3:selectbox(i3,getboxpose,getplayerid);break;
                                case 4:selectbox(i4,getboxpose,getplayerid);break;
                                case 5:selectbox(i5,getboxpose,getplayerid);break;
                                case 6:selectbox(i6,getboxpose,getplayerid);break;
                                case 7:selectbox(i7,getboxpose,getplayerid);break;
                                case 8:selectbox(i8,getboxpose,getplayerid);break;
                                case 9:selectbox(i9,getboxpose,getplayerid);break;
                                default:break;

                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        woneventlistener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("player_id")){
                    String getwinnerid = snapshot.child("player_id").getValue(String.class);
                    final windialouge windialouge;

                    if(getwinnerid.equals(playerid)){
                        windialouge = new windialouge(MainActivity.this, "you win");
                    }
                    else{
                        windialouge = new windialouge(MainActivity.this, "you lost");

                    }
                    windialouge.setCancelable(false);
                    windialouge.show();

                    databasereference.child("won").removeEventListener(woneventlistener);
                    databasereference.child("turns").removeEventListener(turnseventlistener);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        i1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!disablebox.contains(1) && playerturn.equals(playerid)){
                    ((ImageView)v).setImageResource(R.drawable.x);
                    databasereference.child("turns").child(conid).child(String.valueOf(disablebox.size()+1)).child("boxpose").setValue("1");
                    databasereference.child("turns").child(conid).child(String.valueOf(disablebox.size()+1)).child("player_id").setValue(playerid);

                    playerturn = oppoid;



                }
            }
        });

        i2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!disablebox.contains(2) && playerturn.equals(playerid)){
                    ((ImageView)v).setImageResource(R.drawable.x);
                    databasereference.child("turns").child(conid).child(String.valueOf(disablebox.size()+1)).child("boxpose").setValue("2");
                    databasereference.child("turns").child(conid).child(String.valueOf(disablebox.size()+1)).child("player_id").setValue(playerid);

                    playerturn = oppoid;



                }
            }
        });


        i3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!disablebox.contains(3) && playerturn.equals(playerid)){
                    ((ImageView)v).setImageResource(R.drawable.x);
                    databasereference.child("turns").child(conid).child(String.valueOf(disablebox.size()+1)).child("boxpose").setValue("3");
                    databasereference.child("turns").child(conid).child(String.valueOf(disablebox.size()+1)).child("player_id").setValue(playerid);

                    playerturn = oppoid;



                }
            }
        });

        i4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!disablebox.contains(4) && playerturn.equals(playerid)){
                    ((ImageView)v).setImageResource(R.drawable.x);
                    databasereference.child("turns").child(conid).child(String.valueOf(disablebox.size()+1)).child("boxpose").setValue("4");
                    databasereference.child("turns").child(conid).child(String.valueOf(disablebox.size()+1)).child("player_id").setValue(playerid);

                    playerturn = oppoid;



                }
            }
        });

        i5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!disablebox.contains(5) && playerturn.equals(playerid)){
                    ((ImageView)v).setImageResource(R.drawable.x);
                    databasereference.child("turns").child(conid).child(String.valueOf(disablebox.size()+1)).child("boxpose").setValue("5");
                    databasereference.child("turns").child(conid).child(String.valueOf(disablebox.size()+1)).child("player_id").setValue(playerid);

                    playerturn = oppoid;



                }
            }
        });

        i6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!disablebox.contains(6) && playerturn.equals(playerid)){
                    ((ImageView)v).setImageResource(R.drawable.x);
                    databasereference.child("turns").child(conid).child(String.valueOf(disablebox.size()+1)).child("boxpose").setValue("6");
                    databasereference.child("turns").child(conid).child(String.valueOf(disablebox.size()+1)).child("player_id").setValue(playerid);

                    playerturn = oppoid;



                }
            }
        });

        i7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!disablebox.contains(7) && playerturn.equals(playerid)){
                    ((ImageView)v).setImageResource(R.drawable.x);
                    databasereference.child("turns").child(conid).child(String.valueOf(disablebox.size()+1)).child("boxpose").setValue("7");
                    databasereference.child("turns").child(conid).child(String.valueOf(disablebox.size()+1)).child("player_id").setValue(playerid);

                    playerturn = oppoid;



                }
            }
        });

        i8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!disablebox.contains(8) && playerturn.equals(playerid)){
                    ((ImageView)v).setImageResource(R.drawable.x);
                    databasereference.child("turns").child(conid).child(String.valueOf(disablebox.size()+1)).child("boxpose").setValue("8");
                    databasereference.child("turns").child(conid).child(String.valueOf(disablebox.size()+1)).child("player_id").setValue(playerid);

                    playerturn = oppoid;



                }
            }
        });

        i9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!disablebox.contains(9) && playerturn.equals(playerid)){
                    ((ImageView)v).setImageResource(R.drawable.x);
                    databasereference.child("turns").child(conid).child(String.valueOf(disablebox.size()+1)).child("boxpose").setValue("9");
                    databasereference.child("turns").child(conid).child(String.valueOf(disablebox.size()+1)).child("player_id").setValue(playerid);

                    playerturn = oppoid;



                }
            }
        });


    }/*
    private void applyplayerturn(String playerid1){
        if(playerid1.equals(playerid)){
            p1l.setBackgroundResource(R.drawable);
            p2l.setBackgroundResource(R.drawable);
        }
        else{
            p1l.setBackgroundResource(R.drawable);
            p2l.setBackgroundResource(R.drawable);
        }

    }*/
    private void selectbox(ImageView iv,int sbpose, String selectedbyplayer){
        boxesselectedby[sbpose - 1] = selectedbyplayer;

        if(selectedbyplayer.equals(playerid)){
            iv.setImageResource(R.drawable.x);
            playerturn = oppoid;
        }
        else{
            iv.setImageResource(R.drawable.o);
            playerturn = playerid;
        }
        /*applyplayerturn(playerturn);*/
        if(checkwin(selectedbyplayer)){
            databasereference.child("won").child(conid).child("player_id").setValue(selectedbyplayer);
        }
        if(disablebox.size() == 9){
            final windialouge windialouge = new windialouge(MainActivity.this, "Draw");
            windialouge.setCancelable(false);
            windialouge.show();
        }

    }
    private boolean checkwin(String playerid){
        boolean won = false;
        for(int i = 0; i < winlist.size() ; i++){

            final int[] comb = winlist.get(i);

            if(boxesselectedby[comb[0]].equals(playerid) && boxesselectedby[comb[1]].equals(playerid) && boxesselectedby[comb[2]].equals(playerid)){
                won = true;
            }
        }
        return won;
    }
}