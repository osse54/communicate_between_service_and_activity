package com.example.communicate_between_service_and_activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.example.communicate_between_service_and_activity.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;

    private ServiceConnection mServiceConnection;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater()); // 뷰 바인딩
        setContentView(binding.getRoot());

        // 서비스 bind, unbind 이벤트 발생 시 호출되는 콜백 메서드 정의
        mServiceConnection = new ServiceConnection() {
            @Override // 서비스와 연결되었을 때 호출됨
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i(TAG, "onServiceConnected: 호출");
                Messenger messenger = new Messenger(service); // 파라미터로 받은 IBinder 객체로 Messenger 객체 생성

                // 서비스에 보낼 메시지 객체 생성
                // 생성한 handler를 obtain()에서 handler 자리를 null로 두는 이유는 Messenger로 보낼 때는 서비스에 정의된 Handler로 덮어쓰기 때문입니다.
                // arg1과 arg2에 임의의 값을 담습니다.
                // obj에 서비스에서 실행할 핸들러를 담습니다.
                Message msg = Message.obtain(null, MyService.MESSAGE_CODE_SEND, 18, 28);
                msg.replyTo = new Messenger(handler); // 서비스에서 보낸 메시지를 받을 핸들러를 저장
                try {
                    // 위에서 생성한 messenger 객체를 통해 서비스에 생성한 Message 객체를 보냄
                    messenger.send(msg);
                } catch (RemoteException e) {
                    Log.e(TAG, "onServiceConnected: mServiceConnection에서 messenger에 메시지 객체를 send()로 보내다가 발생한 에러", e);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) { // 서비스와 연결이 끊어졌을 때 호출됨
                Log.i(TAG, "onServiceDisconnected: 호출");
            }
        };

        // 서비스에서 호출할 핸들러, msg.getTarget().handleMessage(msg1); 이 부분에서 호출됨, message에서 target 변수에 저장됨.
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Log.i(TAG, "handleMessage: 호출 = " + msg);
                binding.tv.setText((String) msg.obj); // 서비스에서 받아온 메시지를 텍스트뷰에 표시
                unbindService(mServiceConnection); // 서비스 언바인드
            }
        };

        // 버튼 클릭 이벤트
        binding.btn.setOnClickListener(v -> {
            Log.i(TAG, "onCreate: binding.btn.setOnClickListener: 클릭");
            Intent intent = new Intent(getApplicationContext(), MyService.class); // 서비스 인텐트 생성
            bindService(intent, mServiceConnection, BIND_AUTO_CREATE); // 서비스 바인딩
        });
    }
}