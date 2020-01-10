package com.test.helloworld.androidtest2.content.provider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.webkit.PermissionRequest;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.test.helloworld.androidtest2.BaseActivity;
import com.test.helloworld.androidtest2.R;
import com.test.helloworld.androidtest2.adapter.ContentResolverTestAdapter;

import java.security.acl.Permission;
import java.util.ArrayList;
import java.util.List;

public class TestContentResolverActivity extends BaseActivity {


    private ListView lv;
    private ContentResolverTestAdapter adapter;
    private TextView nameTv;
    private TextView numberTv;

    private List<List<String>> phones = new ArrayList();

    private ContentResolver cr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_resolver);
        lv = findViewById(R.id.content_resolver_lv);
        nameTv = findViewById(R.id.content_resolver_name_tv);
        numberTv = findViewById(R.id.content_resolver_number_tv);
        readPhones();
        adapter = new ContentResolverTestAdapter(this, R.layout.content_resolver, phones);
        lv.setAdapter(adapter);

    }

    private void readPhones() {
        cr = getContentResolver();
        Uri uri = Uri.parse("");
        Cursor c = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER},
                null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                List<String> phone = new ArrayList();
                phone.add(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                phone.add(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                phones.add(phone);
            }
            c.close();
        }
    }
}
