package com.hanoitower.game;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.stream.IntStream;
import java.util.stream.Stream;

class StateHolder extends ViewModel {

    public final MutableLiveData<UiState> rings = new MutableLiveData<>();

    public StateHolder(TowersGenerator generator, int ringsCount) {
        rings.setValue(new UiState.UpdateState(
                trimStartingZeros(generator.generate(3, ringsCount))
        ));
    }

    private static int[][] trimStartingZeros(int[][] arr) {
        return Stream.of(arr).map(subArr -> {
            int firstNonZeroValue = IntStream.range(0, subArr.length).filter(i -> subArr[i] > 0).findFirst().orElseGet(() -> subArr.length);
            int[] rings = new int[subArr.length - firstNonZeroValue];
            if (rings.length > 0)
                System.arraycopy(subArr, firstNonZeroValue, rings, 0, rings.length);
            return rings;
        }).toArray(int[][]::new);
    }

    public interface UiState {
        class UpdateState implements UiState {
            public final int[][] towers;
            public final boolean
                    isAccessible, // whether user can interact with towers
                    hasWon; // whether user completed task

            public UpdateState(int[][] towers) {
                this(towers, true);
            }

            public UpdateState(int[][] towers, boolean isAccessible) {
                this(towers, isAccessible, false);
            }

            public UpdateState(int[][] towers, boolean isAccessible, boolean hasWon) {
                this.towers = towers;
                this.isAccessible = isAccessible;
                this.hasWon = hasWon;
            }
        }
    }
}
