package com.test.helloworld.androidtest2;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.test.helloworld.androidtest2.broadcast.SmsTestSentBroadcast;
import com.test.helloworld.androidtest2.content.provider.TestContentResolverActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private BroadcastReceiver broadcast;
    private BroadcastReceiver broadcast2;

    private ImageView takePhotoIv;
    private Button takePhotoBtn;
    private Button choosePhotoBtn;
    private ImageView choosePhotoIv;

    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public static final int CHOOSE_PHOTO = 3;
    public static final String PHOTO_FILE = "output_photo.jpg";
    private Uri uriPhoto;


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
                            Manifest.permission.WRITE_CONTACTS}, 0);
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

        findViewById(R.id.notification_btn).setOnClickListener(new View.OnClickListener() {
            String id = "channel_1";
            String name = "notification_test";
            String description = "test";

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                NotificationManager mn = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                //todo 旧版notification创建方法已经过时，在4.0.3以后。
//                Notification n = new Notification(R.mipmap.ic_launcher, "消息来了",
//                        System.currentTimeMillis());
//                Intent i = new Intent(TestActivity.this, NotificationTestActivity.class);
//                PendingIntent pi = PendingIntent.getActivity(TestActivity.this, 0, i,
//                        PendingIntent.FLAG_CANCEL_CURRENT);
//                n.setLatestEventInfo(this, "title", "content", pi);

                //todo 在8.0后过时，增加了通知渠道的参数，需要创建通知渠道
                //todo 通知渠道
                createNotificationChannel(mn);
                Notification.Builder builder = new Notification.Builder(TestActivity.this, id);

                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setTicker("消息来了");
                builder.setWhen(System.currentTimeMillis());
                builder.setContentTitle("消息标题");
                builder.setContentText("消息内容");
//                builder.setSound(MediaStore.Audio.Media);
                builder.setAutoCancel(true);
                Intent i = new Intent(TestActivity.this, NotificationTestActivity.class);
                //todo 延时意图使用static方法，分别获取活动广播服务之一
                PendingIntent pi = PendingIntent.getActivity(TestActivity.this, 0, i, 0);
                builder.setContentIntent(pi);
                Notification n = builder.build();
                mn.notify(1, n);
                //todo 通知管理取消确定id的通知
//                mn.cancel(1);
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            private void createNotificationChannel(NotificationManager mn) {

                //todo 通知管理者创建通知渠道
                NotificationChannel nc = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
                nc.setDescription(description);
                nc.setName(name);
                //todo 渠道里面设置震动和声音
//                nc.setSound();
//                nc.setVibrationPattern();
                mn.createNotificationChannel(nc);
            }
        });


        final EditText numberEt = findViewById(R.id.send_number_et);
        final EditText contentEt = findViewById(R.id.send_content_et);
        findViewById(R.id.send_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(numberEt.getText().toString()) &&
                        !TextUtils.isEmpty(contentEt.getText().toString())) {
                    SmsManager sm = SmsManager.getDefault();
                    //todo  使用了呼应广播action的延时intent
                    PendingIntent pi = PendingIntent.getBroadcast(TestActivity.this,
                            0, new Intent("sent.test"), 0);
                    //todo  参数1是目标号码 ，参数2似乎是短信中心号码
                    sm.sendTextMessage(numberEt.getText().toString(), null,
                            contentEt.getText().toString(), pi, null);
                }
            }
        });

        //todo 动态注册了发短信广播，检测是否发送成功，使用了自定义action(发送成功时，只会得到一个返回码，而无广播相关)
        IntentFilter iff = new IntentFilter("sent.test");
        broadcast2 = new SmsTestSentBroadcast();
        registerReceiver(broadcast2, iff);

        //todo 动态注册了收短信广播，action可以从Intents类中获取引用字符串
        iff = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        broadcast = new SmsTestReceiverBroadcast();
        registerReceiver(broadcast, iff);

        //todo  直接在onCreate时，询问权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS}, 1);
        }

        //todo 在开始询问外部内存读写权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        takePhotoBtn = findViewById(R.id.take_photo_btn);
        takePhotoIv = findViewById(R.id.take_photo_iv);
        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(Environment.getExternalStorageDirectory(), PHOTO_FILE);
                try {
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                if (Build.VERSION.SDK_INT < 24) {
                uriPhoto = Uri.fromFile(file);
//                } else {
//                    ContentValues cv = new ContentValues(1);
//                    cv.put(MediaStore.Images.Media.DATA, PHOTO_FILE);
//                    uriPhoto = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
//                    Log.e("uriphoto", uriPhoto.toString());
//                }
                //todo action的各类，多媒体action
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
//                Intent i = new Intent("android.media.action.camera.CAPTURE_PICTURE");
                i.putExtra(MediaStore.EXTRA_OUTPUT, uriPhoto);
                startActivityForResult(i, TAKE_PHOTO);
            }
        });

        choosePhotoBtn = findViewById(R.id.choose_photo_btn);
        choosePhotoIv = findViewById(R.id.choose_photo_iv);
        choosePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i, CHOOSE_PHOTO);
            }
        });
    }

    private void dealPhotoKitKat(Intent data) {
        Uri uri = data.getData();
        String path = new String();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String ducumentId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = ducumentId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                path = getChosenPhotoPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri uriDownloads = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads")
                        , Long.parseLong(ducumentId));
                path = getChosenPhotoPath(uriDownloads, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            path = getChosenPhotoPath(uri, null);
        }
        setChosenPhotoImage(path);
    }

    private void dealPhotoBeforeKitKat(Intent data) {
        Uri photoUri = data.getData();
        setChosenPhotoImage(getChosenPhotoPath(photoUri, null));
    }

    private String getChosenPhotoPath(Uri uri, String selection) {

        Cursor c = getContentResolver().query(uri, null, selection, null, null);
        String path = new String();
        if (c != null) {
            if (c.moveToNext()) {
                //todo columnname MediaStore中的DATA列
                path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
//                c.getString(c.getColumnIndex("path"));
            }
            c.close();
        }
        return path;
    }

    private void setChosenPhotoImage(String filePath) {
        Log.e("image path", filePath);
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        if (bitmap != null) {
            choosePhotoIv.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "设置已选择图像失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        dealPhotoKitKat(data);
                    } else {
                        dealPhotoBeforeKitKat(data);
                    }
                }
                break;
            case TAKE_PHOTO:
                Log.e("resultcode", resultCode + "");
                //todo 通过结果码来判断是否拍照成功
                if (resultCode == RESULT_OK) {
                    //todo camera多媒体动作，没有固定引用
                    Intent i = new Intent("com.android.camera.action.CROP");
//                    Intent i = new Intent("com.android.camera.action.camera.CROP_PICTURE");
                    i.putExtra("scale", true);
                    i.setDataAndType(uriPhoto, "image/*");
                    i.putExtra(MediaStore.EXTRA_OUTPUT, PHOTO_FILE);
                    startActivityForResult(i, CROP_PHOTO);
                } else {
                    Toast.makeText(this, "拍照失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case CROP_PHOTO:
                //todo 通过结果码判断是否剪切失败
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().
                                openInputStream(uriPhoto));
                        takePhotoIv.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(this, "拍照剪切失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcast);
        unregisterReceiver(broadcast2);
    }

    class SmsTestReceiverBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            //todo 短信生数据的键就是pdus
            Object[] pdu = (Object[]) bundle.get("pdus");
            SmsMessage[] messages = new SmsMessage[pdu.length];
            for (int i = 0; i < pdu.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdu[i]);
            }
            String number = messages[0].getOriginatingAddress();
            String fullMessage = new String();
            for (SmsMessage message : messages) {
                fullMessage += message.getMessageBody();
            }
//            Intent i = new Intent(context, TestActivity.class);
//            i.putExtra("from", number);
//            i.putExtra("message", fullMessage);
//            context.startActivity(i);
            //todo 动态注册了收短信广播
            Log.e("收短信", "收到收短信广播");
            ((TextView) findViewById(R.id.sender_number_tv)).setText(number);
            ((TextView) findViewById(R.id.sender_content_tv)).setText(fullMessage);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            //todo 联系人权限
            case 0:
                startActivity(new Intent(TestActivity.this, TestContentResolverActivity.class));
                break;
            //todo 短信等其它权限
            case 1:
                break;
        }
        return;
    }
}
