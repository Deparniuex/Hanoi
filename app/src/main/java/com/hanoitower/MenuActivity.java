package com.hanoitower;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.hanoitower.game.GameActivity;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText ringCount;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);
        ringCount = (EditText) findViewById(R.id.menu_edit_text);
        this.findViewById(R.id.menu_game_start).setOnClickListener(this);
        this.findViewById(R.id.plus_button).setOnClickListener(this);
        this.findViewById(R.id.negative_button).setOnClickListener(this);
        this.findViewById(R.id.exit).setOnClickListener(this);
        this.findViewById(R.id.help_menu).setOnClickListener(this);
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
        if (view.getId() == R.id.plus_button) {
            checkEmpty(ringCount);
            if (Integer.parseInt(ringCount.getText().toString()) == 9){
                ringCount.setText("2");
            }
             ringCount.setText(String.valueOf(Integer.parseInt(ringCount.getText().toString()) + 1));
        }
        if (view.getId() == R.id.negative_button) {
            checkEmpty(ringCount);
            if (Integer.parseInt(ringCount.getText().toString()) <= 3){
                ringCount.setText("4");
            }
            ringCount.setText(String.valueOf(Integer.parseInt(ringCount.getText().toString()) - 1));
        }
        if (view.getId() == R.id.exit){
            DialogInterface.OnClickListener listener = (dialog, button) -> {
                if (button == Dialog.BUTTON_POSITIVE) {
                    finish();
                }
            };
            new AlertDialog.Builder(this)
                    .setTitle(R.string.exit_game)
                    .setMessage(R.string.exit_game_dialog_message)
                    .setCancelable(true)
                    .setNegativeButton(R.string.negative_button, listener)
                    .setPositiveButton(R.string.positive_button, listener)
                    .show();
        }
        if (view.getId() == R.id.help_menu){
            showCredits();
        }
    }

    private void showCredits() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.help_dialog)
                .setMessage(R.string.credits_dialog_messsage)
                .setCancelable(true)
                .setPositiveButton(R.string.positive_button, null)
                .show();
    }

    private void checkEmpty(EditText editText){
        if (editText.getText().toString().isEmpty()) {
            ringCount.setText("0");
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

