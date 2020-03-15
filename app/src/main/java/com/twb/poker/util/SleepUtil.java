package com.twb.poker.util;

public class SleepUtil {

    public static final double PLAYER_RESPONSE_LOOP_IN_SECONDS = 0.1d;
    private static final double ROUND_DELAY_IN_MS = 3 * 1000;
    private static final double GAME_DELAY_IN_MS = 5 * 1000;
    private static final double DEAL_TIME_IN_MS = 1.25 * 1000;

    private SleepUtil() {

    }

    public static void dealSleep() {
        try {
            Thread.sleep(Math.round(DEAL_TIME_IN_MS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void playerTurnSleep() {
        try {
            Thread.sleep(Math.round(PLAYER_RESPONSE_LOOP_IN_SECONDS * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void roundDelaySleep() {
        try {
            Thread.sleep(Math.round(ROUND_DELAY_IN_MS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void gameDelaySleep() {
        try {
            Thread.sleep(Math.round(GAME_DELAY_IN_MS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
