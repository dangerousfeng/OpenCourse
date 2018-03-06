package com.feng.opencourse.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.feng.opencourse.R;
import com.feng.opencourse.entity.Section;

import java.util.List;

/**
 * Created by Administrator on 2018/3/6.
 */

public class SectionsListViewAdapter extends BaseAdapter {

    private List<Section> secList;
    private LayoutInflater layoutInflater;
    private Context context;
    public SectionsListViewAdapter(Context context,List<Section> secList){
        this.context=context;
        this.secList = secList;
        this.layoutInflater=LayoutInflater.from(context);
    }
    /**
     * 组件集合，对应list.xml中的控件
     * @author Administrator
     */
    public final class Zujian{
        public TextView title;
    }
    @Override
    public int getCount() {
        return secList.size();
    }
    /**
     * 获得某一位置的数据
     */
    @Override
    public Object getItem(int position) {
        return secList.get(position);
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
        Zujian zujian=null;
        if(convertView==null){
            zujian=new Zujian();
            //获得组件，实例化组件
            convertView=layoutInflater.inflate(R.layout.fragment_course_section,null);

            zujian.title=(TextView)convertView.findViewById(R.id.tv_secName);

            convertView.setTag(zujian);
        }else{
            zujian=(Zujian)convertView.getTag();
        }
        //绑定数据
        zujian.title.setText(secList.get(position).getSecName());

        return convertView;
    }

}

