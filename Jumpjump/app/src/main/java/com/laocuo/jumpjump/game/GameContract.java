package com.laocuo.jumpjump.game;

/**
 * Created by hoperun on 2/23/17.
 */

public interface GameContract {
    void updateChessStep(String rec);
    void refreshCurrentChess(int dir);
    void gameOver(String msg);
    void opponentExit();
    void resetRoomConfirmResult(int result);
}
