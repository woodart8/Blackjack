package com.blackjack.run;

import com.blackjack.domain.game.aggregate.*;
import com.blackjack.response.GameResponseObject;
import com.blackjack.domain.game.service.GameService;
import com.blackjack.request.LoginForm;
import com.blackjack.domain.member.aggregate.Member;
import com.blackjack.response.MemberResponseObject;
import com.blackjack.domain.member.aggregate.Tier;
import com.blackjack.domain.member.service.MemberService;

import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.System.exit;

public class Application {

    private static final MemberService memberService = new MemberService();
    private static final GameService gameService = new GameService();
    private static int memNo;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while(true) {
            System.out.println("\n====== Black Jack ======");
            System.out.println("1. 로그인");
            System.out.println("2. 회원가입");
            System.out.println("9. 게임 종료");
            System.out.println("==========================");
            System.out.println("번호를 입력해주세요: ");

            String line = sc.nextLine();
            try {
                int input = Integer.parseInt(line);
                switch (input) {
                    case 1: //로그인
                        MemberResponseObject checkLoginSuccess = memberService.findMemberByLoginForm(memberLogin());
                        if (checkLoginSuccess.getCheckValueInt() > 0) {
                            System.out.println("\n로그인 성공!");
                            memNo = checkLoginSuccess.getMember().getMemNo();
                            goLounge();
                            break;
                        }
                        else if (checkLoginSuccess.getCheckValueInt() == 0)
                            System.out.println("\n비밀번호가 잘못되었습니다.");
                        else System.out.println("\n존재하지 않는 아이디입니다.");
                        break;
                    case 2: // 회원가입
                        MemberResponseObject checkSignUpSuccess = memberService.registMember(signUp());
                        if (checkSignUpSuccess.getCheckValueBoolean()) {
                            System.out.println("\n회원가입 성공!");
                        }
                        else System.out.println("\n회원가입에 실패했습니다.");
                        break;
                    case 9: // 게임 종료
                        System.out.println("\n게임을 종료합니다.");
                        return;
                    default: System.out.println("\n잘못된 입력입니다.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\n잘못된 입력입니다.");
            }
        }
    }

    private static void goLounge() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n====== 라운지 ======");
            System.out.println("1. 게임 시작");
            System.out.println("2. 내 정보 보기");
            System.out.println("3. 회원 검색");
            System.out.println("4. 무료 충전");
            System.out.println("8. 로그아웃");
            System.out.println("9. 게임 종료");
            System.out.println("0. 회원 탈퇴");
            System.out.println("======================");
            System.out.println("번호를 입력해주세요: ");
            String line = sc.nextLine();
            if(line.length() > 1) {
                System.out.println("\n잘못된 입력입니다.");
                continue;
            }
            try {
                int input = Integer.parseInt(line);
                switch (input) {
                    case 1: // 게임 시작
                        MemberResponseObject checkValidPlayer = memberService.findMemberByMemNo(memNo);
                        if(checkValidPlayer.getCheckValueBoolean()) {
                            System.out.println("\n게임을 시작합니다.");
                            try {
                                playGame(checkValidPlayer.getMember());
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        else System.out.println("\n플레이어 정보를 불러오지 못했습니다.");
                        break;
                    case 2: // 내 정보 보기
                        MemberResponseObject checkMyInfoSuccess = memberService.findMemberByMemNo(memNo);
                        GameResponseObject checkMyGameSuccess = gameService.findGamesByMemNo(memNo);
                        if (checkMyInfoSuccess.getCheckValueBoolean()) {
                            printMemInfo(checkMyInfoSuccess.getMember());
                            printGameListInfo(checkMyGameSuccess.getGameList());
                            selectMyInfoMenuNo(checkMyInfoSuccess.getMember(),checkMyGameSuccess.getGameList());
                        }
                        else System.out.println("\n내 정보를 불러오지 못했습니다.");
                        break;
                    case 3: // 회원 검색
                        String nickname = searchNickname();
                        MemberResponseObject checkMemInfoSuccess = memberService.findMemberByNickname(nickname);
                        if (checkMemInfoSuccess.getCheckValueBoolean()) {
                            GameResponseObject checkMemGameSuccess =
                                    gameService.findGamesByMemNo(checkMemInfoSuccess.getMember().getMemNo());
                            printMemInfo(checkMemInfoSuccess.getMember());
                            printGameListInfo(checkMemGameSuccess.getGameList());
                        }
                        else System.out.println("\n존재하지 않는 닉네임입니다.");
                        break;
                    case 4: // 무료 충전
                        Member member = memberService.findMemberByMemNo(memNo).getMember();
                        if(member.getDollars() < 100) {
                            member.setDollars(100);
                            memberService.modifyMember(member);
                            System.out.println("\n충전이 완료되었습니다!");
                        }
                        else System.out.println("\n금액이 이미 충분합니다.");
                        break;
                    case 8: // 로그아웃
                        System.out.println("\n로그아웃 완료");
                        return;
                    case 9: // 게임 종료
                        System.out.println("\n게임을 종료합니다.");
                        exit(0);
                    case 0: // 회원 탈퇴
                        MemberResponseObject checkMemDelSuccess = memberService.removeMember(delAccount());
                        if (checkMemDelSuccess.getCheckValueBoolean()) {
                            gameService.removeGame(memNo);
                            System.out.println("\n회원 탈퇴 완료");
                            return;
                        } else {
                            System.out.println("\n회원 탈퇴 실패");
                            break;
                        }
                    default: System.out.println("\n잘못된 입력입니다.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\n잘못된 입력입니다.");
            }
        }
    }

    private static void playGame(Member member) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        updateTier(member);
        Player player = new Player(member);
        Game game = new Game(player);
        Dealer dealer = game.getDealer();
        int betLimit = game.getBetLimit();
        boolean checkPlayAtLeastOnce = false;
        while(true) {
            printGameStatus(player, betLimit, game);
            // 베팅 금액 입력
            while(true) {
                System.out.println("배팅할 금액을 정하세요(방 나가기(Q)): ");
                String line = sc.nextLine();
                if (line.equalsIgnoreCase("Q"))  {
                    if(checkPlayAtLeastOnce) gameService.saveGame(game);
                    updateTier(player);
                    printFinalResult(game);
                    return;
                }
                try {
                    int input = Integer.parseInt(line);
                    if (input > player.getDollars())
                        System.out.println("\n배팅 금액이 가지고 있는 돈보다 많습니다.\n");
                    else if(input > betLimit)
                        System.out.println("\n이 방의 베팅 최대치는 $" + betLimit + "입니다.\n");
                    else if(input < 2)
                        System.out.println("\n최소 2달러($)이상 베팅해야합니다.\n");
                    else {
                        game.bet(input);
                        memberService.modifyMember(player);
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("\n잘못된 입력입니다.\n");
                }
            }

            Thread.sleep(500);

            game.getPlayer().initPlayerCard(game.getDeck()); // 플레이어 카드 초기 세팅
            game.getDealer().initDealerCard(game.getDeck()); // 딜러 카드 초기 세팅
            printGameStatus(player, betLimit, game);
            printBothCards(dealer.getDealerCard(), player.getPlayerCard(),true);

            // 플레이어 블랙잭 여부 검사
            player.setBlackjack(game.isBlackjack(player.getPlayerCard().get(0),player.getPlayerCard().get(1)));

            boolean skipFlag = false; // 범위 스킵 여부를 저장하는 변수

            // 플레이어 블랙잭일 경우
            if(player.isBlackjack()) System.out.println("\n플레이어 블랙잭! 축하드립니다~\n");

            // 딜러의 두번째 카드가 에이스인 경우 인슈어런스 및 이븐 머니 선택 제공
            if ((dealer.getDealerCard().get(1).getRank() == Rank.ACE) && (player.getDollars() >= game.getBet()/2)) {
                // 플레이어 블랙잭일 경우 이븐 머니 여부 입력, 아닐 경우 인슈어런스 여부 입력
                if(!player.isBlackjack()) {
                    while(true) {
                        System.out.println("인슈어런스? (Y/N)");
                        // 입력을 통해 인슈어런스 결정
                        String decisionInsurance = sc.nextLine();
                        if (decisionInsurance.equalsIgnoreCase("Y")) {
                            game.placeInsurance();
                            memberService.modifyMember(player);
                            player.setInsurance(true);
                            break;
                        } else if (decisionInsurance.equalsIgnoreCase("N")) {
                            break;
                        }
                        else System.out.println("\n잘못된 입력입니다.\n");
                    }
                } else {
                    while(true) {
                        System.out.println("이븐 머니? (Y/N)");
                        String decisionEven = sc.nextLine();
                        if (decisionEven.equalsIgnoreCase("Y")) {
                            game.setEvenMoney(true);
                            game.evenMoney();
                            skipFlag = true;
                            System.out.println("\n이븐 머니");
                            System.out.println("플레이어 Win");
                            break;
                        } else if (decisionEven.equalsIgnoreCase("N")) {
                            break;
                        }
                        else System.out.println("\n잘못된 입력입니다.\n");
                    }
                }
            }

            // 이븐 머니일 경우 스킵
            if(!skipFlag) {
                // 딜러 블랙잭 확인
                if (game.isBlackjack(dealer.getDealerCard().get(0),dealer.getDealerCard().get(1))) {
                    Thread.sleep(500);
                    printGameStatus(player, betLimit, game);
                    printBothCards(dealer.getDealerCard(), player.getPlayerCard(), false);
                    System.out.println("\n딜러 블랙잭!");
                    // 플레이어가 인슈어런스 베팅을 했을 경우
                    if(player.isInsurance()) game.insurance(true); // 인슈어런스 처리
                    memberService.modifyMember(player);

                    // 플레이어 블랙잭이면 푸시, 아니면 딜러 승리
                    if (player.isBlackjack()) {
                        game.push();
                        System.out.println("\n푸시");
                    } else {
                        game.dealerWin();
                        System.out.println("딜러 Win");
                    }
                    memberService.modifyMember(player);
                } else {
                    if (player.isInsurance()) {
                        System.out.println("\nNO 블랙잭");
                        game.insurance(false); // 인슈어런스 처리
                        memberService.modifyMember(player);
                    }

                    skipFlag = true;
                    if (!player.isBlackjack()) {
                        while (true) {
                            // 첫 바퀴에만 스킵
                            if (!skipFlag) {
                                Thread.sleep(500);
                                printGameStatus(player, betLimit, game);
                                printBothCards(dealer.getDealerCard(), player.getPlayerCard(), true);
                            }
                            // 카드 점수 합이 21 초과이면 버스트
                            if (Card.sumCardsPoint(player.getPlayerCard(), false) > 21) {
                                player.setBust(true);
                                game.dealerWin();
                                memberService.modifyMember(player);
                                System.out.println("\n플레이어 버스트");
                                System.out.println("딜러 Win");
                                break;
                            }

                            // 더블다운을 한 경우 여기서 바로 break
                            if(player.isDoubleDown()) break;

                            System.out.println("\n힛:1, 스탠드:2, 더블다운:3, 서렌더:4");
                            String line = sc.nextLine();
                            try {
                                int input = Integer.parseInt(line);
                                switch (input) {
                                    case 1: // 힛(한장 더 받기)
                                        player.getPlayerCard().add(game.getDeck().dealCard());
                                        player.setHit(true);
                                        break;
                                    case 2: // 스탠드(그만 받기)
                                        player.setStand(true);
                                        break;
                                    case 3: // 더블다운(처음 2장일 때 베팅을 2배로 올리고, 한장만 더 받기)
                                        if(player.getDollars() < game.getBet()) {
                                            System.out.println("\n베팅 금액이 모자랍니다.");
                                            break;
                                        }
                                        if(!player.isHit()) {
                                            game.bet(game.getBet());
                                            memberService.modifyMember(player);
                                            player.getPlayerCard().add(game.getDeck().dealCard());
                                            player.setDoubleDown(true);
                                        }
                                        else System.out.println("\n이미 힛을 하여 더블다운이 불가합니다.");
                                        break;
                                    case 4: // 서렌더(항복하고 베팅 금액의 절반만 챙기기)
                                        game.surrender();
                                        memberService.modifyMember(player);
                                        player.setSurrender(true);
                                        System.out.println("\n플레이어 서렌더");
                                        System.out.println("딜러 Win");
                                        break;
                                    default: System.out.println("\n잘못된 입력입니다.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("\n잘못된 입력입니다.");
                            }
                            if (player.isStand() || player.isSurrender()) break;
                            skipFlag = false;
                        }
                    }

                    // 플레이어 버스트가 아니고, 서렌더도 아닌 경우)
                    if (!player.isBust() && !player.isSurrender()) {
                        boolean delayFlag = false; // 출력 딜레이 여부를 저장하는 변수
                        int playerPoints = Card.sumCardsPoint(player.getPlayerCard(), false);
                        while (true) {
                            if (delayFlag) Thread.sleep(1500);
                            else Thread.sleep(500);
                            printGameStatus(player, betLimit, game);
                            printBothCards(dealer.getDealerCard(), player.getPlayerCard(), false);
                            int dealerPoints = Card.sumCardsPoint(dealer.getDealerCard(), false);
                            // 카드 점수 합이 21 초과이면 버스트
                            if (dealerPoints > 21) {
                                if (player.isBlackjack()) game.blackjack();
                                else game.playerWin();
                                memberService.modifyMember(player);
                                System.out.println("\n딜러 버스트");
                                System.out.println("플레이어 Win");
                                break;
                            }
                            // 딜러 카드 접수 합이 17 미만이면 딜러는 무조건 더 받아야한다.
                            if (dealerPoints >= 17) {
                                if (dealerPoints > playerPoints) {
                                    game.dealerWin();
                                    memberService.modifyMember(player);
                                    System.out.println("\n딜러 Win");
                                    break;
                                } else if (dealerPoints == playerPoints) {
                                    game.push();
                                    memberService.modifyMember(player);
                                    System.out.println("\n푸시");
                                    break;
                                }
                                // 딜러 카드 점수 합이 17 이상이지만 플레이어 카드 점수보다 낮은 경우 무조건 더 받는다.
                                else dealer.getDealerCard().add(game.getDeck().dealCard());
                            }
                            else dealer.getDealerCard().add(game.getDeck().dealCard());

                            if (!delayFlag) delayFlag = true;
                        }
                    }
                }
            }

            Thread.sleep(500);
            printGameStatus(player, betLimit, game);
            // 플레이어의 잔고가 0일 경우 플레이어는 강제 퇴장된다.
            if(player.getDollars() == 0) {
                gameService.saveGame(game);
                updateTier(player);
                printFinalResult(game);
                return;
            }

            player.initPlayerStatus(); // 플레이어 스테이터스 초기화

            // 다시하기 여부 입력
            while(true) {
                System.out.println("\n다시하시겠습니까? (Y/N)");
                String decisionOneMoreGame = sc.nextLine();
                // 그만하는 경우 게임 전적을 저장
                if (decisionOneMoreGame.equalsIgnoreCase("N")) {
                    gameService.saveGame(game);
                    updateTier(player);
                    printFinalResult(game);
                    return;
                }
                else if (decisionOneMoreGame.equalsIgnoreCase("Y")) {
                    checkPlayAtLeastOnce = true;
                    break;
                }
                else System.out.println("\n잘못된 입력입니다.");
            }
        }
    }

    private static void selectMyInfoMenuNo(Member member, ArrayList<Game> gameList) {
        Scanner sc = new Scanner(System.in);
        while(true) {
            System.out.println("\n1. 정보 수정");
            System.out.println("9. 뒤로 가기");
            System.out.println("번호를 입력하세요: ");
            String line = sc.nextLine();
            if(line.length() > 1) {
                System.out.println("\n잘못된 입력입니다.");
                continue;
            }
            try {
                int input = Integer.parseInt(line);
                switch (input) {
                    case 1:
                        MemberResponseObject checkModifySuccess = memberService.modifyMember(changeMyInfo(member));
                        if (!checkModifySuccess.getCheckValueBoolean()) System.out.println("\n정보 수정 실패");
                        else System.out.println("\n정보 수정 완료");
                        printMemInfo(checkModifySuccess.getMember());
                        printGameListInfo(gameList);
                        break;
                    case 9:
                        return;
                    default: System.out.println("\n잘못된 입력입니다.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\n잘못된 입력입니다.");
            }
        }
    }

    private static Member changeMyInfo(Member member) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n====== 내 정보 수정 ======");
            System.out.println("1. 닉네임 변경");
            System.out.println("2. 비밀번호 변경");
            System.out.println("9. 저장하기");
            System.out.println("========================");
            System.out.println("번호를 입력하세요: ");
            String line = sc.nextLine();
            if(line.length() > 1) {
                System.out.println("\n잘못된 입력입니다.");
                continue;
            }
            try {
                int input = Integer.parseInt(line);
                switch (input) {
                    case 1:
                        System.out.println("새 닉네임: ");
                        String newNickname = sc.nextLine();
                        MemberResponseObject checkValidNickname = memberService.findMemberByNickname(newNickname);
                        if(checkValidNickname.getCheckValueBoolean()) System.out.println("\n중복된 닉네임입니다.");
                        else {
                            System.out.println("\n사용 가능한 닉네임입니다.");
                            System.out.println("사용하시겠습니까?(Y/N): ");
                            String checkUseThisNickname = sc.nextLine();
                            if(checkUseThisNickname.equalsIgnoreCase("Y")) member.setNickname(newNickname);
                            else if(checkUseThisNickname.equalsIgnoreCase("N")) break;
                            else System.out.println("\n잘못된 입력입니다.");
                        }
                        break;
                    case 2:
                        System.out.println("새 비밀번호: ");
                        String newPwd = sc.nextLine();
                        System.out.println("새 비밀번호 확인");
                        String newPwdConfirm = sc.nextLine();
                        if(newPwd.equals(member.getPwd())) {
                            System.out.println("\n기존 비밀번호와 동일합니다.");
                            break;
                        }
                        if(!newPwd.equals(newPwdConfirm)) System.out.println("\n비밀번호가 서로 일치하지 않습니다.");
                        else member.setPwd(newPwd);
                        break;
                    case 9:
                        System.out.println("\n저장 완료");
                        return member;
                    default: System.out.println("\n잘못된 입력입니다.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\n잘못된 입력입니다.");
            }
        }
    }

    private static Member delAccount() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n비밀번호 입력: ");
        String memPwd = sc.nextLine();
        System.out.println("[계정 삭제]를 입력하세요: ");
        String checkConfirm = sc.nextLine();
        if(checkConfirm.equals("계정 삭제")) {
            MemberResponseObject checkPwdValid = memberService.findMemberByMemNo(memNo);
            if(memPwd.equals(checkPwdValid.getMember().getPwd())) return checkPwdValid.getMember();
            else {
                System.out.println("\n비밀번호가 다릅니다.");
                return null;
            }
        }
        System.out.println("\n문장을 잘못 입력하셨습니다.");
        return null;
    }

    private static String searchNickname() {
        Scanner sc = new Scanner(System.in);
        System.out.println("검색할 회원의 닉네임을 입력하세요: ");
        return sc.nextLine();
    }

    private static Member signUp() {
        Scanner sc = new Scanner(System.in);
        boolean flag = true;

        String memId;
        do {
            System.out.println("\n아이디: ");
            memId = sc.nextLine();
            MemberResponseObject checkValidId = memberService.findMemberById(memId);
            if (!checkValidId.getCheckValueBoolean()) flag = false;
            else System.out.println("\n중복된 아이디입니다.");
        } while(flag);

        String memPwd;
        String memPwdConfirm;
        flag = true;
        do {
            System.out.println("비밀번호: ");
            memPwd = sc.nextLine();
            System.out.println("비밀번호 확인: ");
            memPwdConfirm = sc.nextLine();
            if(memPwd.equals(memPwdConfirm)) flag = false;
            else System.out.println("\n비밀번호가 서로 일치하지 않습니다.\n");
        } while(flag);

        System.out.println("이름: ");
        String memName = sc.nextLine();

        String memNickname;
        flag = true;
        do {
            System.out.println("닉네임: ");
            memNickname = sc.nextLine();
            MemberResponseObject checkValidNickname = memberService.findMemberByNickname(memNickname);
            if(!checkValidNickname.getCheckValueBoolean()) flag = false;
            else System.out.println("\n중복된 닉네임입니다.\n");
        } while(flag);

        flag = true;
        String line;
        int memAge = 0;
        do {
            System.out.println("나이: ");
            line = sc.nextLine();
            try {
                memAge = Integer.parseInt(line);
                if(memAge < 1 || memAge > 130) {
                    System.out.println("\n잘못된 입력입니다.\n");
                    continue;
                }
                flag = false;
            } catch (NumberFormatException e) {
                System.out.println("\n잘못된 입력입니다.\n");
            }
        } while(flag);
        return new Member(memId, memPwd, memName, memNickname, memAge);
    }

    private static LoginForm memberLogin() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n====== 로그인 ======");
        System.out.println("아이디: ");
        String memId = sc.nextLine();
        System.out.println("비밀번호: ");
        String memPwd = sc.nextLine();
        return new LoginForm(memId, memPwd);
    }

    private static void updateTier(Member member) {
        int dollars = member.getDollars();
        if(dollars >= 200000) member.setTier(Tier.SUPER_GRANDMASTER);
        if(dollars >= 50000) member.setTier(Tier.GRANDMASTER);
        if(dollars >= 15000) member.setTier(Tier.MASTER);
        if(dollars >= 5000) member.setTier(Tier.DIAMOND);
        if(dollars >= 2000) member.setTier(Tier.EMERALD);
        else if(dollars >= 800) member.setTier(Tier.PLATINUM);
        else if(dollars >= 400) member.setTier(Tier.GOLD);
        else if(dollars >= 200) member.setTier(Tier.SILVER);
        else member.setTier(Tier.BRONZE);
        memberService.modifyMember(member);
    }

    private static void printMemInfo(Member member) {
        System.out.println("\n====== 회원 정보 ======");
        System.out.println("닉네임: " + member.getNickname());
        System.out.println("티어: " + member.getTier());
        System.out.println("나이: " + member.getAge());
        System.out.println("잔고: $" + member.getDollars());
        System.out.println("====================");
    }

    private static void printGameListInfo(ArrayList<Game> gameList) {
        System.out.println("\n====== 최근 게임 ======");
        if(gameList.isEmpty()) System.out.println("최근 전적이 없습니다.");
        for(Game game: gameList) {
            System.out.println(game);
        }
        System.out.println("====================");
    }

    private static void printGameStatus(Member member, int betLimit, Game game) {
        System.out.println("\n====== Black Jack ======");
        System.out.println("현재 티어: " + member.getTier());
        System.out.println("잔고: $" + member.getDollars());
        System.out.println("최대 베팅: $" + betLimit + ", 남은 카드: " + game.getDeck().cardsLeft());
        System.out.println("현재 베팅: $" + game.getBet());
        System.out.println("현재 손익: " + game.getResult() + "달러($)");
    }

    private static void printBothCards(ArrayList<Card> dealerCard, ArrayList<Card> playerCard, boolean isPlayerTurn) {
        System.out.println("\n====== 딜러 카드 ======");
        Card.printHorizontalCards(dealerCard, isPlayerTurn);
        System.out.println("======================");
        System.out.println("딜러 숫자 합: " + Card.sumCardsPoint(dealerCard,isPlayerTurn));

        System.out.println("\n====== 플레이어 카드 ======");
        Card.printHorizontalCards(playerCard, false);
        System.out.println("======================");
        System.out.println("플레이어 숫자 합: " + Card.sumCardsPoint(playerCard,false));
    }

    private static void printFinalResult(Game game) {
        System.out.println("\n====== 최종 결과 ======");
        System.out.println("손익: " + game.getResult() + "달러($)");
        System.out.println("\n방을 나갑니다.");
    }
}
