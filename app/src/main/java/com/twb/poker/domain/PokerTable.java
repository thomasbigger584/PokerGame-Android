package com.twb.poker.domain;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import lombok.Setter;

import static com.twb.poker.util.GenerateUtil.generateRandomFunds;
import static com.twb.poker.util.GenerateUtil.generateRandomName;
import static com.twb.poker.util.SleepUtil.dealSleep;

@Setter
public class PokerTable extends ArrayList<PokerPlayer> {
    private static final int NO_CARDS_FOR_PLAYER_DEAL = 2;
    private static final int NO_DEALER = -1;

    private final PokerTableCallback pokerTableCallback;
    private final PokerThreadCallback pokerThreadCallback;

    private final CommunityCards communityCards = new CommunityCards();
    private List<Card> deckOfCards;
    private int deckCardPointer;

    public PokerTable(PokerTableCallback pokerTableCallback,
                      PokerThreadCallback pokerThreadCallback) {
        this.pokerTableCallback = pokerTableCallback;
        this.pokerThreadCallback = pokerThreadCallback;
    }

    /*
     * Player Functionality
     */

    private void addPlayer(String displayName, double funds, boolean currentPlayer) {
        PlayerBank playerBank = new PlayerBank(funds);
        PlayerUser playerUser = new PlayerUser(displayName, playerBank);
        PokerPlayer pokerPlayer = new PokerPlayer(playerUser, currentPlayer);
        add(pokerPlayer);
    }

    public void addPlayer(String displayName, boolean currentPlayer) {
        double funds;
        if (currentPlayer) {
            funds = generateRandomFunds(100, 300);
        } else {
            funds = generateRandomFunds(75, 200);
        }
        addPlayer(displayName, funds, currentPlayer);
    }

    public void addPlayer() {
        addPlayer(generateRandomName(), false);
    }

    /*
     * Poker Table Game Functionality
     */
    public void init() {
        reset();
        reassignPokerTableForDealer();
        this.deckCardPointer = 0;
        this.deckOfCards = DeckOfCardsFactory.getCards(true);
        initPokerTable();
    }

    public void initPokerTable() {
        for (PokerPlayer pokerPlayer : this) {
            pokerPlayer.updatePokerPlayerOnTable();
        }
    }

    public void initDeal() {
        for (int dealRoundIndex = 0; dealRoundIndex < NO_CARDS_FOR_PLAYER_DEAL; dealRoundIndex++) {
            for (PokerPlayer pokerPlayer : this) {
                final Card cardToDeal = deckOfCards.get(deckCardPointer);
                pokerPlayer.update(cardToDeal);
                deckCardPointer++;
                dealSleep();
            }
        }
    }

    public void performPlayerBetTurn() {
        for (int index = 0; index < size(); index++) {
            PokerPlayer prevPokerPlayer = getPrevious(index);
            PokerPlayer thisPokerPlayer = get(index);
            prevPokerPlayer.setTurnPlayer(false);
            if (thisPokerPlayer.isFolded()) {
                continue;
            }
            thisPokerPlayer.setTurnPlayer(true);
            if (thisPokerPlayer.isCurrentPlayer()) {
                pokerThreadCallback.onCurrentPlayerBetTurn(thisPokerPlayer);
            } else {
                pokerThreadCallback.onOtherPlayerBetTurn(thisPokerPlayer);
            }
        }
        get(size() - 1).setTurnPlayer(false);
    }

    public void flopDeal() {
        dealCommunityCard(CommunityCardType.BURN_PRE_FLOP);
        dealCommunityCard(CommunityCardType.FLOP_1);
        dealCommunityCard(CommunityCardType.FLOP_2);
        dealCommunityCard(CommunityCardType.FLOP_3);
    }

    public void turnDeal() {
        dealCommunityCard(CommunityCardType.BURN_PRE_TURN);
        dealCommunityCard(CommunityCardType.TURN);
    }

    public void riverDeal() {
        dealCommunityCard(CommunityCardType.BURN_PRE_RIVER);
        dealCommunityCard(CommunityCardType.RIVER);
    }

    private void dealCommunityCard(CommunityCardType cardType) {
        final Card card = deckOfCards.get(deckCardPointer);
        dealCommunityCard(card, cardType);
        deckCardPointer++;
        dealSleep();
    }

    private void dealCommunityCard(final Card card, CommunityCardType cardType) {
        communityCards.add(card);
        pokerTableCallback.dealCommunityCard(card, cardType);
    }


    private void reassignPokerTableForDealer() {
        int dealerIndex = getDealerIndex();

        if (dealerIndex == NO_DEALER) {
            Random random = new Random();
            int newDealerIndex = random.nextInt(size());
            PokerPlayer pokerPlayer = get(newDealerIndex);
            pokerPlayer.setDealerPlayer(true);
            dealerIndex = newDealerIndex;
        }
        if (dealerIndex == size()) {
            return;
        }
        dealerIndex++; //next player for dealing

        List<PokerPlayer> newPokerPlayerOrderList = new ArrayList<>();
        for (int playerIndex = dealerIndex; playerIndex < size(); playerIndex++) {
            newPokerPlayerOrderList.add(get(playerIndex));
        }
        for (int playerIndex = 0; playerIndex < dealerIndex; playerIndex++) {
            newPokerPlayerOrderList.add(get(playerIndex));
        }
        clear();
        addAll(newPokerPlayerOrderList);
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

        removeFoldedPlayers(copyPokerTable);

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

    private void removeFoldedPlayers(List<PokerPlayer> copyPokerTable) {
        final Iterator<PokerPlayer> each = copyPokerTable.iterator();
        while (each.hasNext()) {
            if (each.next().isFolded()) {
                each.remove();
            }
        }
    }

    private PokerPlayer getPrevious(int index) {
        if (index == 0) {
            return get(size() - 1);
        } else {
            return get(index - 1);
        }
    }

    public void foldCurrentPlayer() {
        PokerPlayer currentPlayer = getCurrentPlayer();
        if (currentPlayer != null) {
            currentPlayer.setFolded(true);
        }
    }

    private PokerPlayer getCurrentPlayer() {
        for (PokerPlayer pokerPlayer : this) {
            if (pokerPlayer.isCurrentPlayer()) {
                return pokerPlayer;
            }
        }
        return null;
    }

    private void reset() {
//        communityCardLayout.reset();
        communityCards.clear();
        for (PokerPlayer pokerPlayer : this) {
            pokerPlayer.reset();
        }
    }

    public interface PokerTableCallback {
        void dealCommunityCard(Card card, CommunityCardType cardType);

        void onAlert();

        void onControlsShow();

        void onControlsHide();

        void onPercentageTimeLeft(int percentage);
    }

    public interface PokerThreadCallback {
        void onCurrentPlayerBetTurn(PokerPlayer pokerPlayer);

        void onOtherPlayerBetTurn(PokerPlayer pokerPlayer);
    }
}
