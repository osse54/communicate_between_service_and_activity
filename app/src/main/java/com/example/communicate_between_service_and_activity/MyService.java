package com.example.communicate_between_service_and_activity;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

public class MyService extends Service {
    private static final String TAG = "MyService";

    public static final int MESSAGE_CODE_SEND = 1;
    public static final int MESSAGE_CODE_ANSWER = 10;

    private static class MessageHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Log.i(TAG, "handleMessage: 호출 = " + msg);
            Log.i(TAG, "handleMessage: 호출 msg.what = " + msg.what);
            // 받은 메시지로부터 특정 값을 추출하여 다시 메시지를 생성하여 보냄, 보낼 때 obj 변수에 문자열 값을 저장함
            Message msg1 = Message.obtain(null, MESSAGE_CODE_ANSWER, "서비스에서 생성한 MessageHandler 객체에서 Message를 받았습니다. msg.arg1 = " + msg.arg1 + ", msg.arg2 = " + msg.arg2);
            try {
                msg.replyTo.send(msg1); // 추출한 핸들러 객체를 통해 새로 생성한 메시지를 보냄
            } catch (RemoteException e) {
                Log.e(TAG, "handleMessage: 서비스에서 생성한 Handler객체에서 메시지를 받았고, replyTo 변수에 할당된 Messenger 객체에 새로 생성한 Messge 객체(msg1)을 send()를 호출하여 보내다 발생한 에러", e);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: 호출");
        return new Messenger(new MessageHandler()).getBinder();
    }
}