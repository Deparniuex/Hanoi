package com.hanoitower.game;

abstract public class GameRules {
    public abstract GameSolver getGameSolver();
    public abstract TowersGenerator getTowersGenerator();
    public abstract WinAuditor getWinAuditor();

    public interface GameSolver {
        int[] solve(int[][] towers);
    }

    public interface TowersGenerator {
        /**
         * @return int[towersCount][ringsCount]
         */
        int[][] generate(int towersCount, int ringsCount);
    }

    public interface WinAuditor {
        boolean isCompleted(int[][] towers);
    }
}
