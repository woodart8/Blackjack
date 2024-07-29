package com.blackjack.domain.game.aggregate;

import com.blackjack.domain.member.aggregate.Member;

import java.io.Serializable;
import java.util.Objects;

public class Game implements Serializable {

    private int gameNo; // 게임번호
    private Player player;// 플레이어
    private int result = 0; // 손익

    private final transient Dealer dealer = new Dealer(); // 딜러
    private final transient Deck deck = new Deck(); // 카드덱
    private transient int betLimit = 10; // 최대 베팅 한도
    private transient int bet = 0; // 현재 베팅 금액
    private transient int insuranceBet = 0; // 인슈어런스 베팅 금액
    private transient boolean evenMoney = false; // 이븐 머니 여부

    public Game(Player player) {
        this.player = player;
        // 입장한 플레이어의 티어에 따라 베팅 한도 설정
        switch (player.getTier()) {
            case BRONZE: this.betLimit = 10; break;
            case SILVER: this.betLimit = 20; break;
            case GOLD: this.betLimit = 40; break;
            case PLATINUM: this.betLimit = 80; break;
            case EMERALD: this.betLimit = 200; break;
            case DIAMOND: this.betLimit = 500; break;
            case MASTER: this.betLimit = 1500; break;
            case GRANDMASTER: this.betLimit = 5000; break;
            case SUPER_GRANDMASTER: this.betLimit = 20000; break;
        }
    }

    public void bet(int dollars) {
        player.setDollars(player.getDollars() - dollars); // 베팅 금액만큼 플레이어의 돈에서 차감
        this.bet += dollars; // 현재 베팅 금액에 반영
    }

    public void insurance(boolean checkDealerBlackjack) {
        // 인슈어런스 베팅을 한 적이 있는 경우
        if (insuranceBet > 0) {
            // 딜러가 블랙잭일 경우
            if(checkDealerBlackjack) {
                player.setDollars(player.getDollars() + 2 * insuranceBet); // 인슈어런스 베팅 금액을 2배로 돌려받음
                this.result += insuranceBet;
            }
            // 딜러가 블랙잭이 아닐 경우
            else this.result -= insuranceBet;
            insuranceBet = 0; // 인슈어런스 베팅 초기화
        }
    }

    public void evenMoney() {
        playerWin(); // 이븐 머니인 경우 베팅 금액의 200%를 지급
        this.evenMoney = false;  // 이븐 머니 초기화
    }

    public void blackjack() {
        // 플레이어가 블랙잭으로 승리한 경우 베팅 금액의 250% 지급
        player.setDollars(player.getDollars() + this.bet/2); // 베팅 금액의 50% 지급
        this.result += (this.bet/2);
        playerWin(); // 베팅 금액의 200% 지급
    }

    public void surrender() {
        // 플레이어가 항복한 경우
        player.setDollars(player.getDollars() + this.bet/2); // 현재 베팅 금액의 50%를 돌려받는다.
        this.result += (this.bet/2);
        dealerWin();
    }

    public void playerWin() {
        player.setDollars(player.getDollars() + 2*this.bet); // 베팅 금액의 200% 지급
        this.result += this.bet;
        this.bet = 0; // 베팅 금액 초기화
    }

    public void dealerWin() {
        this.result -= this.bet;
        this.bet = 0; // 베팅 금액 초기화
    }

    public void push() {
        // 비겼을 경우
        player.setDollars(player.getDollars() + this.bet); // 베팅 금액 반환
        this.bet = 0; // 베팅 금액 초기화
    }

    public void placeInsurance() {
        // 베팅 이후 인슈어런스를 한 적이 없는 경우
        if (this.bet > 0 && this.insuranceBet == 0) {
            this.insuranceBet = bet / 2; // 인슈어런스는 베팅 금액의 절반
            player.setDollars(player.getDollars() - this.insuranceBet); // 인슈어런스 베팅 금액만큼 플레이어의 돈에서 차감
        } else {
            System.out.println("\n인슈어런스를 할 수 없습니다.");
        }
    }

    public boolean isBlackjack(Card Card1, Card Card2) {
        // 블랙잭인 경우 true 아닌 경우 false 리턴
        return  (Card1.getRank() == Rank.ACE &&
                    (Card2.getRank() == Rank.TEN || Card2.getRank() == Rank.JACK ||
                     Card2.getRank() == Rank.QUEEN || Card2.getRank() == Rank.KING)) ||
                (Card2.getRank() == Rank.ACE &&
                     (Card1.getRank() == Rank.TEN || Card1.getRank() == Rank.JACK ||
                      Card1.getRank() == Rank.QUEEN || Card1.getRank() == Rank.KING));
    }

    public Dealer getDealer() {
        return dealer;
    }

    public int getGameNo() {
        return gameNo;
    }

    public void setGameNo(int gameNo) {
        this.gameNo = gameNo;
    }

    public Deck getDeck() {
        return deck;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getBetLimit() {
        return betLimit;
    }

    public void setBetLimit(int betLimit) {
        this.betLimit = betLimit;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public int getInsuranceBet() {
        return insuranceBet;
    }

    public void setInsuranceBet(int insuranceBet) {
        this.insuranceBet = insuranceBet;
    }

    public boolean getEvenMoney() {
        return evenMoney;
    }

    public void setEvenMoney(boolean evenMoney) {
        this.evenMoney = evenMoney;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "게임 [ 티어: " + player.getTier() +
               ", 닉네임: " + player.getNickname() +
               ", 손익: " + result + "달러($) "+
               ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return gameNo == game.gameNo;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(gameNo);
    }
}
