package com.feng.opencourse;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.feng.opencourse.util.PermissionsChecker;

import cn.jzvd.JZVideoPlayerStandard;

public class CreateSectionActivity extends AppCompatActivity {

    private JZVideoPlayerStandard jzVideoPlayerStandard;
    private ImageView ivCreateSection;
    private EditText etSectionName;
    private EditText etSectionDesc;
    private Button btnCreateSec;

    private String secVideoPath;

    private final int VIDEO_REQUEST_CODE = 3;
    private static final int PERMISSIONS_REQUEST_CODE = 2;
    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private PermissionsChecker mPermissionsChecker; // 权限检测器


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_section);

        mPermissionsChecker = new PermissionsChecker(this);
        find();
    }

    private void find() {
        jzVideoPlayerStandard = (JZVideoPlayerStandard) findViewById(R.id.vp_create_section);
        ivCreateSection = (ImageView) findViewById(R.id.iv_create_section);
        ivCreateSection.setOnClickListener(selectSectionListener);
        etSectionName = (EditText) findViewById(R.id.et_sectionName);
        etSectionDesc = (EditText) findViewById(R.id.et_sectionDesc);
        btnCreateSec = (Button) findViewById(R.id.btn_createSection);
        btnCreateSec.setOnClickListener(createSectionListener);
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
    View.OnClickListener selectSectionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, VIDEO_REQUEST_CODE);
        }
    };
    Button.OnClickListener createSectionListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case VIDEO_REQUEST_CODE:
                if ( resultCode == RESULT_OK && null != data) {
                    Uri selectedVideo = data.getData();
                    String[] filePathColumn = {MediaStore.Video.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedVideo,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    secVideoPath = cursor.getString(columnIndex);
                    cursor.close();

                    jzVideoPlayerStandard.setUp(
                            secVideoPath,
                            JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL,
                            "");
                    ivCreateSection.setClickable(false);
                    ivCreateSection.setAlpha(0f);
                }else {
                    Toast.makeText(this, "recChoice", Toast.LENGTH_SHORT).show();
                }

            case PERMISSIONS_REQUEST_CODE:
                if ( resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
                    finish();
                }

        }
    }
}
