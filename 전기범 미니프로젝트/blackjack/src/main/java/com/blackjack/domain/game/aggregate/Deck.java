package com.blackjack.domain.game.aggregate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private final List<Card> cards = new ArrayList<>();

    public Deck() {
        reset();
        shuffle();
    }

    private void reset() {
        // 카드 52장을 디폴트 상태로(순서대로) 덱에 넣는다.
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card dealCard() {
        // 카드가 덱에 남아 있으면 위에서(리스트의 뒤에서부터) 1장을 뽑는다.
        if (!cards.isEmpty()) return cards.remove(cards.size() - 1);
        // 카드가 다 떨어졌는데 뽑아야 하는 경우에는 덱을 리셋하고, 셔플후 1장을 뽑는다.
        reset();
        shuffle();
        return cards.remove(cards.size() - 1);
    }

    public int cardsLeft() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    @Override
    public String toString() {
        return cards.toString();
    }
}