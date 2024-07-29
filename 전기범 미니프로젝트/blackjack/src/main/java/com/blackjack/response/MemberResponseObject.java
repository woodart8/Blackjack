package com.blackjack.response;

import com.blackjack.domain.member.aggregate.Member;

public class MemberResponseObject {

    private Member member;
    private int checkValueInt;
    private boolean checkValueBoolean;

    public MemberResponseObject() {
    }

    public MemberResponseObject(Member member) {
        this.member = member;
    }

    public MemberResponseObject(Member member, int checkValueInt) {
        this.member = member;
        this.checkValueInt = checkValueInt;
    }

    public MemberResponseObject(Member member, boolean checkValueBoolean) {
        this.member = member;
        this.checkValueBoolean = checkValueBoolean;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public int getCheckValueInt() {
        return checkValueInt;
    }

    public void setCheckValueInt(int checkValueInt) {
        this.checkValueInt = checkValueInt;
    }

    public boolean getCheckValueBoolean() {
        return checkValueBoolean;
    }

    public void setCheckValueBoolean(boolean checkValueBoolean) {
        this.checkValueBoolean = checkValueBoolean;
    }
}
