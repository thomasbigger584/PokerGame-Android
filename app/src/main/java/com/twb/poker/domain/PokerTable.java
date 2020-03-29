package com.twb.poker.domain;


import com.twb.poker.layout.CardPairLayout;
import com.twb.poker.layout.CommunityCardLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PokerTable extends ArrayList<PokerPlayer> {
    private static final String TAG = PokerTable.class.getSimpleName();
    private static final int NO_DEALER = -1;

    private final CommunityCardLayout communityCardLayout;
    private final CommunityCards communityCards;

    public PokerTable(CommunityCardLayout communityCardLayout) {
        this.communityCardLayout = communityCardLayout;
        this.communityCards = new CommunityCards();
    }

    public void initPokerTable() {
        for (PokerPlayer pokerPlayer : this) {
            pokerPlayer.updatePokerPlayerOnTable();
        }
    }

    public void dealCommunityCard(final Card card, CommunityCardType cardType) {
        communityCards.add(card);
        if (cardType.isPlayable()) {
            communityCardLayout.dealCard(card, cardType);
        }
    }

    public void addPlayer(CardPairLayout cardPairLayout, String displayName,
                          double funds, boolean currentPlayer) {
        PlayerBank playerBank = new PlayerBank(funds);
        PlayerUser playerUser = new PlayerUser(displayName, playerBank);
        PokerPlayer pokerPlayer = new PokerPlayer(playerUser, cardPairLayout, currentPlayer);
        add(pokerPlayer);
    }

    public PokerTable reassignPokerTableForDealer() {
        int dealerIndex = getDealerIndex();

        if (dealerIndex == NO_DEALER) {
            Random random = new Random();
            int newDealerIndex = random.nextInt(size());
            PokerPlayer pokerPlayer = get(newDealerIndex);
            pokerPlayer.setDealerPlayer(true);
            dealerIndex = newDealerIndex;
        }

        if (dealerIndex == size()) {
            return this;
        }

        //take the next player along for dealing
        dealerIndex++;

        PokerTable pokerTable = new PokerTable(communityCardLayout);
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
        return NO_DEALER;
    }

    //todo might need revisited / test this actually rotates
    public void rotateDealer() {
        for (int index = 0; index < size(); index++) {
            PokerPlayer player = get(index);
            if (player.isDealerPlayer()) {
                if (index == size() - 1) {
                    PokerPlayer firstPlayer = get(0);
                    firstPlayer.setDealerPlayer(true);
                } else {
                    PokerPlayer nextPlayer = get(index + 1);
                    nextPlayer.setDealerPlayer(true);
                }
                player.setDealerPlayer(false);
                break;
            }
        }
    }

    // return a list as there could be a winning tie
    public List<PokerPlayer> evaluateAndGetWinners() {
        List<Card> playableCards = communityCards.getPlayableCards();

        //using copy as dont want to sort the existing table.
        List<PokerPlayer> copyPokerTable = new ArrayList<>(this);
        for (PokerPlayer pokerPlayer : copyPokerTable) {
            Hand hand = pokerPlayer.getHand();
            hand.setCommunityCards(playableCards);
            hand.calculateRank();
        }
        Collections.sort(copyPokerTable, (o1, o2) -> {
            Hand o1Hand = o1.getHand();
            Hand o2Hand = o2.getHand();
            return o2Hand.compareTo(o1Hand);
        });

        List<PokerPlayer> handWinners = new ArrayList<>();
        int winningRankValue = copyPokerTable.get(0).getHand().getRank();
        for (PokerPlayer pokerPlayer : copyPokerTable) {
            Hand hand = pokerPlayer.getHand();
            if (hand.getRank() == winningRankValue) {
                handWinners.add(pokerPlayer);
            }
        }

        //sort cards in hand in ascending order
        for (PokerPlayer pokerPlayer : handWinners) {
            Collections.sort(pokerPlayer.getHand(), (o1, o2) -> {
                Integer o1Rank = o1.getRank();
                Integer o2Rank = o2.getRank();
                return o1Rank.compareTo(o2Rank);
            });
        }
        return handWinners;
    }

    public PokerPlayer getPrevious(int index) {
        if (index == 0) {
            return get(size() - 1);
        } else {
            return get(index - 1);
        }
    }

    public void reset() {
        communityCardLayout.reset();
        communityCards.reset();
        for (PokerPlayer pokerPlayer : this) {
            pokerPlayer.reset();
        }
    }
}
