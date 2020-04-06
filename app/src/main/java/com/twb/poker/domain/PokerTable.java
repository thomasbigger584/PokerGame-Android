package com.twb.poker.domain;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.twb.poker.util.GenerateUtil.generateRandomFunds;
import static com.twb.poker.util.GenerateUtil.generateRandomName;
import static com.twb.poker.util.SleepUtil.dealSleep;

@Data
@EqualsAndHashCode(callSuper = true)
public class PokerTable extends ArrayList<PokerPlayer> {
    private static final int NO_CARDS_FOR_PLAYER_DEAL = 2;
    private static final int NO_DEALER = -1;

    private final PokerTableCallback callback;

    private final CommunityCards communityCards = new CommunityCards();
    private List<Card> deckOfCards;
    private int deckCardPointer;

    public PokerTable(PokerTableCallback callback) {
        this.callback = callback;
    }

    /*
     * Player Functionality
     */
    private void addPlayer(String displayName, double funds, boolean currentPlayer, int tableOrder) {
        PlayerBank playerBank = new PlayerBank(funds);
        PlayerUser playerUser = new PlayerUser(displayName, playerBank);
        PokerPlayer pokerPlayer = new PokerPlayer(playerUser, currentPlayer, tableOrder);
        add(pokerPlayer);
    }

    public void addPlayer(String displayName, boolean currentPlayer, int tableOrder) {
        double funds;
        if (currentPlayer) {
            funds = generateRandomFunds(100, 300);
        } else {
            funds = generateRandomFunds(75, 200);
        }
        addPlayer(displayName, funds, currentPlayer, tableOrder);
    }

    public void addPlayer(int tableOrder) {
        addPlayer(generateRandomName(), false, tableOrder);
    }

    /*
     * Poker Table Game Functionality
     */
    public void init() {
        reset();
        reassignPokerTableForDealer();
        this.deckCardPointer = 0;
        this.deckOfCards = DeckOfCardsFactory.getCards(true);
        callback.onUpdatePlayersOnTable(this);
    }

    public void initDeal() {
        for (int dealRoundIndex = 0; dealRoundIndex < NO_CARDS_FOR_PLAYER_DEAL; dealRoundIndex++) {
            for (PokerPlayer pokerPlayer : this) {
                Card card = getCard();
                pokerPlayer.update(card);
                callback.onDealCardToPlayer(pokerPlayer, card);
            }
        }
    }

    public void performPlayerBetTurn() {
        for (int index = 0; index < size(); index++) {
            PokerPlayer prevPokerPlayer = getPrevious(index);
            PokerPlayer thisPokerPlayer = get(index);
            setTurnPlayer(prevPokerPlayer, false);
            if (thisPokerPlayer.isFolded()) {
                continue;
            }
            setTurnPlayer(thisPokerPlayer, true);
            if (thisPokerPlayer.isCurrentPlayer()) {
                callback.onCurrentPlayerBetTurn(thisPokerPlayer);
            } else {
                callback.onOtherPlayerBetTurn(thisPokerPlayer);
            }
        }
        setTurnPlayer(get(size() - 1), false);
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
        Card card = getCard();
        communityCards.add(card);
        callback.onDealCommunityCard(card, cardType);
        dealSleep();
    }

    private Card getCard() {
        final Card card = deckOfCards.get(deckCardPointer);
        deckCardPointer++;
        return card;
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
                PokerPlayer newDealerPlayer;
                if (index == size() - 1) {
                    newDealerPlayer = get(0);
                } else {
                    newDealerPlayer = get(index + 1);
                }
                setDealerPlayer(newDealerPlayer, true);
                setDealerPlayer(player, false);
                break;
            }
        }
    }

    private void setDealerPlayer(PokerPlayer pokerPlayer, boolean dealer) {
        pokerPlayer.setDealerPlayer(dealer);
        callback.onPlayerDealer(pokerPlayer, dealer);
    }

    private void setTurnPlayer(PokerPlayer pokerPlayer, boolean turn) {
        pokerPlayer.setTurnPlayer(turn);
        callback.onPlayerTurn(pokerPlayer, turn);
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
            callback.onPlayerFold(currentPlayer);
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
        communityCards.clear();
        for (PokerPlayer pokerPlayer : this) {
            pokerPlayer.reset();
        }
    }

    public interface PokerTableCallback {
        void onUpdatePlayersOnTable(PokerTable pokerTable);

        void onCurrentPlayerBetTurn(PokerPlayer pokerPlayer);

        void onOtherPlayerBetTurn(PokerPlayer pokerPlayer);

        void onDealCommunityCard(Card card, CommunityCardType cardType);

        void onDealCardToPlayer(PokerPlayer pokerPlayer, Card card);

        void onPlayerTurn(PokerPlayer pokerPlayer, boolean turn);

        void onPlayerDealer(PokerPlayer pokerPlayer, boolean dealer);

        void onPlayerFold(PokerPlayer pokerPlayer);
    }
}
