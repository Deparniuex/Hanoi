package com.hanoitower;

import android.app.Dialog;
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
import androidx.lifecycle.viewmodel.CreationExtras;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private StateHolder stateHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        findViewById(R.id.exit_to_menu).setOnClickListener(this);
        findViewById(R.id.help).setOnClickListener(this);
        stateHolder = new ViewModelProvider(this,
                new StateHolderFactory(getIntent().getIntExtra("rings", 0))
        ).get(StateHolder.class);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitMenuDialog();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.exit_to_menu)
            showExitMenuDialog();
        else if (view.getId() == R.id.help)
            showHelpDialog();
    }

    private void showExitMenuDialog() {
        DialogInterface.OnClickListener listener = (dialog, button) -> {
            if (button == Dialog.BUTTON_POSITIVE) {
                startActivity(new Intent(GameActivity.this, MenuActivity.class));
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

    private void showHelpDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.help_dialog)
                .setMessage(R.string.game_help_dialog_message)
                .setCancelable(true)
                .setPositiveButton(R.string.positive_button, null)
                .show();
    }
}

class StateHolderFactory implements ViewModelProvider.Factory {

    private final int rings;

    public StateHolderFactory(int rings) {
        this.rings = rings;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return StateHolder.class.isAssignableFrom(modelClass) ?
                (T) new StateHolder(rings) : ViewModelProvider.Factory.super.create(modelClass);
    }
}

class StateHolder extends ViewModel {
    private final int rings;

    public StateHolder(int rings) {
        this.rings = rings;
    }
}
