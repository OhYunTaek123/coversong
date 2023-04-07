package com.example.coversong.main

import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import com.kakao.sdk.common.KakaoSdk


class LoginActivity : AppCompatActivity() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init("{NATIVE_APP_KEY}")
}