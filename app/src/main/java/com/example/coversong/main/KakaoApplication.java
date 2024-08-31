package com.example.coversong.main;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class KakaoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        KakaoSdk.init(this, "c9e8aedbe9321aa528c3a2c2c28336db");
    }
}
