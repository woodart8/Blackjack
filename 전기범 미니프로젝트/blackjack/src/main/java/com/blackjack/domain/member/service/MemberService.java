package com.blackjack.domain.member.service;

import com.blackjack.request.LoginForm;
import com.blackjack.domain.member.aggregate.Member;
import com.blackjack.response.MemberResponseObject;
import com.blackjack.domain.member.repository.MemberRepository;

public class MemberService {

    private final MemberRepository memberRepository = new MemberRepository();

    public MemberResponseObject findMemberByLoginForm(LoginForm loginForm) {
        // 아이디로 회원 조회
        Member member = memberRepository.selectMemberById(loginForm.getMemId());
        // 일치하는 아이디가 없는 경우
        if(member == null) return new MemberResponseObject(null,-1);
        // 일치하는 아이디가 있는 경우 비밀번호를 비교
        if (member.getPwd().equals(loginForm.getMemPwd()))
            return new MemberResponseObject(member,1);
        else return new MemberResponseObject(null,0);
    }

    public MemberResponseObject findMemberByMemNo(int memNo) {
        // 회원번호로 회원 조회
        Member member = memberRepository.selectMemberByMemNo(memNo);
        if(member != null)
            return new MemberResponseObject(member,true);
        else return new MemberResponseObject(null,false);
    }

    public MemberResponseObject findMemberById(String memId) {
        // 아이디로 회원 조회
        Member member = memberRepository.selectMemberById(memId);
        if(member == null) return new MemberResponseObject(null,false);
        else return new MemberResponseObject(null,true);
    }

    public MemberResponseObject findMemberByNickname(String nickname) {
        // 닉네임으로 회원 조회
        Member member = memberRepository.selectMemberByNickname(nickname);
        if(member != null)
            return new MemberResponseObject(member,true);
        else return new MemberResponseObject(null,false);
    }

    public MemberResponseObject registMember(Member member) {
        return new MemberResponseObject(null, memberRepository.insertMember(member));
    }

    public MemberResponseObject removeMember(Member member) {
        // 비정상 요청 처리
        if(member == null) return new MemberResponseObject(null,false);
        // 정상 요청 처리
        if(memberRepository.deleteMember(member))
            return new MemberResponseObject(null,true);
        else return new MemberResponseObject(null,false);
    }

    public MemberResponseObject modifyMember(Member member) {
        // 비정상 요청 처리
        if(member == null) return new MemberResponseObject(null,false);
        // 정상 요청 처리
        if(memberRepository.updateMember(member))
            return new MemberResponseObject(member,true);
        else return new MemberResponseObject(null,false);
    }
}
