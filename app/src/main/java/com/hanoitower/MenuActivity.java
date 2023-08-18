package com.hanoitower;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hanoitower.game.GameActivity;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);
        this.findViewById(R.id.menu_game_start).setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.menu_game_start) {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("rings", 3);
            startActivity(intent);
            finish();
        }
    }
}
