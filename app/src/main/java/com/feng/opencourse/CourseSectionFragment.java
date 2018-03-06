package com.feng.opencourse;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.feng.opencourse.Adapter.SectionsListViewAdapter;
import com.feng.opencourse.entity.Section;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class CourseSectionFragment extends Fragment {

    private ListView lv_sections;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_section,container, false);
        lv_sections = (ListView) view.findViewById(R.id.lv_sections);


        if (isAdded()) {//判断Fragment已经依附Activity
            String sectionsJsonStr = getArguments().getString("sectionsJsonStr");

            //Json的解析类对象
            JsonParser parser = new JsonParser();
            //将JSON的String 转成一个JsonArray对象
            JsonArray jsonArray = parser.parse(sectionsJsonStr).getAsJsonArray();

            Gson gson = new Gson();
            ArrayList<Section> secList = new ArrayList<>();

            //加强for循环遍历JsonArray
            for (JsonElement sec : jsonArray) {
                //使用GSON，直接转成Bean对象
                Section section = gson.fromJson(sec, Section.class);
                secList.add(section);
            }

            lv_sections.setAdapter(new SectionsListViewAdapter(getActivity(),secList));
        }


        return view;
    }
}