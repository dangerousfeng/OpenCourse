package com.feng.opencourse;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.feng.opencourse.util.PermissionsChecker;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;


public class PlaySectionActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 2;
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private PermissionsChecker mPermissionsChecker; // 权限检测器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_section);

        mPermissionsChecker = new PermissionsChecker(this);

        String courseFacePath = this.getIntent().getStringExtra("courseFacePath");
        courseFacePath = "file://"+"/storage/emulated/0/Download/_83351965_explorer273lincolnshirewoldssouthpicturebynicholassilkstone.jpg";
        System.out.println("======================");
        JZVideoPlayerStandard jzVideoPlayerStandard = (JZVideoPlayerStandard) findViewById(R.id.videoplayer);
        jzVideoPlayerStandard.setUp("http://mooc-feng.oss-cn-beijing.aliyuncs.com/mp4/%E7%8E%8B%E6%9D%B0%20-%20%E6%88%91%E4%BC%9A%E7%9F%A5%E9%81%93%E5%87%A0%E6%97%B6%E8%A6%81%E9%80%80.mp4"
                , JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "饺子闭眼睛");
        Picasso.with(PlaySectionActivity.this).load(courseFacePath).into(jzVideoPlayerStandard.thumbImageView);
        //jzVideoPlayerStandard.thumbImageView.setImage("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }
    }
    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, PERMISSIONS_REQUEST_CODE, PERMISSIONS);
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }
    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        //在相册里面选择好相片之后调回到现在的这个activity中
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if ( resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
                    finish();
                }
        }
    }
}
