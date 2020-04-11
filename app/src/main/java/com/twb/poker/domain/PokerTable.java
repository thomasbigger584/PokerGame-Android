package com.twb.poker.domain;


import android.util.Log;

import com.twb.poker.util.GenerateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.twb.poker.util.GenerateUtil.generateRandomFunds;
import static com.twb.poker.util.GenerateUtil.generateRandomName;

@Data
@EqualsAndHashCode(callSuper = true)
public class PokerTable extends ArrayList<PokerPlayer> {
    private static final int NO_CARDS_FOR_PLAYER_DEAL = 2;
    private static final int NO_DEALER = -1;
    private static final int BET_COUNT_AMOUNT = 1;
    private static final int RAISE_COUNT_AMOUNT = 2;
    private static final int INITIAL_BET_COUNT = 0;
    private static final String TAG = PokerTable.class.getSimpleName();
    private final PokerTableCallback callback;
    private final CommunityCards communityCards = new CommunityCards();
    private List<Card> deckOfCards;
    private int deckCardPointer;
    private Pot pot;
    private double minimumBet = 50d;

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
        resetBets();
        reassignPokerTableForDealer();
        this.deckCardPointer = 0;
        this.deckOfCards = DeckOfCardsFactory.getCards(true);
        callback.onUpdatePlayersOnTable(this);
        callback.onEvent("New Round Started");
    }

    private void resetBets() {
        for (PokerPlayer pokerPlayer : this) {
            pokerPlayer.setBetCount(INITIAL_BET_COUNT);
        }
        pot = new Pot();
        pot.setPot(0d);
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

    private boolean isMorePlayersToBet() {
        for (PokerPlayer pokerPlayer : this) {
            if (pokerPlayer.getBetCount() > INITIAL_BET_COUNT) {
                return true;
            }
        }
        return false;
    }

    public void performPlayerBetTurn() {

//        while (isMorePlayersToBet()) {

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
                performAiPlayerBet(thisPokerPlayer);
            }
        }
        setTurnPlayer(get(size() - 1), false);
//        }
    }

    private void performAiPlayerBet(PokerPlayer pokerPlayer) {
        BetType previousBetType = pot.getCurrentBetType();
        BetType nextBetType;
        if (previousBetType != null) {
            nextBetType = previousBetType.getRandomAiBetType();
        } else {
            nextBetType = BetType.getInitialBetType();
        }
        if (nextBetType.isNonPayable()) {
            setBetAmount(pokerPlayer, nextBetType);
        } else {
            PlayerUser playerUser = pokerPlayer.getPlayerUser();
            PlayerBank playerBank = playerUser.getBank();
            double randomBet = GenerateUtil.generateAiBet(minimumBet, playerBank.getFunds());
            setBetAmount(pokerPlayer, nextBetType, randomBet);
        }
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
                PokerPlayer newDealerPlayer = getNext(index);
                setDealerPlayer(player, false);
                setDealerPlayer(newDealerPlayer, true);
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

        if (copyPokerTable.isEmpty()) {
            return Collections.emptyList();
        }
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
        PokerPlayer winningPokerPlayer = copyPokerTable.get(0);
        Hand winningHand = winningPokerPlayer.getHand();
        if (winningHand == null) {
            return Collections.emptyList();
        }
        int winningRankValue = winningHand.getRank();
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

    public void foldCurrentPlayer() {
        PokerPlayer currentPlayer = getCurrentPlayer();
        if (currentPlayer != null) {
            setBetAmount(currentPlayer, BetType.FOLD);
        }
    }

    public PokerPlayer getCurrentPlayer() {
        for (PokerPlayer pokerPlayer : this) {
            if (pokerPlayer.isCurrentPlayer()) {
                return pokerPlayer;
            }
        }
        return null;
    }

    private PokerPlayer getPrevious(int index) {
        if (index == 0) {
            return get(size() - 1);
        } else {
            return get(index - 1);
        }
    }

    private PokerPlayer getNext(int index) {
        if (index == size() - 1) {
            return get(0);
        } else {
            return get(index + 1);
        }
    }

    private void reset() {
        pot = new Pot();
        communityCards.clear();
        for (PokerPlayer pokerPlayer : this) {
            pokerPlayer.reset();
        }
    }

    private void setBetAmount(PokerPlayer pokerPlayer, BetType type) {
        setBetAmount(pokerPlayer, type, null);
    }

    public void setBetAmount(PokerPlayer pokerPlayer, BetType type, Double amount) {
        Log.e(TAG, "");
        if (type == BetType.FOLD) {
            pokerPlayer.setBetCount(INITIAL_BET_COUNT);
            pokerPlayer.setFolded(true);
            callback.onPlayerFold(pokerPlayer);
            callback.onEvent(pokerPlayer.getPlayerUser().getDisplayName() + " folded");
            return;
        }
        pot.setCurrentBetType(type);
        if (type == BetType.CHECK) {
            pokerPlayer.setBetCount(INITIAL_BET_COUNT);
            callback.onEvent(pokerPlayer.getPlayerUser().getDisplayName() + " checked");
            return;
        }
        if (amount != null) {
            PlayerUser playerUser = pokerPlayer.getPlayerUser();
            PlayerBank playerBank = playerUser.getBank();
            playerBank.setFunds(playerBank.getFunds() - amount);

            pot.setCurrentBet(amount);
            pot.setPot(pot.getPot() + amount);
        }
        if (type == BetType.BET) {
            pokerPlayer.setBetCount(pokerPlayer.getBetCount() + BET_COUNT_AMOUNT);
            callback.onEvent(pokerPlayer.getPlayerUser().getDisplayName() + " bet " + amount);
        } else if (type == BetType.RAISE) {
            pokerPlayer.setBetCount(pokerPlayer.getBetCount() + RAISE_COUNT_AMOUNT);
            callback.onEvent(pokerPlayer.getPlayerUser().getDisplayName() + " raised by " + amount);
        } else if (type == BetType.CALL) {
            pokerPlayer.setBetCount(INITIAL_BET_COUNT);
            callback.onEvent(pokerPlayer.getPlayerUser().getDisplayName() + " called " + amount);
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

        void onEvent(String event);
    }
}
