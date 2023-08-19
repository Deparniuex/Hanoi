package com.hanoitower.game;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/* package-private */ class StateHolder extends ViewModel {

    public final MutableLiveData<UiState> uiState = new MutableLiveData<>();
    public final MutableLiveData<Integer> chosenTower = new MutableLiveData<>(null); // null means there is no chosen tower

    private final int[][] towers;

    public StateHolder(TowersGenerator generator, int ringsCount) {
        uiState.setValue(new UiState.UpdateState(trimStartingZeros(
                towers = generator.generate(3, ringsCount)
        )));
    }

    /**
     * @param chosenTower null to not choose tower
     */
    public void setChosenTower(@Nullable Integer chosenTower) {
        this.chosenTower.setValue(chosenTower);
    }

    public void moveChosenRingToTower(int tower) {
        if (chosenTower.getValue() == null)
            throw new IllegalStateException("There is no chosen tower to move ring from");
        int[] fromTower = towers[chosenTower.getValue()];
        int fromIndex = startingZerosCount(fromTower);
        towers[tower][startingZerosCount(towers[tower]) - 1] = fromTower[fromIndex];
        fromTower[fromIndex] = 0;
        chosenTower.setValue(null);
        uiState.setValue(new UiState.UpdateState(trimStartingZeros(towers)));
    }

    private static int startingZerosCount(int[] arr) {
        return IntStream.range(0, arr.length).filter(i -> arr[i] > 0)
                .findFirst().orElse(arr.length);
    }

    private static int[][] trimStartingZeros(int[][] arr) {
        return Stream.of(arr).map(subArr -> {
            int firstNonZeroValue = startingZerosCount(subArr);
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
