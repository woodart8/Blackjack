package com.blackjack.domain.game.aggregate;

import java.util.ArrayList;

public class Dealer {
    private final ArrayList<Card> dealerCard = new ArrayList<>();

    public void initDealerCard(Deck deck) {
        // 게임 시작 시 2장을 드로우한다.
        dealerCard.clear();
        dealerCard.add(deck.dealCard());
        dealerCard.add(deck.dealCard());
    }

    public ArrayList<Card> getDealerCard() {
        return dealerCard;
    }
}
