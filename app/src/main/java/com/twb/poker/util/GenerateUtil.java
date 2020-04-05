package com.twb.poker.util;

import com.github.javafaker.Faker;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        BigDecimal bd = new BigDecimal(funds).setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
