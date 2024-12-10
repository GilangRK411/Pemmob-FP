package com.example.pemmob_fp; // Pastikan nama package sesuai dengan project Anda

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Inisialisasi Firebase
        FirebaseApp.initializeApp(this);
    }
}
