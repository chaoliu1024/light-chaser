/*
 * Copyright (c) 2017, Chao Liu (chaoliu1024@gmail.com). All rights reserved.
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

package group.chaoliu.lightchaser.core;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class CtripDestination {

    public static void main(String[] args) {
        CtripDestination d = new CtripDestination();
        d.test();
    }

    public void test() {

        String s = null;
        try {
            s = FileUtils.readFileToString(new File("D:\\space-time\\light-chaser\\ctrip_destination.json"), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map d = (Map) JSON.parse(s);

        List ds = (List) d.get("Data");
        List<Map> subSets = (List) ((Map) ds.get(0)).get("SubSets");

        StringBuffer sb = new StringBuffer();

        for (Map set : subSets) {
            List<Map> cities = (List) set.get("Destination");
            for (Map city : cities) {
                String url = (String) city.get("Url");
                if (url.contains("tours")) {
                    sb.append("<entity>");
                    sb.append("<url site=\"ctrip\">");
                    sb.append(url);
                    sb.append("/ss12p1</url>");
                    sb.append("<level>50</level>");
                    sb.append("</entity>");
                }
            }
        }

        System.out.println(sb.toString());
    }
}
