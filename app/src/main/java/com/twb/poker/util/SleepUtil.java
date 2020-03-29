package com.twb.poker.util;

public class SleepUtil {
    public static final double PLAYER_RESPONSE_LOOP_IN_SECONDS = 0.1d;
    private static final int MULTIPLIER = 10;
    private static final double ROUND_DELAY_IN_MS = 3 * 1000;
    private static final double GAME_DELAY_IN_MS = 5 * 1000;
    private static final double DEAL_TIME_IN_MS = 1.25 * 1000;

    public static void dealSleep() {
        sleep(DEAL_TIME_IN_MS);
    }

    public static void playerTurnSleep() {
        sleep(PLAYER_RESPONSE_LOOP_IN_SECONDS * 1000);
    }

    public static void roundDelaySleep() {
        sleep(ROUND_DELAY_IN_MS);
    }

    public static void gameDelaySleep() {
        sleep(GAME_DELAY_IN_MS);
    }

    private static void sleep(double sleep) {
        try {
            Thread.sleep(Math.round(sleep / MULTIPLIER));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
