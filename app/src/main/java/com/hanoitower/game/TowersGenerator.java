package com.hanoitower.game;

public interface TowersGenerator {
    /**
     * @return int[towersCount][ringsCount]
     */
    int[][] generate(int towersCount, int ringsCount);
}
