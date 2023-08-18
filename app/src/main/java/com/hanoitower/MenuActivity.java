package com.hanoitower;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText ringCount;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);
        ringCount = (EditText) findViewById(R.id.menu_edit_text);
        this.findViewById(R.id.menu_game_start).setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.menu_game_start) {
            if (ringCount.getText().toString().isEmpty() || 3 > Integer.parseInt(ringCount.getText().toString())){
                showInvalidInputDialog();
            }
            else {
                Intent intent = new Intent(this, GameActivity.class);
                intent.putExtra("rings", Integer.parseInt(ringCount.getText().toString()));
                startActivity(intent);
                finish();
            }
        }
    }

    private void showInvalidInputDialog(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.invalid_dialog)
                .setMessage(R.string.invalid_dialog_message)
                .setCancelable(true)
                .setPositiveButton(R.string.positive_button, null)
                .show();
    }
        }

