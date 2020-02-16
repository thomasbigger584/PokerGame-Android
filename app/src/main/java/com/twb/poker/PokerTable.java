package com.twb.poker;

import com.twb.poker.domain.Card;
import com.twb.poker.domain.CommunityCardType;
import com.twb.poker.domain.CommunityCards;
import com.twb.poker.domain.Hand;
import com.twb.poker.domain.PlayerBank;
import com.twb.poker.domain.PlayerUser;
import com.twb.poker.layout.CardPairLayout;
import com.twb.poker.layout.CommunityCardLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PokerTable extends ArrayList<PokerPlayer> {
    private static final int NO_DEALER = -1;

    private final CommunityCardLayout communityCardLayout;
    private final CommunityCards communityCards;

    public void initPokerTable() {
        for (PokerPlayer pokerPlayer : this) {
            pokerPlayer.updatePokerPlayerOnTable();
        }
    }

    public PokerTable(CommunityCardLayout communityCardLayout) {
        this.communityCardLayout = communityCardLayout;
        this.communityCards = new CommunityCards();
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
        PokerPlayer pokerPlayer = new PokerPlayer(cardPairLayout, playerUser, currentPlayer);
        add(pokerPlayer);
    }

    public PokerTable reorderPokerTableForDealer() {
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

    public List<PokerPlayer> evaluateAndGetWinners() {
        List<Card> playableCards = communityCards.getPlayableCards();
        for (PokerPlayer pokerPlayer : this) {
            Hand hand = pokerPlayer.getHand();
            hand.setCommunityCards(playableCards);
            hand.calculateRank();
        }
        Collections.sort(this, (o1, o2) -> {
            Hand o1Hand = o1.getHand();
            Hand o2Hand = o2.getHand();
            return o2Hand.compareTo(o1Hand);
        });

        List<PokerPlayer> handWinners = new ArrayList<>();
        int winningRankValue = get(0).getHand().getRank();
        for (PokerPlayer pokerPlayer : this) {
            Hand hand = pokerPlayer.getHand();
            if (hand.getRank() == winningRankValue) {
                handWinners.add(pokerPlayer);
            }
        }
        return handWinners;
    }
}
