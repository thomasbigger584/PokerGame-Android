package com.twb.poker.domain;

import com.twb.poker.util.WeightedRandomCollection;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum BetType {
    ANTE, BLIND, CHECK, CALL, BET, FOLD, RAISE;


    public static BetType getInitialAiBetType() {
        WeightedRandomCollection<BetType> initialBetTypes = new WeightedRandomCollection<>();
        initialBetTypes.
                add(65, CHECK).
                add(35, BET);
        return initialBetTypes.next();
    }

    public static List<BetType> getInitialBetTypeOptions() {
        return Arrays.asList(CHECK, BET);
    }

    public boolean isNonPayable() {
        BetType betType = BetType.valueOf(name());
        return betType == FOLD || betType == CHECK;
    }

    public List<BetType> getNextBetTypeOptions() {
        BetType betType = BetType.valueOf(name());
        switch (betType) {
            case CHECK: {
                return Arrays.asList(CHECK, BET);
            }
            case BET:
            case CALL: {
                return Arrays.asList(CALL, RAISE, FOLD);
            }
            case RAISE: {
                return Arrays.asList(CALL, FOLD);
            }
            default: {
                return Collections.singletonList(FOLD);
            }
        }
    }

    public BetType getRandomAiNextBetType() {
        BetType betType = BetType.valueOf(name());

        WeightedRandomCollection<BetType> nextBetTypes = new WeightedRandomCollection<>();
        switch (betType) {
            case CHECK: {
                nextBetTypes.
                        add(80, CHECK).
                        add(20, BET);
                break;
            }
            case CALL: {
                nextBetTypes.
                        add(70, CALL).
                        add(20, RAISE).
                        add(10, FOLD);
                break;
            }
            case RAISE: {
                nextBetTypes.
                        add(70, CALL).
                        add(30, FOLD);
                break;
            }
            default: {
                nextBetTypes.
                        add(100, FOLD);
            }
        }
        return nextBetTypes.next();
    }
}
