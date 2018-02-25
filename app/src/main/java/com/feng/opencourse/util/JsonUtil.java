package com.feng.opencourse.util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Windows 7 on 2018/2/24 0024.
 */

public class JsonUtil {

    public  String convertStandardJSONString(String data_json) {
        data_json = data_json.replaceAll("\\\\r\\\\n", "");
        data_json = data_json.replace("\"{", "{");
        data_json = data_json.replace("}\",", "},");
        data_json = data_json.replace("}\"", "}");
        return data_json;
    }
}
