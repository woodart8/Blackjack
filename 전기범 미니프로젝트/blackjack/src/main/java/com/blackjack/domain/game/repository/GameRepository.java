package com.blackjack.domain.game.repository;

import com.blackjack.domain.game.aggregate.Game;
import com.blackjack.domain.member.aggregate.Member;
import com.blackjack.stream.MyObjectOutput;

import java.io.*;
import java.util.ArrayList;

public class GameRepository {

    private ArrayList<Game> gameList = new ArrayList<>();
    private final File file;
    private final String filePath = "전기범 미니프로젝트/blackjack/src/main/java/com/blackjack/domain/game/db/gameDB.dat";

    public GameRepository() {
        file = new File(filePath);
        // 파일이 없는 경우
        if(!file.exists()) {
            // 파일 생성 및 헤더 추가
            saveGames(file, gameList);
        }
        loadGames(file);
    }

    private void saveGames(File file, ArrayList<Game> games) {
        // 파일을 덮어 씌워 저장
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(file)
                    )
            );
            for(Game game: games) {
                oos.writeObject(game);
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

    private void loadGames(File file) {
        // 파일을 읽어 ArrayList에 저장
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(
                    new BufferedInputStream(
                            new FileInputStream(file)
                    )
            );

            while(true) {
                gameList.add((Game)ois.readObject());
            }
        } catch (EOFException e) {
            System.out.print("");
        } catch (IOException e) {
            System.out.println("IOException");
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException");
            throw new RuntimeException(e);
        } finally {
            try {
                if(ois != null) ois.close();
            } catch (IOException e) {
                System.out.println("ois close failure");
            }
        }
    }

    private int selectLastGameNo() {
        // 마지막 게임의 게임번호를 리턴(게임이 존재하지 않는 경우 0)
        if(gameList.isEmpty()) return 0;
        return gameList.get(gameList.size() - 1).getGameNo();
    }

    public boolean insertGame(Game game) {
        // 파일에 이어붙여 저장
        MyObjectOutput moo = null;
        boolean result = false;
        try {
            moo = new MyObjectOutput(
                    new BufferedOutputStream(
                            new FileOutputStream(filePath, true)
                    )
            );
            game.setGameNo(selectLastGameNo()+1);
            moo.writeObject(game);
            gameList.add(game);
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

    public ArrayList<Game> selectGamesByMemNo(int memNo) {
        ArrayList<Game> games = new ArrayList<>();
        int cnt = 0;
        // ArrayList의 뒤에서 부터 10게임 가져온다.
        for(int i=gameList.size()-1; i>=0; i--) {
            Game game = gameList.get(i);
            if(game.getPlayer().getMemNo() == memNo) {
                games.add(game);
                cnt++;
            }
            if(cnt == 6) break;
        }
        return games;
    }

    public boolean deleteGameByMemNo(int memNo) {
        ArrayList<Game> newGameList = new ArrayList<>();
        // 탈퇴한 회원의 전적을 제외한다.
        for(Game game: gameList) {
            Member member = game.getPlayer();
            if(member.getMemNo() == memNo) continue;
            newGameList.add(game);
        }
        gameList = newGameList;
        saveGames(file,gameList);
        return true;
    }
}
