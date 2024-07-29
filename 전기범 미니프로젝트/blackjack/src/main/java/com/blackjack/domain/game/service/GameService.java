package com.blackjack.domain.game.service;

import com.blackjack.domain.game.aggregate.Game;
import com.blackjack.response.GameResponseObject;
import com.blackjack.domain.game.repository.GameRepository;

import java.util.ArrayList;

public class GameService {

    private final GameRepository gameRepository = new GameRepository();

    public GameService() {
    }

    public GameResponseObject saveGame(Game game) {
        // 비정상 Game Object일 경우
        if(game == null) return new GameResponseObject(null, false);
        // 정상 Game Object일 경우
        return new GameResponseObject(null, gameRepository.insertGame(game));
    }

    public GameResponseObject findGamesByMemNo(int memNo) {
        // 회원번호로 게임 전적 조회
        ArrayList<Game> gameList = gameRepository.selectGamesByMemNo(memNo);
        // 조회된 전적이 없는 경우
        if(gameList == null) return new GameResponseObject(null, false);
        // 조회된 전적이 있는 경우
        else return new GameResponseObject(gameList,true);
    }

    public GameResponseObject removeGame(int memNo) {
        // 해당 번호의 회원이 플레이한 게임 전적 삭제
        return new GameResponseObject(null,gameRepository.deleteGameByMemNo(memNo));
    }
}
