package com.blackjack.domain.member.repository;

import com.blackjack.domain.member.aggregate.Member;
import com.blackjack.stream.MyObjectOutput;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class MemberRepository {

    private final ArrayList<Member> memberList = new ArrayList<>();
    private final String filePath = "전기범 미니프로젝트/blackjack/src/main/java/com/blackjack/domain/member/db/memberDB.dat";
    private final File file;

    public MemberRepository() {
        file = new File(filePath);
        // 파일이 없는 경우
        if(!file.exists()) {
            // 파일 생성 및 헤더 추가
            saveMembers(file, memberList);
        }
        loadMember(file);
    }

    private void saveMembers(File file, ArrayList<Member> members) {
        // 파일을 덮어 씌워 저장
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(file)
                    )
            );

            for(Member member: members) {
                oos.writeObject(member);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if(oos != null) oos.close();
            } catch (IOException e) {
                System.out.println("oos close failure");
            }
        }
    }

    private void loadMember(File file) {
        // 파일을 읽어 ArrayList에 저장
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(
                    new BufferedInputStream(
                            new FileInputStream(file)
                    )
            );

            while(true) {
                memberList.add((Member)ois.readObject());
            }
        } catch (EOFException e) {
            System.out.print("");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if(ois != null) ois.close();
            } catch (IOException e) {
                System.out.println("ois close failure");
            }
        }
    }

    private int selectLastMemberNo() {
        // 마지막 회원의 회원번호를 리턴(회원이 존재하지 않는 경우 0)
        if(memberList.isEmpty()) return 0;
        return memberList.get(memberList.size() - 1).getMemNo();
    }

    public boolean insertMember(Member member) {
        // 파일에 이어붙여 저장
        MyObjectOutput moo = null;
        boolean result = false;
        try {
            moo = new MyObjectOutput(
                    new BufferedOutputStream(
                            new FileOutputStream(filePath, true)
                    )
            );
            member.setMemNo(selectLastMemberNo()+1);
//            System.out.println(member);
            moo.writeObject(member);

            memberList.add(member);

            result = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if(moo != null) moo.close();
            } catch (IOException e) {
                System.out.println("moo close failure");
            }
        }
        return result;
    }

    public Member selectMemberByMemNo(int memNo) {
        // 오름차순으로 정렬되어 있으므로 이분탐색
        int index = Collections.binarySearch(memberList
                        , new Member(memNo, "", "", "", "", 0));
        // 회원을 찾은 경우
        if(index >= 0) return memberList.get(index);
        // 회원을 찾지 못한 경우
        return null;
    }

    public Member selectMemberById(String memId) {
        for(Member member : memberList) {
            if(member.getId().equals(memId)) return new Member(member); // 깊은 복사
        }
        return null;
    }

    public Member selectMemberByNickname(String nickname) {
        for(Member member : memberList) {
            if(member.getNickname().equals(nickname)) return new Member(member); // 깊은 복사
        }
        return null;
    }

    public boolean updateMember(Member member) {
        int index = Collections.binarySearch(memberList
                , new Member(member.getMemNo(), "", "", "", "", 0));
        // 회원을 찾은 경우
        if(index >= 0) {
            memberList.set(index, new Member(member));
            saveMembers(file, memberList);
            return true;
        }
        // 회원을 찾지 못한 경우
        return false;
    }

    public boolean deleteMember(Member member) {
        int index = Collections.binarySearch(memberList
                , new Member(member.getMemNo(), "", "", "", "", 0));
        // 회원을 찾은 경우
        if(index >= 0) {
            memberList.remove(index);
            saveMembers(file, memberList);
            return true;
        }
        // 회원을 찾지 못한 경우
        return false;
    }

}
