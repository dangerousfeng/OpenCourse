package com.feng.opencourse;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.feng.opencourse.adapter.CoursesListViewAdapter;
import com.feng.opencourse.adapter.ViewPagerAdapter;
import com.feng.opencourse.entity.Course;
import com.feng.opencourse.entity.UserBase;
import com.feng.opencourse.entity.UserData;
import com.feng.opencourse.util.MyApplication;
import com.feng.opencourse.util.ProperTies;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import static com.feng.opencourse.util.PictureUtil.zoomBitmap;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ViewPager mViewPager;
    private TextView mTvPagerTitle;

    private List<ImageView> mImageList;//轮播的图片集合
    private String[] mImageTitles;//标题集合
    private int previousPosition = 0;//前一个被选中的position
    private List<View> mDots;//小点

    private boolean isStop = false;//线程是否停止
    private static int PAGER_TIOME = 3000;//间隔时间

    private UserData userData;
    private UserBase userBase;
    private MyApplication myapp;
    private ListView lvHomeCourses;
    private ArrayList<Course> hotCourseList;
    private ArrayList<Course> recommendCourseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        myapp = (MyApplication) getApplication();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lvHomeCourses = (ListView) findViewById(R.id.lv_home_course);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        ImageView iv_avatar = (ImageView) headerView.findViewById(R.id.iv_avatar);
        TextView tv_userName = (TextView) headerView.findViewById(R.id.tv_userName);
        System.out.println("===================="+tv_userName.getText());
        TextView tv_email = (TextView) headerView.findViewById(R.id.tv_email);

        // 全局获取jwt过来
        myapp = (MyApplication) getApplication();
        String jwt = myapp.getJWT();
        Toast.makeText(this, jwt, Toast.LENGTH_SHORT).show();

        // 登录传递
        Bundle bundle=this.getIntent().getExtras();
        String hotCoursesJsonStr = bundle.getString("hotCoursesJsonStr");
        String recommendCoursesJsonStr = bundle.getString("recommendCoursesJsonStr");
        userBase = (UserBase) bundle.getSerializable("userBase");
        userData = (UserData) bundle.getSerializable("userData");
        tv_userName.setText(userData.getNickname());
        tv_email.setText(userBase.getEmail());


        // 课程列表
        JsonParser parser = new JsonParser();
        JsonArray hotJsonArray = parser.parse(hotCoursesJsonStr).getAsJsonArray();
        JsonArray recommendJsonArray = parser.parse(recommendCoursesJsonStr).getAsJsonArray();
        Gson gson = new Gson();
        hotCourseList = new ArrayList<>();   //最热20课程
        recommendCourseList = new ArrayList<>(); //推荐4课程
        for (JsonElement cour : hotJsonArray) {
            Course course = gson.fromJson(cour, Course.class);
            hotCourseList.add(course);
        }
        for (JsonElement cour : recommendJsonArray) {
            Course course = gson.fromJson(cour, Course.class);
            recommendCourseList.add(course);
        }
        lvHomeCourses.setAdapter(new CoursesListViewAdapter(myapp.getApplicationContext(),hotCourseList,myapp));
        lvHomeCourses.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String clickCourseId = hotCourseList.get(position).getCourseId();
                Intent toCourseDetail = new Intent(myapp.getApplicationContext(), CourseDetailActivity.class);
                toCourseDetail.putExtra("courseId",clickCourseId);
                startActivity(toCourseDetail);
            }
        } );

        // 轮播标题
        initRollViewPager();
    }

    /**
     * 第一步、初始化控件
     */
    public void initRollViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mTvPagerTitle = (TextView) findViewById(R.id.tv_pager_title);
        initData();//初始化数据
        initView();//初始化View，设置适配器
        autoPlayView();//开启线程，自动播放
    }
    /**
     * 第二步、初始化数据（图片、标题、点击事件）
     */
    public void initData() {
        //初始化标题列表和图片
        mImageTitles = new String[4];
        //添加图片到图片列表里
        mImageList = new ArrayList<>();
        for (int i = 0; i < recommendCourseList.size(); i++) {
            mImageTitles[i] = recommendCourseList.get(i).getCourseName();
            ImageView iv = new ImageView(this);
            String courseId = recommendCourseList.get(i).getCourseId();

            Properties proper = ProperTies.getProperties(myapp.getApplicationContext());
            String endpoint = proper.getProperty("OSS_ENDPOINT");
            OSS oss = new OSSClient(
                    myapp.getApplicationContext(),
                    endpoint,
                    myapp.getReadOnlyOSSCredentialProvider());

            GetObjectRequest get = new GetObjectRequest(proper.getProperty("OSS_BUCKET_NAME"), courseId + proper.getProperty("FACE_KEY"));

            OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>(){
                @Override
                public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                    // 请求成功
                    InputStream inputStream = result.getObjectContent();
                    Bitmap originalBitmap= BitmapFactory.decodeStream(inputStream);
                    Bitmap bitmap = zoomBitmap(originalBitmap,iv.getWidth(), iv.getHeight());
                    iv.setImageBitmap(bitmap);
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
            iv.setId(i);//顺便给图片设置id
            iv.setOnClickListener(new HomeActivity.pagerImageOnClick());//设置图片点击事件
            mImageList.add(iv);
        }
        //添加轮播点
        LinearLayout linearLayoutDots = (LinearLayout) findViewById(R.id.lineLayout_dot);
        //其中fromResToDrawable()方法是我自定义的，目的是将资源文件转成Drawable
        mDots = addDots(linearLayoutDots,fromResToDrawable(this,R.drawable.ic_dot_normal),recommendCourseList.size());
    }

    //图片点击事件
    private class pagerImageOnClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String clickCourseId = recommendCourseList.get(v.getId()).getCourseId();
            Intent toCourseDetail = new Intent(myapp.getApplicationContext(), CourseDetailActivity.class);
            toCourseDetail.putExtra("courseId",clickCourseId);
            startActivity(toCourseDetail);
        }
    }
    /**
     *  第三步、给PagerViw设置适配器，并实现自动轮播功能
     */
    public void initView(){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(mImageList, mViewPager);
        mViewPager.setAdapter(viewPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //伪无限循环，滑到最后一张图片又从新进入第一张图片
                int newPosition = position % mImageList.size();
                // 把当前选中的点给切换了, 还有描述信息也切换
                mTvPagerTitle.setText(mImageTitles[newPosition]);//图片下面设置显示文本
                //设置轮播点
                LinearLayout.LayoutParams newDotParams = (LinearLayout.LayoutParams) mDots.get(newPosition).getLayoutParams();
                newDotParams.width = 24;
                newDotParams.height = 24;

                LinearLayout.LayoutParams oldDotParams = (LinearLayout.LayoutParams) mDots.get(previousPosition).getLayoutParams();
                oldDotParams.width = 16;
                oldDotParams.height = 16;

                // 把当前的索引赋值给前一个索引变量, 方便下一次再切换.
                previousPosition = newPosition;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setFirstLocation();
    }

    /**
     * 第四步：设置刚打开app时显示的图片和文字
     */
    private void setFirstLocation() {
        mTvPagerTitle.setText(mImageTitles[previousPosition]);
        // 把ViewPager设置为默认选中Integer.MAX_VALUE / t2，从十几亿次开始轮播图片，达到无限循环目的;
        int m = (Integer.MAX_VALUE / 2) % mImageList.size();
        int currentPosition = Integer.MAX_VALUE / 2 - m;
        mViewPager.setCurrentItem(currentPosition);
    }

    /**
     * 第五步: 设置自动播放,每隔PAGER_TIOME秒换一张图片
     */
    private void autoPlayView() {
        //自动播放图片
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isStop){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                        }
                    });
                    SystemClock.sleep(PAGER_TIOME);
                }
            }
        }).start();
    }



    /**
     * 资源图片转Drawable
     * @param context
     * @param resId
     * @return
     */
    public Drawable fromResToDrawable(Context context, int resId) {
        return context.getResources().getDrawable(resId);
    }


    /**
     * 动态添加一个点
     * @param linearLayout 添加到LinearLayout布局
     * @param backgount 设置
     * @return
     */
    public int addDot(final LinearLayout linearLayout, Drawable backgount) {
        final View dot = new View(this);
        LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dotParams.width = 16;
        dotParams.height = 16;
        dotParams.setMargins(4,0,4,0);
        dot.setLayoutParams(dotParams);
        dot.setBackground(backgount);
        dot.setId(View.generateViewId());
        linearLayout.addView(dot);
        return dot.getId();
    }

    /**
     * 添加多个轮播小点到横向线性布局
     * @param linearLayout
     * @param backgount
     * @param number
     * @return
     */
    public List<View> addDots(final LinearLayout linearLayout, Drawable backgount, int number){
        List<View> dots = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            int dotId = addDot(linearLayout,backgount);
            dots.add(findViewById(dotId));
        }
        return dots;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cellection) {
            Intent to_second = new Intent();
            to_second.setClass(HomeActivity.this,PlaySectionActivity.class);
            startActivity(to_second);

        } else if (id == R.id.nav_type) {

        } else if (id == R.id.nav_record) {
            Intent toPlayRecord = new Intent();
            toPlayRecord.setClass(HomeActivity.this,PlayRecordActivity.class);
            startActivity(toPlayRecord);
        } else if (id == R.id.nav_manage) {
            Intent toCreateCourse = new Intent();
            toCreateCourse.putExtra("teacherId",myapp.getUserId());
            toCreateCourse.setClass(HomeActivity.this,TeacherHomeActivity.class);
            startActivity(toCreateCourse);

        } else if (id == R.id.nav_user_center){

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            Intent toCourseDetail = new Intent();
            toCourseDetail.setClass(HomeActivity.this,CourseDetailActivity.class);
            startActivity(toCourseDetail);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
