package com.blackjack.domain.game.aggregate;

import com.blackjack.domain.member.aggregate.Member;

import java.io.Serializable;
import java.util.ArrayList;

public class Player extends Member implements Serializable {
    private transient final ArrayList<Card> playerCard = new ArrayList<>();
    private transient final ArrayList<Card> splitCard = new ArrayList<>();
    private transient boolean blackjack = false; // 블랙잭 여부
    private transient boolean splitBlackjack = false; // 스플릿 블랙잭 여부
    private transient boolean insurance = false; // 인슈어런스 여부
    private transient boolean stand = false; // 스탠드 여부
    private transient boolean splitStand = false; // 스플릿 스탠드 여부
    private transient boolean bust = false; // 버스트 여부
    private transient boolean splitBust = false; // 스플릿 버스트 여부
    private transient boolean doubleDown = false; // 더블다운 여부
    private transient boolean splitDoubleDown = false; // 스플릿 더블다운 여부
    private transient boolean surrender = false; // 서렌더 여부
    private transient boolean splitSurrender = false; // 스플릿 서렌드 여부
    private transient boolean hit = false; // 힛 여부
    private transient boolean splitHit = false; // 스플릿 힛 여부
    private transient boolean split = false; // 스플릿 여부

    public Player(Member member) {
        super(member);
    }

    public void initPlayerCard(Deck deck) {
        // 게임 시작 시 2장을 드로우한다.
        playerCard.clear();
        splitCard.clear();
        playerCard.add(deck.dealCard());
        playerCard.add(deck.dealCard());
    }

    public void initPlayerStatus() {
        // 플레이어 스테이터스 초기화
        this.setHit(false);
        this.setSplitHit(false);
        this.setInsurance(false);
        this.setBlackjack(false);
        this.setSplitBlackjack(false);
        this.setStand(false);
        this.setSplitStand(false);
        this.setBust(false);
        this.setSplitBust(false);
        this.setDoubleDown(false);
        this.setSplitDoubleDown(false);
        this.setSurrender(false);
        this.setSplitSurrender(false);
        this.setSplit(false);
    }

    public ArrayList<Card> getPlayerCard() {
        return playerCard;
    }

    public ArrayList<Card> getSplitCard() {
        return splitCard;
    }

    public boolean isSplit() {
        return split;
    }

    public void setSplit(boolean split) {
        this.split = split;
    }

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public boolean isInsurance() {
        return insurance;
    }

    public void setInsurance(boolean insurance) {
        this.insurance = insurance;
    }

    public boolean isBlackjack() {
        return blackjack;
    }

    public void setBlackjack(boolean blackjack) {
        this.blackjack = blackjack;
    }

    public boolean isSplitBlackjack() {
        return splitBlackjack;
    }

    public void setSplitBlackjack(boolean splitBlackjack) {
        this.splitBlackjack = splitBlackjack;
    }

    public boolean isStand() {
        return stand;
    }

    public void setStand(boolean stand) {
        this.stand = stand;
    }

    public boolean isBust() {
        return bust;
    }

    public void setBust(boolean bust) {
        this.bust = bust;
    }

    public boolean isDoubleDown() {
        return doubleDown;
    }

    public void setDoubleDown(boolean doubleDown) {
        this.doubleDown = doubleDown;
    }

    public boolean isSurrender() {
        return surrender;
    }

    public void setSurrender(boolean surrender) {
        this.surrender = surrender;
    }

    public boolean isSplitStand() {
        return splitStand;
    }

    public void setSplitStand(boolean splitStand) {
        this.splitStand = splitStand;
    }

    public boolean isSplitBust() {
        return splitBust;
    }

    public void setSplitBust(boolean splitBust) {
        this.splitBust = splitBust;
    }

    public boolean isSplitDoubleDown() {
        return splitDoubleDown;
    }

    public void setSplitDoubleDown(boolean splitDoubleDown) {
        this.splitDoubleDown = splitDoubleDown;
    }

    public boolean isSplitSurrender() {
        return splitSurrender;
    }

    public void setSplitSurrender(boolean splitSurrender) {
        this.splitSurrender = splitSurrender;
    }

    public boolean isSplitHit() {
        return splitHit;
    }

    public void setSplitHit(boolean splitHit) {
        this.splitHit = splitHit;
    }
}
