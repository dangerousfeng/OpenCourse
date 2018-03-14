package com.feng.opencourse.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.feng.opencourse.CourseDetailActivity;
import com.feng.opencourse.CreateCourseActivity;
import com.feng.opencourse.R;
import com.feng.opencourse.entity.Comment;
import com.feng.opencourse.util.HttpUtil;
import com.feng.opencourse.util.MyApplication;
import com.feng.opencourse.util.ProperTies;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.feng.opencourse.util.HttpUtil.getRespData;

/**
 * Created by Administrator on 2018/3/13.
 */

public class CommentListViewAdapter extends BaseAdapter {
    private List<Comment> commentList;
    private LayoutInflater layoutInflater;
    private Context context;
    private MyApplication myapp;

    public CommentListViewAdapter(Context context,List<Comment> commentList,MyApplication myapp){
        this.context=context;
        this.commentList = commentList;
        this.layoutInflater=LayoutInflater.from(context);
        this.myapp = myapp;
    }
    /**
     * 组件集合，对应list.xml中的控件
     * @author Administrator
     */
    public final class CommentItem {
        public TextView uName;
        public TextView commentTime;
        public TextView zanNum;
        public TextView content;
        public ImageView avatar;
        public ImageView zan;
    }
    @Override
    public int getCount() {
        return commentList.size();
    }
    /**
     * 获得某一位置的数据
     */
    @Override
    public Object getItem(int position) {
        return commentList.get(position);
    }
    /**
     * 获得唯一标识
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommentItem commentItem =null;
        if(convertView==null){
            commentItem =new CommentItem();
            //获得组件，实例化组件
            convertView=layoutInflater.inflate(R.layout.listview_item_comment,null);

            commentItem.uName=(TextView)convertView.findViewById(R.id.tv_comment_name);
            commentItem.commentTime=(TextView)convertView.findViewById(R.id.tv_comment_time);
            commentItem.zanNum=(TextView)convertView.findViewById(R.id.tv_zan_num);
            commentItem.content=(TextView)convertView.findViewById(R.id.tv_comment_content);
            commentItem.avatar = (ImageView) convertView.findViewById(R.id.iv_comment_avatar);
            commentItem.zan = (ImageView) convertView.findViewById(R.id.iv_zan);
            commentItem.zan.setClickable(true);
            convertView.setTag(commentItem);
        }else{
            commentItem =(CommentItem)convertView.getTag();
        }
        //绑定数据
        commentItem.uName.setText(commentList.get(position).getuName());
        commentItem.commentTime.setText(commentList.get(position).getCommentTime());
        commentItem.zanNum.setText(String.valueOf(commentList.get(position).getZanNum()));
        commentItem.content.setText(commentList.get(position).getContent());
        CommentItem finalCommentItem = commentItem;
        commentItem.zan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                try {
                    json.put("ActionId", 207);
                    json.put("JWT", myapp.getJWT());
                    json.put("userId", myapp.getUserId());
                    json.put("commentId", commentList.get(position).getCommentId());
                    String req = json.toString();
                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), req);
                    HttpUtil.sendAsyncRequest(body, new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            //todo 点赞服务端出错
                            int addZan = Integer.valueOf((String) finalCommentItem.zanNum.getText()) + 1;
                            finalCommentItem.zanNum.setText(String.valueOf(addZan));
                            commentList.get(position).setZanNum(addZan);
                            finalCommentItem.zan.setImageResource(R.drawable.zan_finish);
                            finalCommentItem.zan.setClickable(false);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        String userId = commentList.get(position).getUserId();


        Properties proper = ProperTies.getProperties(myapp.getApplicationContext());
        String endpoint = proper.getProperty("OSS_ENDPOINT");
        OSS oss = new OSSClient(
                myapp.getApplicationContext(),
                endpoint,
                myapp.getReadOnlyOSSCredentialProvider());

        GetObjectRequest get = new GetObjectRequest(proper.getProperty("OSS_BUCKET_NAME"),  proper.getProperty("AVATAR_KEY") + userId );

        OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>(){
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功
                InputStream inputStream = result.getObjectContent();
                Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                finalCommentItem.avatar.setImageBitmap(bitmap);
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
        return convertView;
    }
}
