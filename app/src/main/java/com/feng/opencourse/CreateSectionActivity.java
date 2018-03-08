package com.feng.opencourse;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.feng.opencourse.util.HttpUtil;
import com.feng.opencourse.util.MyApplication;
import com.feng.opencourse.util.PermissionsChecker;
import com.feng.opencourse.util.ProperTies;
import com.feng.opencourse.util.UriPathUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Properties;

import cn.jzvd.JZVideoPlayerStandard;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.feng.opencourse.util.HttpUtil.getRespData;

public class CreateSectionActivity extends AppCompatActivity {

    private JZVideoPlayerStandard jzVideoPlayerStandard;
    private ImageView ivCreateSection;
    private EditText etSectionName;
    private EditText etSectionDesc;
    private Button btnCreateSec;
    private ProgressBar pbUploadSec;

    private String secVideoPath;
    private String courseId;
    private String secId;

    private MyApplication myapp;

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

        Intent intent = getIntent();
        courseId = intent.getStringExtra("courseId");
        myapp = (MyApplication) getApplication();
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
        pbUploadSec = (ProgressBar) findViewById(R.id.pb_upload_section);
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

//          Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            Intent intent=new Intent("android.intent.action.GET_CONTENT");
            intent.setType("video/*");
            startActivityForResult(intent, VIDEO_REQUEST_CODE);
        }
    };
    Button.OnClickListener createSectionListener = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            ivCreateSection.setVisibility(View.GONE);
            jzVideoPlayerStandard.setClickable(false);
            jzVideoPlayerStandard.setAlpha(0.5f);
            pbUploadSec.setVisibility(View.VISIBLE);

            JSONObject json = new JSONObject();
            try {
                json.put("ActionId", 201);
                json.put("JWT", myapp.getJWT());
                json.put("userId", myapp.getUserId());
                json.put("courseId",courseId);
                json.put("secName", etSectionName.getText());
                json.put("secDesc", etSectionDesc.getText());

                String req = json.toString();
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), req);
                HttpUtil.sendAsyncRequest(body, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(CreateSectionActivity.this, "创建小节失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String respDataStr = getRespData(response);
                            JSONObject respDataJson = new JSONObject(respDataStr);
                            secId = respDataJson.optString("secId");
                            Log.i("secId","============"+secId);
                            Properties proper = ProperTies.getProperties(myapp.getApplicationContext());
                            String endpoint = proper.getProperty("OSS_ENDPOINT");
                            OSS oss = new OSSClient(
                                    myapp.getApplicationContext(),
                                    endpoint,
                                    myapp.getWriteOnlyOSSCredentialProvider());
                            PutObjectRequest put = new PutObjectRequest(
                                    proper.getProperty("OSS_BUCKET_NAME"),
                                    courseId + "/" +secId ,
                                    secVideoPath);
                            // 异步上传时可以设置进度回调
                            put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                                @Override
                                public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                                    Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pbUploadSec.setMax((int) totalSize);
                                            pbUploadSec.setProgress((int) currentSize);
                                        }
                                    });
                                }
                            });
                            OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                                @Override
                                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                                    Log.d("PutObject", "UploadSuccess");
                                    Log.d("ETag", result.getETag());
                                    Log.d("RequestId", result.getRequestId());
                                }
                                @Override
                                public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                                    // 请求异常
                                    if (clientExcepion != null) {
                                        // 本地异常如网络异常等
                                        clientExcepion.printStackTrace();
                                    }
                                    if (serviceException != null) {
                                        // 服务异常
                                        Log.e("ErrorCode", serviceException.getErrorCode());
                                        Log.e("RequestId", serviceException.getRequestId());
                                        Log.e("HostId", serviceException.getHostId());
                                        Log.e("RawMessage", serviceException.getRawMessage());
                                    }
                                }
                            });
                            task.waitUntilFinished(); // 可以等待任务完成
                            System.out.println("===finish()====");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //String responseData = response.body().string();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case VIDEO_REQUEST_CODE:
                if ( resultCode == RESULT_OK && null != data) {
                    Uri selectedVideo = data.getData();
                    secVideoPath = UriPathUtil.getPath(myapp.getApplicationContext(),selectedVideo);
                    jzVideoPlayerStandard.setUp(
                            secVideoPath,
                            JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL,
                            "");
                    ivCreateSection.setClickable(false);
                    ivCreateSection.setAlpha(0f);
                    ivCreateSection.setVisibility(View.GONE);

                    jzVideoPlayerStandard.setClickable(true);
                    jzVideoPlayerStandard.setVisibility(View.VISIBLE);

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
