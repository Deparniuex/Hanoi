package com.hanoitower.game;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/* package-private */ class StateHolder extends ViewModel {
    private final static int
            DElAY_CHOOSE = 200,
            DELAY_MOVE = 1000;

    public final MutableLiveData<Ring[][]> towersState = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isAccessible = new MutableLiveData<>(true);
    public final MutableLiveData<Integer> chosenTower = new MutableLiveData<>(null); // null means there is no chosen tower
    public final MutableLiveData<Boolean> isWon = new MutableLiveData<>(false);

    private final WinAuditor winAuditor;
    private final GameSolver solver;
    private final int[][] towers;
    private final int[] ringsColor;
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private final Random rand = new Random(System.currentTimeMillis());
    private Timer autoSolveTimer;

    public StateHolder(TowersGenerator generator, WinAuditor winAuditor, GameSolver solver, int ringsCount) {
        this.winAuditor = winAuditor;
        this.solver = solver;
        towers = generator.generate(3, ringsCount);
        ringsColor = IntStream.range(0, ringsCount).map(i -> generateColor()).toArray();
        exposeTowers();
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
        int[]
                fromTower = towers[chosenTower.getValue()],
                toTower = towers[tower];
        int
                fromIndex = startingZerosCount(fromTower),
                toIndex = startingZerosCount(toTower) - 1;
        toTower[toIndex] = fromTower[fromIndex];
        fromTower[fromIndex] = 0;
        chosenTower.setValue(null);
        exposeTowers();
        if (Boolean.FALSE.equals(isWon.getValue()) && winAuditor.isCompleted(towers))
            isWon.setValue(true);
    }

    public void startAutoSolving() {
        if (autoSolveTimer != null)
            throw new IllegalStateException("Auto solving is already started");
        autoSolveTimer = new Timer();
        int[] moves = solver.solve(Stream.of(towers).map(arr -> IntStream.of(arr).toArray()).toArray(int[][]::new));
        class Task extends TimerTask {
            private final int index;

            public Task() {
                this(0);
            }

            public Task(int index) {
                this.index = index;
            }

            @Override
            public void run() {
                mainThreadHandler.post(() -> {
                    if (index % 2 == 0)
                        setChosenTower(moves[index]);
                    else
                        moveChosenRingToTower(moves[index]);
                    if (index + 1 < moves.length)
                        autoSolveTimer.schedule(new Task(index + 1), (index + 1) % 2 == 0 ? DElAY_CHOOSE : DELAY_MOVE);
                    else
                        stopAutoSolving();
                });
            }
        }
        isAccessible.setValue(false);
        autoSolveTimer.schedule(new Task(), DElAY_CHOOSE);
    }

    public void stopAutoSolving() {
        if (autoSolveTimer == null)
            throw new IllegalStateException("Auto solving is not started");
        autoSolveTimer.cancel();
        autoSolveTimer = null;
        isAccessible.setValue(true);
    }

    private void exposeTowers() {
        towersState.setValue(
                Stream.of(trimStartingZeros(towers)).map(
                        tower -> IntStream.of(tower).mapToObj(
                                ring -> new Ring(ring, ringsColor[ring - 1])
                        ).toArray(Ring[]::new)
                ).toArray(Ring[][]::new)
        );
    }

    @ColorInt
    private int generateColor() {
        int
                r = rand.nextInt(256),
                g = rand.nextInt(256),
                b = 256 * 2 - r - g;
        return (int) (b + 256 * (g + 256 * (r + 256L * 255)));
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
}
