package com.feng.opencourse;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.feng.opencourse.adapter.SectionsListViewAdapter;
import com.feng.opencourse.entity.Section;
import com.feng.opencourse.util.MyApplication;
import com.feng.opencourse.util.PermissionsChecker;
import com.feng.opencourse.util.ProperTies;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import cn.jzvd.JZVideoPlayerStandard;


public class CourseSectionFragment extends Fragment {

    private ListView lv_sections;
    private FloatingActionButton fabCreateSec;
    private JZVideoPlayerStandard jzVideoPlayerStandard;
    private String courseId;
    private ArrayList<Section> secList;
    private MyApplication myapp;

    private File mTmpFile = null;
    private File url;

    private static final int PERMISSIONS_REQUEST_CODE = 2;
    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };
    private PermissionsChecker mPermissionsChecker; // 权限检测器
    private Activity myactivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        myactivity = (CourseDetailActivity) activity;
        courseId = ((CourseDetailActivity) activity).getCourseId();//通过强转成宿主activity，就可以获取到传递过来的数据
        myapp = ((CourseDetailActivity) activity).getMyapp();
        jzVideoPlayerStandard = (JZVideoPlayerStandard) getActivity().findViewById(R.id.vp_face);
        url = activity.getCacheDir();
        mPermissionsChecker = new PermissionsChecker(myapp.getApplicationContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_section,container, false);
        lv_sections = (ListView) view.findViewById(R.id.lv_sections);
        fabCreateSec = (FloatingActionButton) view.findViewById(R.id.fab_create_section);
        fabCreateSec.setOnClickListener(toCreateSecListener);

        if (isAdded()) {//判断Fragment已经依附Activity
            String sectionsJsonStr = getArguments().getString("sectionsJsonStr");

            //Json的解析类对象
            JsonParser parser = new JsonParser();
            //将JSON的String 转成一个JsonArray对象
            JsonArray jsonArray = parser.parse(sectionsJsonStr).getAsJsonArray();

            Gson gson = new Gson();
            secList = new ArrayList<>();

            //加强for循环遍历JsonArray
            for (JsonElement sec : jsonArray) {
                //使用GSON，直接转成Bean对象
                Section section = gson.fromJson(sec, Section.class);
                secList.add(section);
            }

            lv_sections.setAdapter(new SectionsListViewAdapter(getActivity(),secList));
            lv_sections.setOnItemClickListener(playSecListener);
        }


        return view;
    }
    View.OnClickListener toCreateSecListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.putExtra("courseId",courseId);
            intent.setClass(myapp.getApplicationContext(),CreateSectionActivity.class);
            startActivity(intent);
        }
    };
    AdapterView.OnItemClickListener playSecListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int clickSectionId = secList.get(position).getSectionId();
            String clickSecName = secList.get(position).getSecName();

            Properties proper = ProperTies.getProperties(myapp.getApplicationContext());

            // TODO 通过只读权限读取到io流inputstream进行缓存并播放
//            String endpoint = proper.getProperty("OSS_ENDPOINT");
//            OSS oss = new OSSClient(
//                    myapp.getApplicationContext(),
//                    endpoint,
//                    myapp.getReadOnlyOSSCredentialProvider());
//            Log.i("click===========",courseId + "/" + String.valueOf(clickSectionId));
//            GetObjectRequest get = new GetObjectRequest(proper.getProperty("OSS_BUCKET_NAME"), courseId + "/" + String.valueOf(clickSectionId));
//
//            get.setProgressListener(new OSSProgressCallback<GetObjectRequest>() {
//                @Override
//                public void onProgress(GetObjectRequest request, long currentSize, long totalSize) {
//
//                    //Log.i("progress=====","getobj_progress: " + currentSize+"  total_size: " + totalSize);
//                }
//            });
//
//            OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>(){
//                @Override
//                public void onSuccess(GetObjectRequest request, GetObjectResult result) {
//                    // 请求成功
//                    InputStream inputStream = result.getObjectContent();
//                    //String tempPath = play(inputStream);
//
//                }
//
//                @Override
//                public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
//                    // 请求异常
//                    if (clientExcepion != null) {
//                        // 本地异常如网络异常等
//                        clientExcepion.printStackTrace();
//                    }
//                    if (serviceException != null) {
//                        // 服务异常
//                        Log.e("ErrorCode", serviceException.getErrorCode());
//                        Log.e("RequestId", serviceException.getRequestId());
//                        Log.e("HostId", serviceException.getHostId());
//                        Log.e("RawMessage", serviceException.getRawMessage());
//                    }
//                }
//            });
//            task.waitUntilFinished();
            // 没有只读权限验证 直接读取
            String reqURL = proper.getProperty("MOOC_PATH")  + courseId + "/" + String.valueOf(clickSectionId);
            jzVideoPlayerStandard.setUp(
                    reqURL,
                    JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL,
                    ""
            );
        }
    };
    @Override
    public void onDestroy() {
        if (mTmpFile != null) {
            mTmpFile.delete();
        }
        super.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }
    }
    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(myactivity, PERMISSIONS_REQUEST_CODE, PERMISSIONS);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if ( resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
                    myactivity.finish();
                }
        }

        }
    private String play( InputStream stream) {
        String tempPath = "";
        try {
            File temp = File.createTempFile("mediaplayertmp", ".mp4");
            tempPath = temp.getAbsolutePath();
            FileOutputStream out = new FileOutputStream(temp);
            //用BufferdOutputStream速度快
            BufferedOutputStream bis = new BufferedOutputStream(out);
            byte buf[] = new byte[128];
            do {
                int numread = stream.read(buf);
                if (numread <= 0)
                    break;
                bis.write(buf, 0, numread);
            } while (true);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempPath;
    }

}