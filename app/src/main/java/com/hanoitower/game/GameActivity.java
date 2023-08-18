package com.hanoitower.game;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.hanoitower.ringview.RingView;

import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    private void onLayout() {
        towerAdapters = IntStream.of(R.id.tower1, R.id.tower2, R.id.tower3).<RecyclerView>mapToObj(this::findViewById)
                .map(view -> {
                    TowerAdapter adapter = new TowerAdapter(
                            this,
                            ringsCount,
                            view.getHeight() / ringsCount
                    );
                    view.setAdapter(adapter);
                    return adapter;
                }).toArray(TowerAdapter[]::new);
        stateHolder.rings.observe(this, towers -> {
            if (towers instanceof StateHolder.UiState.UpdateState) { // TODO accessibility and win check
                StateHolder.UiState.UpdateState state = (StateHolder.UiState.UpdateState) towers;
                IntStream.range(0, state.towers.length).forEach(i -> towerAdapters[i].setRings(state.towers[i]));
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

class TowerAdapter extends RecyclerView.Adapter<TowerAdapter.Holder> {
    private final LayoutInflater inflater;
    private final int maxRing, ringHeight;
    private boolean isChosen = false;
    @NonNull
    private int[] rings = {};

    public TowerAdapter(@NonNull Context context, int maxRing, int ringHeight) {
        this.inflater = LayoutInflater.from(context);
        this.maxRing = maxRing;
        this.ringHeight = ringHeight;
    }

    @Override
    @NonNull
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder((RingView) inflater.inflate(R.layout.tower_item, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.bind(rings[position]);
    }

    @Override
    public int getItemCount() {
        return rings.length;
    }

    /**
     *  Notifies itself about changes on its own
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setRings(@NonNull int[] rings) {
        this.rings = rings;
        notifyDataSetChanged();
        if (rings.length == 0)
            isChosen = false;
    }

    public boolean isChosen() {
        return isChosen;
    }

    /**
     * @return false if there is no rings, true otherwise
     */
    public boolean setChosen(boolean chosen) {
        if (rings.length == 0)
            return false;
        isChosen = chosen;
        notifyItemChanged(0);
        return true;
    }

    public class Holder extends RecyclerView.ViewHolder {
        public final RingView ringView;

        private Holder(@NonNull RingView ringView) {
            super(ringView);
            this.ringView = ringView;
            ringView.setRingMaxLevel(maxRing);
            ringView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ringHeight
            ));
        }

        private void bind(int ring) {
            ringView.setRingLevel(ring);
        }
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

