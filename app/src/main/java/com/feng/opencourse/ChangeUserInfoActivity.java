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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.feng.opencourse.entity.UserBase;
import com.feng.opencourse.entity.UserData;
import com.feng.opencourse.util.HttpUtil;
import com.feng.opencourse.util.MyApplication;
import com.feng.opencourse.util.PermissionsChecker;
import com.feng.opencourse.util.ProperTies;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.feng.opencourse.util.HttpUtil.getRespData;

public class ChangeUserInfoActivity extends AppCompatActivity {

    private UserData userData;
    private UserBase userBase;
    private String avatarPath;

    private ImageView ivAvatar;
    private EditText etNickname;
    private EditText etName;
    private EditText etPhone;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnChange;

    private MyApplication myapp;
    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Toolbar mTToolbar;
    private PermissionsChecker mPermissionsChecker; // 权限检测器
    private static final int PERMISSIONS_REQUEST_CODE = 2;
    private final int IMAGE_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_info);

        myapp = (MyApplication) getApplication();
        setSupportActionBar(mTToolbar);
        mPermissionsChecker = new PermissionsChecker(this);

        ivAvatar = (ImageView) findViewById(R.id.iv_change_user_info_avatar);
        etNickname = (EditText) findViewById(R.id.et_change_user_info_nickname);
        etName = (EditText) findViewById(R.id.et_change_user_info_realname);
        etPhone = (EditText) findViewById(R.id.et_change_user_info_phone);
        etEmail = (EditText) findViewById(R.id.et_change_user_info_email);
        etPassword = (EditText) findViewById(R.id.et_change_user_info_password);
        btnChange = (Button) findViewById(R.id.btn_change_user_info);
        btnChange.setOnClickListener(changeOnClickListener);
        mTToolbar = (Toolbar) findViewById(R.id.main_t_toolbar);

        Bundle bundle=this.getIntent().getExtras();
        userBase = (UserBase) bundle.getSerializable("userBase");
        userData = (UserData) bundle.getSerializable("userData");
        if(!userData.getNickname().isEmpty()){
            etNickname.setText(userData.getNickname());
        }
        if (!userBase.getUserName().isEmpty()){
            etName.setText(userBase.getUserName());
        }
        if (userBase.getPhone()!=0){
            etPhone.setText(String.valueOf(userBase.getPhone()));
        }
        if (!userBase.getEmail().isEmpty()){
            etEmail.setText(userBase.getEmail());
        }
       if (!userBase.getPassword().isEmpty()){
           etPassword.setText(userBase.getPassword());
       }
        ivAvatar.setClickable(true);
        ivAvatar.setOnClickListener(avatarOnClickListener);

        initData();
    }
    private void initData() {
        Properties proper = ProperTies.getProperties(myapp.getApplicationContext());
        String endpoint = proper.getProperty("OSS_ENDPOINT");
        OSS oss = new OSSClient(
                myapp.getApplicationContext(),
                endpoint,
                myapp.getReadOnlyOSSCredentialProvider());

        GetObjectRequest get = new GetObjectRequest(proper.getProperty("OSS_BUCKET_NAME"), proper.getProperty("AVATAR_KEY") + userBase.getUserId() );

        OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>(){
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功
                InputStream inputStream = result.getObjectContent();
                Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                ivAvatar.setImageBitmap(bitmap);
            }
            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
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
    View.OnClickListener avatarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            Intent intent=new Intent("android.intent.action.GET_CONTENT");
//            intent.setType("image/*");
            startActivityForResult(intent, IMAGE_REQUEST_CODE);
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        //在相册里面选择好相片之后调回到现在的这个activity中
        switch (requestCode) {
            case IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {//resultcode是setResult里面设置的code值
                    try {
                        Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        Log.i("=====5====",String.valueOf(columnIndex));
                        avatarPath = cursor.getString(columnIndex);  //获取照片路径
                        Log.i("==========path",avatarPath);
                        cursor.close();
                        Bitmap originalBitmap = BitmapFactory.decodeFile(avatarPath);
                        //Bitmap bitmap = zoomBitmap(originalBitmap,ivCourseFace.getWidth(), ivCourseFace.getHeight());
                        ivAvatar.setImageBitmap(originalBitmap);

                    } catch (Exception e) {
                        // TODO Auto-generatedcatch block
                        e.printStackTrace();
                    }
                }
                break;
            case PERMISSIONS_REQUEST_CODE:
                if ( resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
                    finish();
                }
        }
    }
    Button.OnClickListener changeOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JSONObject json = new JSONObject();
            try {
                json.put("ActionId", 102);
                json.put("JWT", myapp.getJWT());
                json.put("userId", myapp.getUserId());
                json.put("userName", etName.getText());
                json.put("phone", etPhone.getText());
                json.put("email", etEmail.getText());
                json.put("password", etPassword.getText());
                json.put("nickName", etNickname.getText());

                String req = json.toString();
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), req);
                HttpUtil.sendAsyncRequest(body, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(ChangeUserInfoActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String respDataStr = getRespData(response);
                            JSONObject respDataJson = new JSONObject(respDataStr);
//                            courseId = respDataJson.optString("courseId");


                            Properties proper = ProperTies.getProperties(myapp.getApplicationContext());
                            String endpoint = proper.getProperty("OSS_ENDPOINT");
                            OSS oss = new OSSClient(
                                    myapp.getApplicationContext(),
                                    endpoint,
                                    myapp.getWriteOnlyOSSCredentialProvider());
                            PutObjectRequest put = new PutObjectRequest(
                                    proper.getProperty("OSS_BUCKET_NAME"),
                                     proper.getProperty("AVATAR_KEY") + myapp.getUserId(),
                                    avatarPath);

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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
