package com.hanoitower.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hanoitower.R;
import com.hanoitower.ringview.RingView;

/* package-private */ class TowerAdapter extends RecyclerView.Adapter<TowerAdapter.Holder> {
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
     * Notifies itself about changes on its own
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setRings(@NonNull int[] rings) {
        this.rings = rings;
        notifyDataSetChanged();
        if (rings.length == 0)
            isChosen = false;
    }

    public int topRingSize() {
        return rings.length == 0 ? 0 : rings[0];
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
            ringView.setAtPointer(isChosen && getAdapterPosition() == 0);
        }
    }
}
