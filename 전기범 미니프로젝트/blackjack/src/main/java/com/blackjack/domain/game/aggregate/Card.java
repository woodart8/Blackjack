package com.blackjack.domain.game.aggregate;

import java.util.ArrayList;

public class Card {

    private final Suit suit; // 카드 문양
    private final Rank rank; // 카드 숫자

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public static int sumCardsPoint(ArrayList<Card> playerCard, boolean isDealerCardAndFirstRound) {
        int sum = 0; // 받은 카드의 점수 합
        int cntAce = 0; // 받은 카드 중 에이스 카드 수
        for(Card card : playerCard) {
            // 첫번째 턴일 경우 딜러의 첫카드는 계산 미포함
            if(isDealerCardAndFirstRound) {
                isDealerCardAndFirstRound = false;
                continue;
            }
            // 에이스: 11점, [10,J,Q,K]: 10점, 나머지: 숫자대로
            Rank rank = card.getRank();
            if(rank == Rank.ACE) {
                cntAce++;
                sum+=11;
            }
            else if(rank == Rank.TEN || rank == Rank.JACK ||
                    rank == Rank.QUEEN || rank == Rank.KING) {
                sum+=10;
            }
            else {
                sum+=rank.ordinal()+2;
            }
        }
        // 에이스를 받은적 있고 점수 합이 21보다 클 경우
        if(cntAce > 0 && sum > 21) {
            // 합계가 21이하 또는 cntAce 가 0이 될 때까지 받은 에이스를 1점으로 변환
            while(sum > 21 && cntAce > 0) {
                sum -= 10;
                cntAce--;
            }
        }
        return sum;
    }

    public static void printHorizontalCards(ArrayList<Card> cards, boolean isDealerCardAndFirstRound) {
        // 각 줄을 생성하여 합친다.
        StringBuilder line1 = new StringBuilder();
        StringBuilder line2 = new StringBuilder();
        StringBuilder line3 = new StringBuilder();
        StringBuilder line4 = new StringBuilder();
        StringBuilder line5 = new StringBuilder();
        StringBuilder line6 = new StringBuilder();
        StringBuilder line7 = new StringBuilder();

        for (Card card : cards) {
            String[] cardLines;
            if(isDealerCardAndFirstRound) {
                cardLines = card.getBackSide().split("\n");
                isDealerCardAndFirstRound = false;
            }
            else cardLines = card.getFrontSide().split("\n");
            line1.append(cardLines[0]).append(" ");
            line2.append(cardLines[1]).append(" ");
            line3.append(cardLines[2]).append(" ");
            line4.append(cardLines[3]).append(" ");
            line5.append(cardLines[4]).append(" ");
            line6.append(cardLines[5]).append(" ");
            line7.append(cardLines[6]).append(" ");
        }

        System.out.println(line1);
        System.out.println(line2);
        System.out.println(line3);
        System.out.println(line4);
        System.out.println(line5);
        System.out.println(line6);
        System.out.println(line7);
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    private String getSuitSymbol(Suit suit) {
        return switch (suit) {
            case SPADES -> "♠";
            case HEARTS -> "♥";
            case DIAMONDS -> "♦";
            case CLUBS -> "♣";
        };
    }

    private String getRankDisplay(Rank rank) {
        return switch (rank) {
            case TWO -> "2";
            case THREE -> "3";
            case FOUR -> "4";
            case FIVE -> "5";
            case SIX -> "6";
            case SEVEN -> "7";
            case EIGHT -> "8";
            case NINE -> "9";
            case TEN -> "10";
            case JACK -> "J";
            case QUEEN -> "Q";
            case KING -> "K";
            case ACE -> "A";
        };
    }

    public String getBackSide() {
        return "┌─────────┐\n" +
               "│░░░░░░░░░│\n" +
               "│░░░░░░░░░│\n" +
               "│░░░░░░░░░│\n" +
               "│░░░░░░░░░│\n" +
               "│░░░░░░░░░│\n" +
               "└─────────┘";
    }

    public String getFrontSide() {
        String suitSymbol = getSuitSymbol(suit);
        String rankDisplay = getRankDisplay(rank);
        // 카드 출력 모양
        if(!rankDisplay.equals("10")) {
            return "┌─────────┐\n" +
                   "│ " + rankDisplay + "       │\n" +
                   "│         │\n" +
                   "│    " + suitSymbol + "    │\n" +
                   "│         │\n" +
                   "│       " + rankDisplay + " │\n" +
                   "└─────────┘";
        } else {
            return "┌─────────┐\n" +
                   "│ " + rankDisplay + "      │\n" +
                   "│         │\n" +
                   "│    " + suitSymbol + "    │\n" +
                   "│         │\n" +
                   "│      " + rankDisplay + " │\n" +
                   "└─────────┘";
        }
    }

    @Override
    public String toString() {
        // 디폴트는 카드의 앞면
        return toString(false);
    }

    public String toString(boolean isFaceDown) {
        if (isFaceDown) return getBackSide();
        else return getFrontSide();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Card other)) return false;
        return this.suit == other.suit && this.rank == other.rank;
    }

    @Override
    public int hashCode() {
        return suit.hashCode() * 31 + rank.hashCode();
    }
}
