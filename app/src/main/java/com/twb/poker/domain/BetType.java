package com.twb.poker.domain;

import com.twb.poker.util.WeightedRandomCollection;

public enum BetType {
    ANTE, BLIND, CHECK, CALL, BET, FOLD, RAISE;

    public static BetType getInitialBetType() {
        WeightedRandomCollection<BetType> initialBetTypes = new WeightedRandomCollection<>();
        initialBetTypes.
                add(65, CHECK).
                add(35, BET);
        return initialBetTypes.next();
    }

    public boolean isNonPayable() {
        BetType betType = BetType.valueOf(name());
        return betType == FOLD || betType == CHECK;
    }

    public BetType getRandomAiBetType() {
        return getNextBetTypeOptions().next();
    }

    private WeightedRandomCollection<BetType> getNextBetTypeOptions() {
        BetType betType = BetType.valueOf(name());

        WeightedRandomCollection<BetType> nextBetTypes = new WeightedRandomCollection<>();
        switch (betType) {
            case CHECK: {
                return nextBetTypes.
                        add(80, CHECK).
                        add(20, BET);
            }
            case CALL: {
                return nextBetTypes.
                        add(70, CALL).
                        add(20, RAISE).
                        add(10, FOLD);
            }
            case RAISE: {
                return nextBetTypes.
                        add(70, CALL).
                        add(30, FOLD);
            }
            default: {
                return nextBetTypes.
                        add(100, FOLD);
            }
        }
    }
}
