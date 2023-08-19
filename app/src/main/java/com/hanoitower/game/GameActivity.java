package com.hanoitower.game;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.hanoitower.MenuActivity;
import com.hanoitower.R;

import java.util.List;
import java.util.stream.IntStream;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private final TowersGenerator towersGenerator = (towersCount, ringsCount) -> {
        int[][] towers = new int[towersCount][ringsCount];
        towers[0] = IntStream.range(1, ringsCount + 1).toArray();
        return towers;
    }; // TODO define tower generator instead of this demo implementation
    private int ringsCount;

    /* These fields will be initialized after views are drawn */
    private StateHolder stateHolder;
    private TowerAdapter[] towerAdapters;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        findViewById(R.id.exit_to_menu).setOnClickListener(this);
        findViewById(R.id.help).setOnClickListener(this);
        ringsCount = getIntent().getIntExtra("rings", 0);
        stateHolder = new ViewModelProvider(
                this,
                new StateHolderFactory(towersGenerator, ringsCount)
        ).get(StateHolder.class);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitMenuDialog();
            }
        });
        findViewById(android.R.id.content).getRootView().post(this::onLayout);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void onLayout() {
        towerAdapters = IntStream.of(R.id.tower1, R.id.tower2, R.id.tower3).<RecyclerView>mapToObj(this::findViewById)
                .map(view -> { // map moreover sets callback and binds adapter
                    TowerAdapter adapter = new TowerAdapter(
                            this,
                            ringsCount,
                            view.getHeight() / ringsCount
                    );
                    view.setAdapter(adapter);
                    view.setOnTouchListener(new RecyclerViewTouchToClickPerformer());
                    view.setOnClickListener(this);
                    return adapter;
                }).toArray(TowerAdapter[]::new);
        stateHolder.uiState.observe(this, towers -> {
            if (towers instanceof StateHolder.UiState.UpdateState) { // TODO accessibility and win check
                StateHolder.UiState.UpdateState state = (StateHolder.UiState.UpdateState) towers;
                IntStream.range(0, state.towers.length).forEach(i -> towerAdapters[i].setRings(state.towers[i]));
            }
        });
        stateHolder.chosenTower.observe(this, chosenTower -> IntStream.range(0, towerAdapters.length)
                .filter(i -> towerAdapters[i].isChosen() != Integer.valueOf(i).equals(chosenTower))
                .forEach(i -> towerAdapters[i].setChosen(Integer.valueOf(i).equals(chosenTower))));
    }

    @Override
    public void onClick(View view) {
        List<Integer> towersIds = List.of(R.id.tower1, R.id.tower2, R.id.tower3);
        final int tower;
        if (view.getId() == R.id.exit_to_menu)
            showExitMenuDialog();
        else if (view.getId() == R.id.help)
            showHelpDialog();
        else if ((tower = towersIds.indexOf(view.getId())) != -1)
            onClickTower(tower);
    }

    private void onClickTower(int tower) {
        // This is a compact funny way to do the same :)
        // IntStream.of(tower).forEach(stateHolder.chosenTower.getValue() == null ? stateHolder::setChosenTower : stateHolder::moveChosenRingToTower);
        final Integer fromTower = stateHolder.chosenTower.getValue();
        if (fromTower == null) {
            if (towerAdapters[tower].getItemCount() > 0)
                stateHolder.setChosenTower(tower);
        } else if (
                fromTower == tower ||
                (towerAdapters[tower].topRingSize() > 0 && towerAdapters[fromTower].topRingSize() > towerAdapters[tower].topRingSize())
        )
            stateHolder.setChosenTower(null);
        else
            stateHolder.moveChosenRingToTower(tower);
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

class RecyclerViewTouchToClickPerformer implements View.OnTouchListener {
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP)
            view.performClick();
        return true;
    }
}

class StateHolderFactory implements ViewModelProvider.Factory {

    private final TowersGenerator generator;
    private final int rings;

    public StateHolderFactory(TowersGenerator generator, int rings) {
        this.generator = generator;
        this.rings = rings;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return StateHolder.class.isAssignableFrom(modelClass) ?
                (T) new StateHolder(generator, rings) :
                ViewModelProvider.Factory.super.create(modelClass);
    }
}

