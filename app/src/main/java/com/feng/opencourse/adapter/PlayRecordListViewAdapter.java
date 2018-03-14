package com.feng.opencourse.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.feng.opencourse.R;
import com.feng.opencourse.entity.Course;
import com.feng.opencourse.entity.PlayRecord;
import com.feng.opencourse.util.MyApplication;
import com.feng.opencourse.util.ProperTies;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Created by Administrator on 2018/3/14.
 */

public class PlayRecordListViewAdapter extends BaseAdapter{
    private List<PlayRecord> playRecordList;
    private LayoutInflater layoutInflater;
    private Context context;
    private MyApplication myapp;
    public PlayRecordListViewAdapter(Context context,List<PlayRecord> playRecordList,MyApplication myapp){
        this.context=context;
        this.playRecordList = playRecordList;
        this.layoutInflater=LayoutInflater.from(context);
        this.myapp = myapp;
    }
    /**
     * 组件集合，对应list.xml中的控件
     * @author Administrator
     */
    public final class RecordItem {
        public TextView title;
        public TextView recordTime;
        public ImageView face;
    }
    @Override
    public int getCount() {
        return playRecordList.size();
    }
    /**
     * 获得某一位置的数据
     */
    @Override
    public Object getItem(int position) {
        return playRecordList.get(position);
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
        PlayRecordListViewAdapter.RecordItem recordItem =null;
        if(convertView==null){
            recordItem =new PlayRecordListViewAdapter.RecordItem();
            //获得组件，实例化组件
            convertView=layoutInflater.inflate(R.layout.listview_item_course,null);

            recordItem.recordTime = (TextView) convertView.findViewById(R.id.tv_item_record_time);
            recordItem.title=(TextView)convertView.findViewById(R.id.tv_item_record_course_name);
            recordItem.face = (ImageView) convertView.findViewById(R.id.iv_item_record_course_face);
            convertView.setTag(recordItem);
        }else{
            recordItem =(PlayRecordListViewAdapter.RecordItem)convertView.getTag();
        }
        //绑定数据
        recordItem.title.setText(playRecordList.get(position).getCourseName());
        recordItem.recordTime.setText(playRecordList.get(position).getPlayTime());
        String courseId = playRecordList.get(position).getCourseId();

        Properties proper = ProperTies.getProperties(myapp.getApplicationContext());
        String endpoint = proper.getProperty("OSS_ENDPOINT");
        OSS oss = new OSSClient(
                myapp.getApplicationContext(),
                endpoint,
                myapp.getReadOnlyOSSCredentialProvider());

        GetObjectRequest get = new GetObjectRequest(proper.getProperty("OSS_BUCKET_NAME"), courseId + proper.getProperty("FACE_KEY"));

        PlayRecordListViewAdapter.RecordItem finalRecordItem = recordItem;
        OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>(){
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功
                InputStream inputStream = result.getObjectContent();
                Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                finalRecordItem.face.setImageBitmap(bitmap);
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
