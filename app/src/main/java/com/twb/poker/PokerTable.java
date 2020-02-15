package com.twb.poker;

import java.util.ArrayList;
import java.util.Random;

public class PokerTable extends ArrayList<PokerPlayer> {

    public PokerTable reorderPokerTableForDealer() {
        int dealerIndex = getDealerIndex();

        if (dealerIndex == -1) {
            Random random = new Random();
            int newDealerIndex = random.nextInt(size());
            PokerPlayer pokerPlayer = get(newDealerIndex);
            pokerPlayer.setDealerPlayer(true);
        }

        dealerIndex = getDealerIndex();
        if (dealerIndex == size()) {
            return this;
        }

        //take the next player along for dealing
        dealerIndex++;

        PokerTable pokerTable = new PokerTable();
        for (int playerIndex = dealerIndex; playerIndex < size(); playerIndex++) {
            pokerTable.add(get(playerIndex));
        }
        for (int playerIndex = 0; playerIndex < dealerIndex; playerIndex++) {
            pokerTable.add(get(playerIndex));
        }
        return pokerTable;
    }

    private int getDealerIndex() {
        for (int index = 0; index < size(); index++) {
            PokerPlayer player = get(index);
            if (player.isDealerPlayer()) {
                return index;
            }
        }
        return -1;
    }

    public boolean checkMultipleDealers() {
        int dealerCount = 0;
        for (int index = 0; index < size(); index++) {
            PokerPlayer player = get(index);
            if (player.isDealerPlayer()) {
                dealerCount++;
            }
        }
        return dealerCount > 1;
    }
}
