package com.laocuo.jumpjump.battle;

import com.laocuo.jumpjump.base.BasePresenter;
import com.laocuo.jumpjump.base.BaseView;
import com.laocuo.jumpjump.login.JumpUser;

import java.util.List;

/**
 * Created by hoperun on 12/27/16.
 */

public interface BattleContract {
    interface View extends BaseView<Presenter> {
        void createRoomSuccess();

        void createRoomFail();

        void setRoomCanJoinResult(boolean result);

        void updateRoom(List<Room> list);

        void startGame(boolean isHomeUser);

        void joinRoomFail();

        void parseChessStep(String chessStep);

        void exitGame();

        void roomCanJoin();

        void updateChessStepResult(boolean result);

        void resetRoomResult(boolean result);

        void updateMemberInfo(JumpUser anotherUser);

        void deleteRoomResult(boolean result);
    }

    interface Presenter extends BasePresenter {
        void createRoomSuccess();

        void queryRoom();

        void createRoom();

        void deleteRoom();

        void joinRoom(String objectId);

        void updateChessStep(String chessStep);

        void exit(boolean isHomeUser);

        void setRoomCanJoin();

        void queryRoomJoin();

        void resetRoom(int value);

        void initChessListener();

        void opponentExit();

        void timeout();
    }
}
