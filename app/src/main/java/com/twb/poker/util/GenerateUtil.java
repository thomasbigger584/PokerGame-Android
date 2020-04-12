package com.twb.poker.util;

import com.github.javafaker.Faker;

import java.util.Locale;
import java.util.Random;

public class GenerateUtil {

    private GenerateUtil() {
    }

    public static String generateRandomName() {
        return new Faker().name().firstName();
    }

    public static double generateRandomFunds(int rangeMin, int rangeMax) {
        Random r = new Random();
        double funds = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        String fundsStr = String.format(Locale.getDefault(), "%.2f", funds);
        return Double.valueOf(fundsStr);
    }

    public static double generateAiBet(double min, double max) {
        double newMin = min * 2;
        double newMax = (max / 3) * 2;

        if (newMin < newMax) {
            newMin = min;
            newMax = max;
        }
        Random r = new Random();
        double funds = newMin + (newMax - newMin) * r.nextDouble();
        String fundsStr = String.format(Locale.getDefault(), "%.2f", funds);
        return Double.valueOf(fundsStr);
    }
}
