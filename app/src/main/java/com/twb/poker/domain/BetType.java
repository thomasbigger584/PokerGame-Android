package com.twb.poker.domain;

import com.twb.poker.util.WeightedRandomCollection;

public enum BetType {
    ANTE, BLIND, CHECK, CALL, BET, FOLD, RAISE;

    public static BetType getInitialBetType() {
        WeightedRandomCollection<BetType> initialBetTypes = new WeightedRandomCollection<>();
        initialBetTypes.add(60, CHECK).add(40, BET);
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
                return nextBetTypes.add(75, CHECK).add(25, BET);
            }
            case CALL: {
                return nextBetTypes.add(55, CALL).add(35, RAISE).add(10, FOLD);
            }
            case RAISE: {
                return nextBetTypes.add(25, CALL).add(75, FOLD);
            }
            default: {
                return nextBetTypes.add(100, FOLD);
            }
        }
    }
}
