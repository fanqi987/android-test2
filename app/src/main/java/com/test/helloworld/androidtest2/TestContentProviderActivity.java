package com.test.helloworld.androidtest2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class TestContentProviderActivity extends BaseActivity {

    private ContentResolver cr;

    public static final String AUTHORITY = "com.test.activity.provider";

    public static final String CONTENT = "content://";

    private String newID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.provider_test);

        cr = getContentResolver();

        findViewById(R.id.provider_add_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo uri的完整格式,并且路径小写
                Uri uri = Uri.parse(CONTENT + AUTHORITY + "/book");
                //                Uri uri = Uri.parse(AUTHORITY + "/Book");

                ContentValues cv = new ContentValues();
                cv.put("name", "math book");
                cv.put("author", "math teacher");
                cv.put("page", "199");
                cv.put("price", "12.2");
                Uri uri2 = cr.insert(uri, cv);
                newID = uri2.getPathSegments().get(1);
                Log.i("增加", "增加成功");

            }
        });

        findViewById(R.id.provider_update_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo uri的完整格式,并且路径小写
                Uri uri = Uri.parse(CONTENT + AUTHORITY + "/book");
//                Uri uri = Uri.parse(AUTHORITY + "/Book");
                ContentValues cv = new ContentValues();
                cv.put("price", "9.9");
                int columns = cr.update(uri, cv, "id = ?", new String[]{newID});
                Log.i("更新", "更新成功" + columns);
            }
        });
        findViewById(R.id.provider_query_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo uri的完整格式,并且路径小写
                Uri uri = Uri.parse(CONTENT + AUTHORITY + "/book");
                Cursor c = cr.query(uri, null, null, null, null, null);
                if (c != null) {
                    while (c.moveToNext()) {
                        Log.i("name", c.getString(c.getColumnIndex("name")));
                        Log.i("author", c.getString(c.getColumnIndex("author")));
                        Log.i("page", String.valueOf(c.getInt(c.getColumnIndex("page"))));
                        Log.i("price", String.valueOf(c.getFloat(c.getColumnIndex("price"))));
                    }
                    c.close();
                }
            }
        });

        findViewById(R.id.provider_del_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo uri的完整格式,并且路径小写
//                Uri uri = Uri.parse(CONTENT + AUTHORITY + "/book");
//                int columns = cr.delete(uri, "id = ?", new String[]{newID});
                //todo 另一种写法，使用uri条件
                Uri uri = Uri.parse(CONTENT + AUTHORITY + "/book/" + newID);
                int columns = cr.delete(uri, null, null);
                Log.i("删除", "删除了" + columns);

            }
        });

    }
}
