package com.twb.poker.domain;

public enum PokerGameState {
    INIT_DEAL {
        @Override
        public PokerGameState nextState() {
            return INIT_DEAL_BET;
        }
    }, INIT_DEAL_BET {
        @Override
        public PokerGameState nextState() {
            return FLOP_DEAL;
        }
    }, FLOP_DEAL {
        @Override
        public PokerGameState nextState() {
            return FLOP_DEAL_BET;
        }
    }, FLOP_DEAL_BET {
        @Override
        public PokerGameState nextState() {
            return RIVER_DEAL;
        }
    }, RIVER_DEAL {
        @Override
        public PokerGameState nextState() {
            return RIVER_DEAL_BET;
        }
    }, RIVER_DEAL_BET {
        @Override
        public PokerGameState nextState() {
            return TURN_DEAL;
        }
    }, TURN_DEAL {
        @Override
        public PokerGameState nextState() {
            return TURN_DEAL_BET;
        }
    }, TURN_DEAL_BET {
        @Override
        public PokerGameState nextState() {
            return EVAL;
        }
    }, EVAL {
        @Override
        public PokerGameState nextState() {
            return FINISH;
        }
    }, FINISH {
        @Override
        public PokerGameState nextState() {
            return null;
        }
    };

    public abstract PokerGameState nextState();
}
