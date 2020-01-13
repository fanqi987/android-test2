package com.test.helloworld.androidtest2;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;

public class MusicTestActivity extends BaseActivity {

    private MediaPlayer mp = new MediaPlayer();
    private File file;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.music);

        initPlayer();
        findViewById(R.id.music_play_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mp.isPlaying()) {
                    mp.start();
                }
            }
        });

        findViewById(R.id.music_pause_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp.isPlaying()) {
                    mp.pause();
                }
            }
        });
        findViewById(R.id.music_stop_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.reset();
                initPlayer();
            }
        });

    }

    private void initPlayer() {
        file = new File(Environment.getExternalStorageDirectory(), "Download/music.mp3");
        try {
            mp.setDataSource(file.getPath());
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            mp.stop();
            mp.release();
        }
    }
}
