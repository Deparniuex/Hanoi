package com.hanoitower.game;

import java.util.stream.IntStream;

public class BaseGameRules extends GameRules {

    @Override
    public GameSolver getGameSolver() {
        return new GameSolver();
    }

    @Override
    public TowersGenerator getTowersGenerator() {
        return new TowersGenerator();
    }

    @Override
    public WinAuditor getWinAuditor() {
        return new WinAuditor();
    }

    /* Caching

    private final Map<Class<Object>, Object> cache = new HashMap<>();

    @SuppressWarnings("unchecked cast")
    private <T> T get(Class<T> type) {
        if (!cache.containsKey(type))
            try {
                cache.put((Class<Object>) type, type.getConstructor().newInstance()); // must be public or set to accessible
            } catch (Exception exception) {
                throw new IllegalArgumentException(exception);
            }
        return (T) cache.get(type);
    }*/

    private static class GameSolver implements GameRules.GameSolver {
        @Override
        public int[] solve(int[][] towers) {
            return solve(towers, 0, 2, towers[0].length - 1).toArray();
        }

        private IntStream solve(int[][] towers, int fromTower, int toTower, int ringIndex) {
            if (ringIndex == 0 || towers[fromTower][ringIndex - 1] == 0) {
                towers[toTower][findTopRingIndex(towers[toTower]) - 1] = towers[fromTower][ringIndex];
                towers[fromTower][ringIndex] = 0;
                return IntStream.of(fromTower, toTower);
            }
            int bufferTower = IntStream.range(0, towers.length).filter(i -> i != fromTower && i != toTower).findAny().orElseThrow(
                    RuntimeException::new // unreachable (of course for 3 and more towers)
            );
            int bufferedRing = findTopRingIndex(towers[bufferTower]) - 1;
            return IntStream.concat(
                    IntStream.concat(
                            solve(towers, fromTower, bufferTower, ringIndex - 1),
                            solve(towers, fromTower, toTower, ringIndex)
                    ), solve(towers, bufferTower, toTower, bufferedRing)
            );
        }

        /**
         * @return first ring or length of array if there is no rings on this tower
         */
        private int findTopRingIndex(int[] tower) {
            return IntStream.range(0, tower.length)
                    .filter(i -> tower[i] != 0)
                    .findFirst()
                    .orElse(tower.length);
        }
    }

    private static class TowersGenerator implements GameRules.TowersGenerator {
        @Override
        public int[][] generate(int towersCount, int ringsCount) {
            int[][] towers = new int[towersCount][ringsCount];
            towers[0] = IntStream.range(1, ringsCount + 1).toArray();
            return towers;
        }
    }

    private static class WinAuditor implements GameRules.WinAuditor {
        @Override
        public boolean isCompleted(int[][] towers) {
            return towers[2][0] != 0;
        }
    }
}
