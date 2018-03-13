package com.feng.opencourse;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.feng.opencourse.adapter.CommentListViewAdapter;
import com.feng.opencourse.adapter.CoursesListViewAdapter;
import com.feng.opencourse.adapter.SectionsListViewAdapter;
import com.feng.opencourse.entity.Comment;
import com.feng.opencourse.entity.Course;
import com.feng.opencourse.entity.Section;
import com.feng.opencourse.util.MyApplication;
import com.feng.opencourse.util.PermissionsChecker;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cn.jzvd.JZVideoPlayerStandard;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.feng.opencourse.util.HttpUtil.getRespData;


public class CourseCommentFragment extends Fragment {
    private ListView lv_comments;
    private EditText etComment;
    private Button btnCreateComment;
    private ArrayList<Comment> commentList;
    private MyApplication myapp;
    private Activity myactivity;
    private String courseId;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        myactivity = (CourseDetailActivity) activity;
        courseId = ((CourseDetailActivity) activity).getCourseId();//通过强转成宿主activity，就可以获取到传递过来的数据
        myapp = ((CourseDetailActivity) activity).getMyapp();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_course_comment,container, false);
        lv_comments = (ListView) view.findViewById(R.id.lv_comments);
        etComment = (EditText) view.findViewById(R.id.et_comment);
        btnCreateComment = (Button) view.findViewById(R.id.btn_create_comment);
        btnCreateComment.setOnClickListener(createCommentListener);

        if (isAdded()) {//判断Fragment已经依附Activity
            String commentsJsonStr = getArguments().getString("commentsJsonStr");
            //Json的解析类对象
            JsonParser parser = new JsonParser();
            //将JSON的String 转成一个JsonArray对象
            JsonArray jsonArray = parser.parse(commentsJsonStr).getAsJsonArray();
            Gson gson = new Gson();
            commentList = new ArrayList<>();
            //加强for循环遍历JsonArray
            for (JsonElement comm : jsonArray) {
                //使用GSON，直接转成Bean对象
                Comment comment = gson.fromJson(comm, Comment.class);
                commentList.add(comment);
            }
            lv_comments.setAdapter(new CommentListViewAdapter(getActivity(),commentList,myapp));
        }
        return view;
    }
    Button.OnClickListener createCommentListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            String newCommentContent = etComment.getText().toString();
            if (newCommentContent.isEmpty()){
                Toast.makeText(myapp.getApplicationContext(), "please input comment!", Toast.LENGTH_SHORT).show();
            }else {
                JSONObject json = new JSONObject();
                try {
                    json.put("ActionId", 205);
                    json.put("JWT", myapp.getJWT());
                    json.put("userId", myapp.getUserId());
                    json.put("courseId",courseId);
                    json.put("content",newCommentContent);
                    String req = json.toString();
                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), req);
                    com.feng.opencourse.util.HttpUtil.sendAsyncRequest(body, new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Toast.makeText(myapp.getApplicationContext(), "comment failed", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            myactivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(myapp.getApplicationContext(), "comment success", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}