package com.feng.opencourse;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.feng.opencourse.util.HttpUtil;
import com.feng.opencourse.util.MyApplication;
import com.feng.opencourse.util.PermissionsChecker;
import com.feng.opencourse.util.ProperTies;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Properties;
import android.support.v7.widget.Toolbar;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.feng.opencourse.util.HttpUtil.getRespData;

public class CreateCourseActivity extends AppCompatActivity {

    private ImageView ivCourseFace;
    private Button btnCreateCourse;
    private EditText etCourseName;
    private EditText etCourseDesc;
    private Spinner spinnerType;
    private final int IMAGE_REQUEST_CODE = 1;
    private String courseFacePath = null;
    private int courseType;
    private MyApplication myapp;
    private static final int REQUEST_CODE = 0; // 请求码
    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Toolbar mTToolbar;
    private PermissionsChecker mPermissionsChecker; // 权限检测器


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);

        findViews();
        myapp = (MyApplication) getApplication();
        setSupportActionBar(mTToolbar);
        mPermissionsChecker = new PermissionsChecker(this);

        spinnerType.setOnItemSelectedListener(spinnerTypeSelectListener);
        btnCreateCourse.setOnClickListener(createOnClickListener);
        ivCourseFace.setOnClickListener(courseFaceOnClickListener);

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
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }


    void findViews(){
        ivCourseFace = (ImageView) findViewById(R.id.iv_courseFace);
        etCourseName = (EditText) findViewById(R.id.et_courseName);
        spinnerType = (Spinner) findViewById(R.id.spinner_courseType);

        etCourseDesc = (EditText) findViewById(R.id.et_courseDesc);
        etCourseDesc.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        etCourseDesc.setGravity(Gravity.TOP);
        etCourseDesc.setSingleLine(false);
        etCourseDesc.setHorizontallyScrolling(false);

        btnCreateCourse = (Button) findViewById(R.id.btn_create);
        mTToolbar = (Toolbar) findViewById(R.id.main_t_toolbar);

    }
    View.OnClickListener courseFaceOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, IMAGE_REQUEST_CODE);
        }
    };
    AdapterView.OnItemSelectedListener spinnerTypeSelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            parent.getItemIdAtPosition(position);
            String typeData = (String)spinnerType.getItemAtPosition(position);
            System.out.println("type data =="+typeData);
            courseType = 1;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            btnCreateCourse.setEnabled(false);
        }
    };
    Button.OnClickListener createOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JSONObject json = new JSONObject();
            System.out.println("======="+myapp.getUserId());
            try {
                json.put("ActionId", 200);
                json.put("JWT", myapp.getJWT());
                json.put("userId", myapp.getUserId());
                json.put("etCourseName", etCourseName.getText());
                json.put("type", courseType);
                json.put("etCourseDesc", etCourseDesc.getText());
                
                String req = json.toString();
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), req);
                HttpUtil.sendAsyncRequest(body, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(CreateCourseActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String respDataStr = getRespData(response);
                            JSONObject respDataJson = new JSONObject(respDataStr);
                            String courseId = respDataJson.optString("courseId");

                            Properties proper = ProperTies.getProperties(myapp.getApplicationContext());
                            String endpoint = proper.getProperty("OSS_ENDPOINT");
//                            OSS oss = new OSSClient(
//                                    myapp.getApplicationContext(),
//                                    endpoint,
//                                    );

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
        //在相册里面选择好相片之后调回到现在的这个activity中
        switch (requestCode) {
            case IMAGE_REQUEST_CODE://这里的requestCode是我自己设置的，就是确定返回到那个Activity的标志
                if (resultCode == RESULT_OK) {//resultcode是setResult里面设置的code值
                    try {
                        Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        courseFacePath = cursor.getString(columnIndex);  //获取照片路径
                        cursor.close();
                        Bitmap originalBitmap = BitmapFactory.decodeFile(courseFacePath);
                        //Bitmap bitmap = zoomBitmap(originalBitmap,ivCourseFace.getWidth(), ivCourseFace.getHeight());
                        ivCourseFace.setImageBitmap(originalBitmap);

                    } catch (Exception e) {
                        // TODO Auto-generatedcatch block
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_CODE:
                if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
                    finish();
                }
        }
    }
    /**
     * 图片缩放
     * @param bitmap 对象
     * @param w 要缩放的宽度
     * @param h 要缩放的高度
     * @return newBmp 新 Bitmap对象
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,matrix, true);
        return newBmp;
    }
}
