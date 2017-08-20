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

package group.chaoliu.lightchaser.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * description
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class RegexUtil {

    /**
     * Check the IP is legal or not.
     *
     * @param IP the IP string
     * @return if legal return true
     */
    public static boolean isLegalIP(String IP) {

        String ipRegex = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";
        Pattern p = Pattern.compile(ipRegex);
        Matcher m = p.matcher(IP);
        return m.matches();
    }

    /**
     * Check if the number is the number string
     *
     * @param number number string
     * @return if number return true
     */
    public static boolean isNumber(String number) {

        String ipRegex = "\\d+";
        Pattern p = Pattern.compile(ipRegex);
        Matcher m = p.matcher(number);
        return m.matches();
    }
}
