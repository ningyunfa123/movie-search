package com.movie.search.moviesearch.util.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class DataTrans {

    /**
     * bean转json
     * @param obj 待数据转换bean
     * @return JSONObject
     */
    public static JSONObject beanToJsonObject(Object obj){
        return JSONObject.parseObject(JSON.toJSONString(obj));
    }

    /**
     * json转bean
     * @param jObject 待数据转换json
     * @param tClass 转换类型
     * @param <T> 泛型
     * @return Object
     */
    public static <T> T jsonObjectToBean(JSONObject jObject, Class<T> tClass) {
        return JSON.parseObject(JSON.toJSONString(jObject), tClass);
    }

    /**
     * json转String
     * @param jObject 待数据转转json
     * @return String
     */
    public static String jsonObjectToString(JSONObject jObject) {
        return JSON.toJSONString(jObject);
    }

    /**
     * String转json
     * @param str 待数据转换字符串
     * @return JSONObject
     */
    public static JSONObject stringToJson(String str) {
        return JSONObject.parseObject(str);
    }
}
