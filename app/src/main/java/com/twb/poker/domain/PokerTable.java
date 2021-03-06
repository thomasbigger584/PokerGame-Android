package com.twb.poker.domain;


import com.twb.poker.util.GenerateUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.twb.poker.util.GenerateUtil.generateRandomFunds;
import static com.twb.poker.util.GenerateUtil.generateRandomName;

@Data
@EqualsAndHashCode(callSuper = true)
public class PokerTable extends ArrayList<PokerPlayer> {
    private static final double MINIMUM_BET = 20d;
    private static final Logger LOGGER = Logger.getLogger(PokerTable.class.getSimpleName());
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

    public void performPlayerBetTurn() {
        pot.clearForPlayerRound();

        do {
            for (int index = 0; index < size(); index++) {

                PokerPlayer prevPokerPlayer = getPrevious(index);
                PokerPlayer thisPokerPlayer = get(index);
                setTurnPlayer(prevPokerPlayer, false);
                if (thisPokerPlayer.isFolded()) {
                    continue;
                }
                setTurnPlayer(thisPokerPlayer, true);
                if (thisPokerPlayer.isCurrentPlayer()) {

                    BetAmountRequest betAmountRequest = getBetAmountRequest();
                    callback.onCurrentPlayerBetTurn(betAmountRequest);

                } else {

                    callback.onOtherPlayerBetTurn(thisPokerPlayer);
                    performAiPlayerBet(thisPokerPlayer);
                }

                if (pot.isAllPlayersPaidUp(getTableSize())) {
                    break;
                }
            }
            setTurnPlayer(get(size() - 1), false);

        } while (!pot.isAllPlayersPaidUp(getTableSize()));

    }

    @NotNull
    private BetAmountRequest getBetAmountRequest() {
        BetAmountRequest betAmountRequest = new BetAmountRequest();
        betAmountRequest.setBetTypes(getNextBetTypesForCurrent());
        Bet currentBet = pot.getCurrentBet();
        if (currentBet != null) {
            betAmountRequest.setAmount(currentBet.getBetAmount());
        } else {
            betAmountRequest.setAmount(MINIMUM_BET);
        }
        return betAmountRequest;
    }

    private void performAiPlayerBet(PokerPlayer pokerPlayer) {
        Bet bet = new Bet();
        bet.setBetType(getNextBetTypeForAi());
        if (bet.getBetType() == BetType.CALL) {
            double currentBetAmount = pot.getCurrentBet().getBetAmount();
            bet.setBetAmount(currentBetAmount);
        } else if (bet.getBetType() == BetType.RAISE) {
            double currentBetAmount = pot.getCurrentBet().getBetAmount();
            PlayerUser playerUser = pokerPlayer.getPlayerUser();
            PlayerBank playerBank = playerUser.getBank();
            double randomRaise = GenerateUtil.generateAiBet(currentBetAmount, playerBank.getFunds());
            bet.setBetAmount(randomRaise);
        } else if (bet.getBetType() == BetType.BET) {
            PlayerUser playerUser = pokerPlayer.getPlayerUser();
            PlayerBank playerBank = playerUser.getBank();
            double randomBet = GenerateUtil.generateAiBet(MINIMUM_BET, playerBank.getFunds());
            bet.setBetAmount(randomBet);
        }
        setBetAmount(pokerPlayer, bet);
    }

    private List<BetType> getNextBetTypesForCurrent() {
        Bet currentBet = pot.getCurrentBet();
        if (currentBet != null) {
            BetType betType = currentBet.getBetType();
            if (betType != null) {
                return betType.getNextBetTypeOptions();
            }
        }
        return BetType.getInitialBetTypeOptions();
    }

    private BetType getNextBetTypeForAi() {
        Bet currentBet = pot.getCurrentBet();
        if (currentBet != null) {
            BetType betType = currentBet.getBetType();
            if (betType != null) {
                return betType.getRandomAiNextBetType();
            }
        }
        return BetType.getInitialAiBetType();
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
        Card card = deckOfCards.get(deckCardPointer);
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

    public void callCurrentPlayer() {
        PokerPlayer currentPlayer = getCurrentPlayer();
        if (currentPlayer != null) {
            double betAmount = pot.getCurrentBet().getBetAmount();
            Bet bet = new Bet();
            bet.setBetType(BetType.CALL);
            bet.setBetAmount(betAmount);
            setBetAmount(currentPlayer, bet);
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

    private boolean isLastPokerPlayerStanding() {
        int tableSizeNotFolded = getTableSize();
        return tableSizeNotFolded == 1;
    }

    private int getTableSize() {
        int count = 0;
        for (PokerPlayer pokerPlayer : this) {
            if (!pokerPlayer.isFolded()) {
                count++;
            }
        }
        return count;
    }

    private void reset() {
        pot = new Pot();
        communityCards.clear();
        for (PokerPlayer pokerPlayer : this) {
            pokerPlayer.reset();
        }
    }

    public void setBetAmount(PokerPlayer pokerPlayer, BetType type) {
        Bet bet = new Bet();
        bet.setBetType(type);
        setBetAmount(pokerPlayer, bet);
    }

    public void setBetAmount(PokerPlayer pokerPlayer, Bet bet) {
        pot.addBet(pokerPlayer, bet);
        switch (bet.getBetType()) {
            case FOLD: {
                pokerPlayer.setFolded(true);
                callback.onPlayerFold(pokerPlayer);
                callback.onEvent(pokerPlayer.getPlayerUser().getDisplayName() + " folded");
                break;
            }
            case CHECK: {
                callback.onEvent(pokerPlayer.getPlayerUser().getDisplayName() + " checked");
                break;
            }
            case CALL: {
                callback.onEvent(pokerPlayer.getPlayerUser().getDisplayName() + " called " + bet.getBetAmount());
            }
            case BET: {
                callback.onEvent(pokerPlayer.getPlayerUser().getDisplayName() + " bet " + bet.getBetAmount());
                break;
            }
            case RAISE: {
                callback.onEvent(pokerPlayer.getPlayerUser().getDisplayName() + " raised " + bet.getBetAmount());
                break;
            }
        }
        callback.onEvent("Pot: " + pot.getPot() + " Curr: " + pot.getCurrentBet() + " CurrBetType: " + pot.getCurrentBet());
    }

    public interface PokerTableCallback {
        void onUpdatePlayersOnTable(PokerTable pokerTable);

        void onCurrentPlayerBetTurn(BetAmountRequest betAmountRequest);

        void onOtherPlayerBetTurn(PokerPlayer pokerPlayer);

        void onDealCommunityCard(Card card, CommunityCardType cardType);

        void onDealCardToPlayer(PokerPlayer pokerPlayer, Card card);

        void onPlayerTurn(PokerPlayer pokerPlayer, boolean turn);

        void onPlayerDealer(PokerPlayer pokerPlayer, boolean dealer);

        void onPlayerFold(PokerPlayer pokerPlayer);

        void onEvent(String event);
    }
}
