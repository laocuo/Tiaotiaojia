package com.laocuo.jumpjump.battle;

import android.content.Context;
import android.text.TextUtils;

import com.laocuo.jumpjump.login.JumpUser;
import com.laocuo.jumpjump.utils.L;

import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.ValueEventListener;

/**
 * Created by hoperun on 12/27/16.
 */

public class BattlePresenter implements BattleContract.Presenter {
    private BattleContract.View mView;
    private BmobRealTimeData bmobRealTimeData;
    private BattleEventListener mEventListener;
    private RoomInfo mCurrentRoom;
    private boolean isHomeUser;
    private JumpUser mCurrentUser;
    private boolean isListenRoom;
//    private boolean isListenChess;
    private boolean isListenCommand;
    private Context mContext;

    public BattlePresenter(BattleContract.View mView) {
        this.mView = mView;
        mContext = (Context) mView;
        mView.setPresenter(this);
        mCurrentRoom = new RoomInfo();
        mCurrentUser = BmobUser.getCurrentUser(JumpUser.class);
        mEventListener = new BattleEventListener();
    }

    @Override
    public void start() {
        L.i("bmob", "start isHomeUser=" + isHomeUser);
        getAnotherUserInfo();
//        listenChessUpdate();
        listenCommandUpdate();
    }

    private void getAnotherUserInfo() {
        String objectId;
        if (isHomeUser) {
            objectId = mCurrentRoom.getAwayUser();
        } else {
            objectId = mCurrentRoom.getHomeUser();
        }
        L.i("bmob", "getAnotherUserInfo=" + objectId);
        BmobQuery bmobQuery = new BmobQuery("_User");
        bmobQuery.getObject(objectId, new QueryListener<JumpUser>() {

            @Override
            public void done(JumpUser user, BmobException e) {
                if (e == null) {
//                    saveAnotherUserAvatar(user);
                    mView.updateMemberInfo(user);
                } else {
                    L.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

//    private void saveAnotherUserAvatar(final JumpUser user) {
//        BmobFile avatar = user.getAvatar();
//        final String url = avatar == null ? "" : avatar.getFileUrl();
//        if (!TextUtils.isEmpty(url)) {
//            L.d("download AnotherAvatar");
//            File file =  new File(FactoryInterface.getAnotherAvatarPath(mContext));
//            avatar.download(file, new DownloadFileListener() {
//                @Override
//                public void done(String s, BmobException e) {
//                    if (e == null) {
//                        L.d("下载成功,保存路径:"+s);
//                        mView.updateMemberInfo(user);
//                    } else {
//                        L.i("bmob", "下载失败："+e.getErrorCode()+","+e.getMessage());
//                    }
//                }
//
//                @Override
//                public void onProgress(Integer value, long newworkSpeed) {
//                    L.i("bmob", "下载进度："+value+","+newworkSpeed);
//                }
//            });
//        }
//    }

    @Override
    public void createRoomSuccess() {
        listenRoomUpdate();
    }

    @Override
    public void queryRoom() {
        BmobQuery bmobQuery = new BmobQuery("Room");
        bmobQuery.findObjects(new FindListener<Room>() {

            @Override
            public void done(List<Room> list, BmobException e) {
                if (e == null) {
                    mView.updateRoom(list);
                }
            }
        });
    }

    @Override
    public void createRoom() {
        mCurrentRoom.clearInfo();
        mCurrentRoom.setRoomName(mCurrentUser.getUsername());
        mCurrentRoom.setHomeUser(mCurrentUser.getObjectId());
        Room room = new Room();
        room.setRoomName(mCurrentUser.getUsername());
        room.setHomeUser(mCurrentUser.getObjectId());
        room.setAwayUser("");
        room.setRoomId("");
        room.setPlaying(false);
        room.setCanJoin(false);
        room.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    L.i("bmob", "createRoom success " + s);
                    mCurrentRoom.setObjectId(s);
                    mView.createRoomSuccess();
                } else {
                    L.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                    mView.createRoomFail();
                }
            }
        });
    }

    @Override
    public void setRoomCanJoin() {
        Room room = new Room();
        room.setCanJoin(true);
        room.update(mCurrentRoom.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                boolean ret = true;
                if (e != null) {
                    L.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                    ret = false;
                } else {
                    mCurrentRoom.setCanJoin(true);
                    ret = true;
                }
                mView.setRoomCanJoinResult(ret);
            }
        });
    }

//    @Override
//    public void queryChessStep() {
//        L.i("bmob", "queryChessStep");
//        BmobQuery chessQuery = new BmobQuery("Chess");
//        chessQuery.getObject(mCurrentRoom.getRoomId(), new QueryListener<Chess>() {
//            @Override
//            public void done(Chess chess, BmobException e) {
//                if (e == null) {
//                    if (TextUtils.isEmpty(chess.getHomeUser()) ||
//                            TextUtils.isEmpty(chess.getAwayUser()) ||
//                            TextUtils.isEmpty(chess.getChessStep())) {
//                        L.i("bmob", "queryChessStep something is empty");
//                    } else {
//                        L.i("bmob", "parseChessStep 1:" + chess.getChessStep());
//                        mView.parseChessStep(chess.getChessStep());
//                    }
//                } else {
//                    L.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
//                }
//            }
//        });
//    }

    @Override
    public void queryRoomJoin() {
        L.i("bmob", "queryRoomJoin:"+mCurrentRoom.getObjectId());
        //fix, stop loop timer after destory room
        if (TextUtils.isEmpty(mCurrentRoom.getObjectId())) {
            return;
        }
        BmobQuery roomQuery = new BmobQuery("Room");
        roomQuery.getObject(mCurrentRoom.getObjectId(), new QueryListener<Room>() {

            @Override
            public void done(Room room, BmobException e) {
                if (e == null) {
                    //TextUtils.isEmpty(room.getRoomId())
                    if (TextUtils.isEmpty(room.getAwayUser()) ||
                            room.getPlaying() == false) {
                        L.i("bmob", "queryRoomJoin something is empty");
                    } else {
                        if (mCurrentRoom.getPlaying() == false) {
                            unListenRoomUpdate();
                            mCurrentRoom.setRoomId(room.getRoomId());
                            mCurrentRoom.setAwayUser(room.getAwayUser());
                            mCurrentRoom.setPlaying(room.getPlaying());
                            isHomeUser = true;
                            mView.startGame(true);
                        }
                    }
                } else {
                    L.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    //1:request reset;2:success;3:fail
    @Override
    public void resetRoom(int value) {
        L.i("bmob", "resetRoom value="+value);
        final int val = value;
        Command command = new Command();
        command.setRoomId(mCurrentRoom.getObjectId());
        command.setChessStep(getPreFix()+"reset:"+value);
        command.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                boolean ret = true;
                if (e != null) {
                    L.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                    ret = false;
                } else {
                    L.i("bmob", "resetRoom success");
                    ret = true;
                }
                if (val == 1) {
                    mView.resetRoomResult(ret);
                }
            }
        });

//        Chess chess = new Chess();
//        chess.setResetConfirm(value);
//        chess.update(mCurrentRoom.getRoomId(), new UpdateListener() {
//            @Override
//            public void done(BmobException e) {
//                boolean ret = true;
//                if (e != null) {
//                    L.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
//                    ret = false;
//                } else {
//                    L.i("bmob", "resetRoom success");
//                    ret = true;
//                }
//                if (val == 1) {
//                    mView.resetRoomResult(ret);
//                }
//            }
//        });
    }

//    @Override
//    public void queryResetRoom() {
//        L.i("bmob", "queryResetRoom");
//        BmobQuery chessQuery = new BmobQuery("Chess");
//        chessQuery.getObject(mCurrentRoom.getRoomId(), new QueryListener<Chess>() {
//            @Override
//            public void done(Chess chess, BmobException e) {
//                if (e == null) {
//                    if (TextUtils.isEmpty(chess.getHomeUser()) ||
//                            TextUtils.isEmpty(chess.getAwayUser()) ||
//                            TextUtils.isEmpty(chess.getChessStep())) {
//                        L.i("bmob", "queryResetRoom something is empty");
//                    } else {
//                        int confirmResult = chess.getResetConfirm();
//                        L.i("bmob", "getResetConfirm:" + confirmResult);
//                        if (confirmResult > 1) {
//                            mView.resetRoomConfirmResult(confirmResult);
//                        }
//                    }
//                } else {
//                    L.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
//                }
//            }
//        });
//    }

    @Override
    public void initChessListener() {
//        unListenChessUpdate();
//        listenChessUpdate();
        unListenCommandUpdate();
        listenCommandUpdate();
    }

    @Override
    public void opponentExit() {
        if (isHomeUser) {
            mCurrentRoom.setAwayUser("");
        } else {
            mCurrentRoom.setHomeUser("");
        }
    }

    @Override
    public void timeout() {
        Command command = new Command();
        command.setRoomId(mCurrentRoom.getObjectId());
        command.setChessStep(getPreFix()+"timeout");
        command.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e != null) {
                    L.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    @Override
    public void deleteRoom() {
        L.i("bmob", "deleteRoom " + mCurrentRoom.getObjectId());
        if (!TextUtils.isEmpty(mCurrentRoom.getObjectId())) {
            unListenRoomUpdate();
            Room room = new Room();
            room.delete(mCurrentRoom.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    boolean ret = false;
                    if (e != null) {
                        L.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                    } else {
                        ret = true;
                        mCurrentRoom.clearInfo();
                    }
                    mView.deleteRoomResult(ret);
                }
            });
        }
    }

    @Override
    public void joinRoom(String objectId) {
        L.i("bmob", "joinRoom " + objectId);
        BmobQuery bmobQuery = new BmobQuery("Room");
        bmobQuery.getObject(objectId, new QueryListener<Room>() {

            @Override
            public void done(Room room, BmobException e) {
                if (e == null) {
                    if (!TextUtils.isEmpty(room.getHomeUser()) &&
                            TextUtils.isEmpty(room.getAwayUser()) &&
                            room.getPlaying() == false &&
                            room.getCanJoin() == true) {
                        mCurrentRoom.clearInfo();
                        mCurrentRoom.setObjectId(room.getObjectId());
                        mCurrentRoom.setHomeUser(room.getHomeUser());
                        mCurrentRoom.setRoomName(room.getRoomName());
                        mCurrentRoom.setCanJoin(room.getCanJoin());
//                        Chess chess = new Chess();
//                        chess.setHomeUser(room.getHomeUser());
//                        chess.setAwayUser(mCurrentUser.getObjectId());
//                        chess.setChessStep("");
//                        chess.save(new SaveListener<String>() {
//                            @Override
//                            public void done(String s, BmobException e) {
//                                if (e == null) {
//                                    L.i("bmob", "createChess success " + s);
//                                    mCurrentRoom.setRoomId(s);
//                                    mCurrentRoom.setAwayUser(mCurrentUser.getObjectId());
//                                    mCurrentRoom.setPlaying(true);
//                                    Room room = new Room();
//                                    room.setRoomId(s);
//                                    room.setAwayUser(mCurrentUser.getObjectId());
//                                    room.setPlaying(true);
//                                    L.i("bmob", "joinRoom update " + mCurrentRoom.getObjectId());
//                                    room.update(mCurrentRoom.getObjectId(), new UpdateListener() {
//                                        @Override
//                                        public void done(BmobException e) {
//                                            if (e == null) {
//                                                isHomeUser = false;
//                                                mView.startGame(false);
//                                            } else {
//                                                L.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
//                                                joinRoomFail();
//                                            }
//                                        }
//                                    });
//                                } else {
//                                    joinRoomFail();
//                                    L.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
//                                }
//                            }
//                        });
                        mCurrentRoom.setAwayUser(mCurrentUser.getObjectId());
                        mCurrentRoom.setPlaying(true);
                        room.setAwayUser(mCurrentUser.getObjectId());
                        room.setPlaying(true);
                        L.i("bmob", "joinRoom update " + mCurrentRoom.getObjectId());
                        room.update(mCurrentRoom.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    isHomeUser = false;
                                    mView.startGame(false);
                                } else {
                                    L.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                                    joinRoomFail();
                                }
                            }
                        });
                    } else {
                        joinRoomFail();
                    }
                } else {
                    L.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                    joinRoomFail();
                }
            }
        });
    }

    @Override
    public void updateChessStep(String chessStep) {
        L.i("bmob", "updateChessStep " + chessStep);
//        Chess chess = new Chess();
//        chess.setChessStep(chessStep);
//        chess.update(mCurrentRoom.getRoomId(), new UpdateListener() {
//            @Override
//            public void done(BmobException e) {
//                boolean ret = true;
//                if (e != null) {
//                    L.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
//                    ret = false;
//                } else {
//                    L.i("bmob", "updateChessStep success");
//                    ret = true;
//                }
//                mView.updateChessStepResult(ret);
//            }
//        });
        Command command = new Command();
        command.setRoomId(mCurrentRoom.getObjectId());
        command.setChessStep(chessStep);
        command.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                boolean ret = true;
                if (e != null) {
                    L.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                    ret = false;
                } else {
                    L.i("bmob", "updateChessStep success");
                    ret = true;
                }
                mView.updateChessStepResult(ret);
            }
        });
    }

    @Override
    public void exit(boolean isHomeUser) {
//        unListenChessUpdate();
        unListenCommandUpdate();
        L.i("bmob", "exit isHomeUser= " + isHomeUser);
        L.i("bmob", "exit homeUser=" + mCurrentRoom.getHomeUser());
        L.i("bmob", "exit awayUser=" + mCurrentRoom.getAwayUser());
        if (TextUtils.isEmpty(mCurrentRoom.getAwayUser()) &&
                TextUtils.isEmpty(mCurrentRoom.getHomeUser())) {
            exitGame();
        } else if (isHomeUser && TextUtils.isEmpty(mCurrentRoom.getAwayUser()) ||
                !isHomeUser && TextUtils.isEmpty(mCurrentRoom.getHomeUser())) {
            //exit directly
//            if (!TextUtils.isEmpty(mCurrentRoom.getRoomId())) {
//                Chess chess = new Chess();
//                chess.delete(mCurrentRoom.getRoomId(), new UpdateListener() {
//                    @Override
//                    public void done(BmobException e) {
//                        if (e != null) {
//                            L.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
//                            exitGame();
//                        } else {
//                            if (!TextUtils.isEmpty(mCurrentRoom.getObjectId())) {
//                                Room room = new Room();
//                                room.delete(mCurrentRoom.getObjectId(), new UpdateListener() {
//                                    @Override
//                                    public void done(BmobException e) {
//                                        if (e != null) {
//                                            L.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
//                                        }
//                                        exitGame();
//                                    }
//                                });
//                            }
//                        }
//                    }
//                });
//            } else {
//                if (!TextUtils.isEmpty(mCurrentRoom.getObjectId())) {
//                    Room room = new Room();
//                    room.delete(mCurrentRoom.getObjectId(), new UpdateListener() {
//                        @Override
//                        public void done(BmobException e) {
//                            if (e != null) {
//                                L.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
//                            }
//                            exitGame();
//                        }
//                    });
//                }
//            }
            if (!TextUtils.isEmpty(mCurrentRoom.getObjectId())) {
                Room room = new Room();
                room.delete(mCurrentRoom.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e != null) {
                            L.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                        }
                        exitGame();
                    }
                });
            }
        } else {
//            Chess chess = new Chess();
//            if (isHomeUser) {
//                chess.setHomeUser("");
//            } else {
//                chess.setAwayUser("");
//            }
//            chess.update(mCurrentRoom.getRoomId(), new UpdateListener() {
//                @Override
//                public void done(BmobException e) {
//                    if (e != null) {
//                        L.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
//                    }
//                    exitGame();
//                }
//            });
            Command command = new Command();
            command.setRoomId(mCurrentRoom.getObjectId());
            command.setChessStep(getPreFix()+"exit");
            command.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e != null) {
                        L.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                    }
                    exitGame();
                }
            });
        }
    }

    private String getPreFix() {
        return isHomeUser ? "R:" : "G:";
    }

    private void exitGame() {
        mCurrentRoom.clearInfo();
        mView.exitGame();
    }

    private class BattleEventListener implements ValueEventListener {

        @Override
        public void onConnectCompleted(Exception e) {
            if (bmobRealTimeData.isConnected()) {
                if (isListenRoom) {
                    L.i("bmob", "onConnectCompleted subRowUpdate Room");
                    bmobRealTimeData.subRowUpdate("Room", mCurrentRoom.getObjectId());
                    mView.roomCanJoin();
                }
                if (isListenCommand) {
                    L.i("bmob", "onConnectCompleted subTableUpdate Command");
                    bmobRealTimeData.subTableUpdate("Command");
                }
//                if (isListenChess) {
//                    L.i("bmob", "onConnectCompleted subRowUpdate Chess");
//                    bmobRealTimeData.subRowUpdate("Chess", mCurrentRoom.getRoomId());
//                }
            }
        }

        @Override
        public void onDataChange(JSONObject object) {
            String action = object.optString("action");
            String tableName = object.optString("tableName");
            L.i("bmob", "(" + action + ")" + "数据：" + object);
            if (BmobRealTimeData.ACTION_UPDATEROW.equals(action)) {
                if ("Room".equals(tableName) && isListenRoom) {
                    JSONObject data = object.optJSONObject("data");
                    String roomId = data.optString("roomId");
                    boolean isPlaying = data.optBoolean("isPlaying", false);
                    String awayUser = data.optString("awayUser");
                    if (isPlaying == true && !TextUtils.isEmpty(awayUser) && !TextUtils.isEmpty(roomId)) {
                        if (mCurrentRoom.getPlaying() == false) {
                            unListenRoomUpdate();
                            mCurrentRoom.setRoomId(roomId);
                            mCurrentRoom.setAwayUser(awayUser);
                            mCurrentRoom.setPlaying(true);
                            isHomeUser = true;
                            mView.startGame(true);
                        }
                    }
                }
//                if ("Chess".equals(tableName) && isListenChess) {
//                    JSONObject data = object.optJSONObject("data");
//                    String chessStep = data.optString("chessStep");
//                    String homeUser = data.optString("homeUser");
//                    String awayUser = data.optString("awayUser");
//                    int roomReset = data.optInt("resetConfirm");
//                    if (TextUtils.isEmpty(homeUser) || TextUtils.isEmpty(awayUser)) {
//                        L.i("bmob", "runAway homeUser=" + homeUser);
//                        L.i("bmob", "runAway awayUser=" + awayUser);
//                        mCurrentRoom.setHomeUser(homeUser);
//                        mCurrentRoom.setAwayUser(awayUser);
//                        mView.runAway();
//                    } else {
//                        L.i("bmob", "onDataChange chessStep=" + chessStep);
//                        L.i("bmob", "onDataChange roomReset=" + roomReset);
//                        if (roomReset > 0) {
//                            mView.resetRoomConfirmResult(roomReset);
//                        } else {
//                            if (!TextUtils.isEmpty(chessStep)) {
//                                L.i("bmob", "parseChessStep:" + chessStep);
//                                mView.parseChessStep(chessStep);
//                            }
//                        }
//                    }
//                }
            }
            if (BmobRealTimeData.ACTION_UPDATETABLE.equals(action)) {
                if ("Command".equals(tableName) && isListenCommand) {
                    JSONObject data = object.optJSONObject("data");
                    String roomId = data.optString("roomId");
                    String chessStep = data.optString("chessStep");
                    if (roomId.equals(mCurrentRoom.getObjectId())) {
                        mView.parseChessStep(chessStep);
                    }
                }
            }
        }
    }

    private void joinRoomFail() {
        L.i("bmob", "joinRoomFail");
        mCurrentRoom.clearInfo();
        mView.joinRoomFail();
    }

    private void listenRoomUpdate() {
        if (isListenRoom == false) {
            L.i("bmob", "listenRoomUpdate " + mCurrentRoom.getObjectId());
            if (bmobRealTimeData == null) {
                bmobRealTimeData = new BmobRealTimeData();
            }
            if (bmobRealTimeData.isConnected() == false) {
                L.i("bmob", "start(mEventListener)");
                isListenRoom = true;
                bmobRealTimeData.start(mEventListener);
            } else {
                L.i("bmob", "subRowUpdate Room");
                isListenRoom = true;
                bmobRealTimeData.subRowUpdate("Room", mCurrentRoom.getObjectId());
                mView.roomCanJoin();
            }
        }
    }

    private void unListenRoomUpdate() {
        if (isListenRoom == true) {
            L.i("bmob", "unListenRoomUpdate " + mCurrentRoom.getObjectId());
            if (bmobRealTimeData != null) {
                L.i("bmob", "unsubRowUpdate Room");
                isListenRoom = false;
                bmobRealTimeData.unsubRowUpdate("Room", mCurrentRoom.getObjectId());
            }
        }
    }

//    private void listenChessUpdate() {
//        if (isListenChess == false) {
//            L.i("bmob", "listenChessUpdate " + mCurrentRoom.getRoomId());
//            if (bmobRealTimeData == null) {
//                bmobRealTimeData = new BmobRealTimeData();
//            }
//            if (bmobRealTimeData.isConnected() == false) {
//                isListenChess = true;
//                bmobRealTimeData.start(mEventListener);
//            } else {
//                L.i("bmob", "subRowUpdate Chess");
//                isListenChess = true;
//                bmobRealTimeData.subRowUpdate("Chess", mCurrentRoom.getRoomId());
//            }
//        }
//    }
//
//    private void unListenChessUpdate() {
//        if (isListenChess == true) {
//            L.i("bmob", "unListenChessUpdate " + mCurrentRoom.getRoomId());
//            if (bmobRealTimeData != null) {
//                L.i("bmob", "unsubRowUpdate Chess");
//                isListenChess = false;
//                bmobRealTimeData.unsubRowUpdate("Chess", mCurrentRoom.getRoomId());
//            }
//        }
//    }

    private void listenCommandUpdate() {
        if (isListenCommand == false) {
            L.i("bmob", "listenCommandUpdate " + mCurrentRoom.getObjectId());
            if (bmobRealTimeData == null) {
                bmobRealTimeData = new BmobRealTimeData();
            }
            if (bmobRealTimeData.isConnected() == false) {
                isListenCommand = true;
                bmobRealTimeData.start(mEventListener);
            } else {
                L.i("bmob", "subTableUpdate Command");
                isListenCommand = true;
                bmobRealTimeData.subTableUpdate("Command");
            }
        }
    }

    private void unListenCommandUpdate() {
        if (isListenCommand == true) {
            L.i("bmob", "unListenCommandUpdate " + mCurrentRoom.getObjectId());
            if (bmobRealTimeData != null) {
                L.i("bmob", "unsubTableUpdate Command");
                isListenCommand = false;
                bmobRealTimeData.unsubTableUpdate("Command");
            }
        }
    }
}
