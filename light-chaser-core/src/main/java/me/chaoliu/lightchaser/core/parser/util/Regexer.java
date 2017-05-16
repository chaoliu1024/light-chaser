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

package me.chaoliu.lightchaser.core.parser.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * regular expression tool to parse text
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class Regexer {

    private static final String IP_REGEX =
            "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";

    private static final String NUMBER_REGEX = "\\d+";

    /**
     * regular expression to verify parameter is ip
     *
     * @param ip ip
     */
    public boolean isIP(String ip) {
        Pattern p = Pattern.compile(IP_REGEX);
        Matcher m = p.matcher(ip);
        return m.matches();
    }

    /**
     * regular expression to verify parameter is number
     *
     * @param num number
     * @return is matched or not
     */
    public boolean isNumber(String num) {
        Pattern p = Pattern.compile(NUMBER_REGEX);
        Matcher m = p.matcher(num);
        return m.matches();
    }
}
