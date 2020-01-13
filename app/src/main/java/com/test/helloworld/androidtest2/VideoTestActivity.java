package com.test.helloworld.androidtest2;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.VideoView;

import androidx.annotation.Nullable;

import java.io.File;

public class VideoTestActivity extends BaseActivity {

    private VideoView vv;
    private File file;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video);
        vv = findViewById(R.id.video_view);
        initVideo();
        findViewById(R.id.video_start_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!vv.isPlaying()) {
                    vv.start();
                }
            }
        });

        findViewById(R.id.video_pause_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vv.isPlaying()) {
                    vv.pause();
                }
            }
        });

        findViewById(R.id.video_restart_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (vv.isPlaying()) {
                vv.resume();
//                }
            }
        });
    }

    private void initVideo() {
        file = new File(Environment.getExternalStorageDirectory(), "DCIM/Camera/video.mp4");
        vv.setVideoPath(file.getPath());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vv != null) {
            vv.suspend();
        }
    }
}
