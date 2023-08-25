package com.hanoitower;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.hanoitower.game.GameActivity;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText ringCount;
    private ImageButton plusButton;
    private ImageButton negativeButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);
        ringCount = (EditText) findViewById(R.id.menu_edit_text);
        negativeButton = (ImageButton) findViewById(R.id.negative_button);
        plusButton = (ImageButton) findViewById(R.id.plus_button);
        negativeButton.setEnabled(false);
        ringCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (ringCount.getText().toString().isEmpty()) {
                    plusButton.setEnabled(false);
                    negativeButton.setEnabled(false);
                }else{
                if (Integer.parseInt(ringCount.getText().toString()) >= 9) {
                    plusButton.setEnabled(false);
                    negativeButton.setEnabled(true);
                }
                else if (Integer.parseInt(ringCount.getText().toString()) <= 3) {
                    plusButton.setEnabled(true);
                    negativeButton.setEnabled(false);
                }else{
                    plusButton.setEnabled(true);
                    negativeButton.setEnabled(true);
                }
            }}

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        this.findViewById(R.id.menu_game_start).setOnClickListener(this);
        this.findViewById(R.id.plus_button).setOnClickListener(this);
        this.findViewById(R.id.negative_button).setOnClickListener(this);
        this.findViewById(R.id.exit).setOnClickListener(this);
        this.findViewById(R.id.help_menu).setOnClickListener(this);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitDialog();
            }
        });
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
        if (view.getId() == R.id.plus_button || view.getId() == R.id.negative_button) {
            int value = Integer.parseInt(ringCount.getText().toString()) + (view.getId()==R.id.plus_button?1:-1);
            ringCount.setText(Integer.toString(value));
            if (value <= 3) {
                negativeButton.setEnabled(false);
                plusButton.setEnabled(true);
            }
            else if (value >= 9){
                plusButton.setEnabled(false);
                negativeButton.setEnabled(true);
            }
        }
        if (view.getId() == R.id.exit){
            showExitDialog();
        }
        if (view.getId() == R.id.help_menu){
            showCredits();
        }
    }


    private void showExitDialog() {
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

    private void showCredits() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.help_dialog)
                .setMessage(R.string.credits_dialog_messsage)
                .setCancelable(true)
                .setPositiveButton(R.string.positive_button, null)
                .show();
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

