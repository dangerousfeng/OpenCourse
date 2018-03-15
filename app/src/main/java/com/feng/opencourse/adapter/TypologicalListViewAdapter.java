package com.feng.opencourse.adapter;

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
 * Created by Administrator on 2018/3/15.
 */

public class TypologicalListViewAdapter extends BaseAdapter{
    private List<String> typologicalList;
    private LayoutInflater layoutInflater;
    private Context context;
    public TypologicalListViewAdapter(Context context,List<String> typologicalList){
        this.context=context;
        this.typologicalList = typologicalList;
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
        return typologicalList.size();
    }
    /**
     * 获得某一位置的数据
     */
    @Override
    public Object getItem(int position) {
        return typologicalList.get(position);
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
        TypologicalListViewAdapter.Zujian zujian=null;
        if(convertView==null){
            zujian=new TypologicalListViewAdapter.Zujian();
            //获得组件，实例化组件
            convertView=layoutInflater.inflate(R.layout.listview_item_typological,null);

            zujian.title=(TextView)convertView.findViewById(R.id.tv_typological_type_name);

            convertView.setTag(zujian);
        }else{
            zujian=(TypologicalListViewAdapter.Zujian)convertView.getTag();
        }
        //绑定数据
        zujian.title.setText(typologicalList.get(position));

        return convertView;
    }
}
