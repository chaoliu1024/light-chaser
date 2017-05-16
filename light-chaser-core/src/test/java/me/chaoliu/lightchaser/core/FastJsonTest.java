/*
 * Copyright (c) 2016, Chao Liu (chaoliu1024@gmail.com). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.chaoliu.lightchaser.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertSame;

/**
 * test fastjson parse function
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class FastJsonTest {

    private String jsonArray = "[{\"productId\":3081107},{\"productId\":9063864},{\"productId\":9663337}]";

    private String hashMap = "{\"productId\": 3081107}";

    @Test
    public void test_JSONObject_Parse_Array() {
        Object array = JSONObject.parse(jsonArray);
        assertSame("object of JSONObject.parse() is not JSONArray.class", array.getClass(), JSONArray.class);
        System.out.println(array.getClass());
    }

    @Test
    public void test_JSONObject_Parse_Map() {
        Object map = JSONObject.parse(hashMap);
        assertSame("object of JSONObject.parse() is not JSONArray.class", map.getClass(), JSONObject.class);
        System.out.println(map.getClass());
    }

    /**
     * can not convert JSONArray to JSONObject
     */
    @Test(expected = ClassCastException.class)
    public void test_JSONArray2JSONObject() {
        Object o = JSONObject.parse(jsonArray);
        JSONObject array = (JSONObject) o;
        System.out.println(array.getClass());
    }

    /**
     * JSONArray convert to JSON is OK!
     */
    @Test
    public void test_JSONArray2JSON() {
        JSON array = (JSON) JSONObject.parse(jsonArray);
        // class com.alibaba.fastjson.JSONArray
        System.out.println(array.getClass());
    }

    /**
     * JSONObject convert to JSON is OK!
     */
    @Test
    public void test_JSONObject2JSON() {
        JSON map = (JSON) JSONObject.parse(hashMap);
        // class com.alibaba.fastjson.JSONObject
        System.out.println(map.getClass());
    }

    /**
     * java.lang.ClassCastException: com.alibaba.fastjson.JSONArray cannot be cast to com.alibaba.fastjson.JSONObject
     */
    @Test(expected = ClassCastException.class)
    public void test_JSON_Array2Object_Error() {
        JSON.parseObject(jsonArray);
    }

    /**
     * java.lang.ClassCastException: com.alibaba.fastjson.JSONArray cannot be cast to com.alibaba.fastjson.JSONObject
     */
    @Test(expected = ClassCastException.class)
    public void test_JSONObject_Array2Object_Error() {
        JSONObject.parseObject(jsonArray);
    }

    @Test
    public void test_JSONObject_Array2ArrayObject() {
        JSONArray array = JSONObject.parseArray(jsonArray);
        assertSame("object of JSON.parse() is not JSONObject.class", array.getClass(), JSONArray.class);
    }

    @Test
    public void test_JSON_Map2JSONObject() {
        Object jsonObject = JSON.parse(hashMap);
        assertSame("object of JSON.parse() is not JSONObject.class", jsonObject.getClass(), JSONObject.class);
        System.out.println(jsonObject.getClass());
    }

    @Test
    public void test_JSON_Array2JSONArray() {
        Object arrayObject = JSON.parse(jsonArray);
        assertSame("object of JSON.parse() is not JSONArray.class", arrayObject.getClass(), JSONArray.class);
        System.out.println(arrayObject.getClass());
    }
}
