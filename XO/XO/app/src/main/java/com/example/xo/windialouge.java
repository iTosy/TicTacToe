package com.example.xo;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class windialouge extends Dialog{

    private final String message;
    private final MainActivity mainActivity;
    public windialouge(@NonNull Context context, String message) {
        super(context);
        this.message = message;
        this.mainActivity = ((MainActivity) context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.windialouge_layout);

        final TextView mess = findViewById(R.id.mess);
        final Button start = findViewById(R.id.startnew);
        mess.setText(message);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                getContext().startActivity(new Intent(getContext(), playerName.class));
                mainActivity.finish();
            }
        });
    }
}
