/*
* Copyright (C) 2013-2016 laocuo@163.com .
* Modification based on code covered by the mentioned copyright
* and/or permission notice(s).
*/
/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.laocuo.jumpjump.battle;

import android.widget.Toast;

public class MessageIDCommon {
    public static int INIT = 0x01;

    public final static int RETURN_ROOM_NAME = 0x101;

    public final static int ROOM_CAN_JOIN = 0x102;

    public final static int MAIN_HANDLER_TIMER_30S = 0x103;

    public final static int JOIN_ROOM_SUCCESS = 0x104;

    public final static int CREATE_ROOM_SUCCESS = 0x105;

    public final static int CHESS_STEP_UPDATE = 0x106;

    public final static int CHESS_STEP_QUERY_UPDATE = 0x107;

    public final static int ROOM_JOIN_QUERY_UPDATE = 0x108;

    public final static int CHESS_STEP_UPDATE_RESULT = 0x109;

    public final static int RESET_ROOM_RESULT = 0x10A;

    public final static int RESET_ROOM_QUERY_UPDATE = 0x10B;

    public final static int RESET_ROOM_CONFIRM_RESULT = 0x10C;

    public final static int DIALOG_SHOW = 0x10D;

    public final static int DELETE_ROOM = 0x10E;

    public static int TOAST_TIME = Toast.LENGTH_SHORT;

    public static int DELAY_TIME = 1000;

    public static int LOOP_TIME = 5000;
}
