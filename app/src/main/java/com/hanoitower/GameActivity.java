package com.hanoitower;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private StateHolder stateHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        this.<ImageButton>findViewById(R.id.exit_to_menu).setOnClickListener(this);
        stateHolder = new ViewModelProvider(this).get(StateHolder.class);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitMenuDialog();
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    private void showExitMenuDialog() {
        DialogInterface.OnClickListener listener = (dialog, button) -> {
            if (button == Dialog.BUTTON_POSITIVE) {
                startActivity(new Intent(GameActivity.this, GameActivity.class)); // TODO go to menu
                finish();
            }
        };
        new AlertDialog.Builder(this)
                .setTitle(R.string.exit_menu_dialog)
                .setMessage(R.string.exit_menu_dialog_message)
                .setCancelable(true)
                .setNegativeButton(R.string.negative_button, listener)
                .setPositiveButton(R.string.positive_button, listener)
                .show();
    }
}

class StateHolder extends ViewModel {

}
