package com.test.helloworld.androidtest2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.test.helloworld.androidtest2.content.provider.TestContentResolverActivity;

public class TestActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById(R.id.content_resolver_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo 6.0以后某些权限的取得，需要向用户询问，而非放在清单文件取得
                if (ActivityCompat.checkSelfPermission(TestActivity.this, Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(TestActivity.this, Manifest.permission.WRITE_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(TestActivity.this, new String[]{Manifest.permission.READ_CONTACTS,
                            Manifest.permission.WRITE_CONTACTS}, PackageManager.PERMISSION_GRANTED);
                } else {
                    startActivity(new Intent(TestActivity.this, TestContentResolverActivity.class));
                }
            }
        });

        findViewById(R.id.content_provider_test_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TestActivity.this, TestContentProviderActivity.class));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PackageManager.PERMISSION_GRANTED:
                startActivity(new Intent(TestActivity.this, TestContentResolverActivity.class));
                break;
        }
        return;
    }
}
