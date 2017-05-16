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

package me.chaoliu.lightchaser.core.fission.archive.domain;

import lombok.Data;

@Data
public class ArchiveVO {

    private String page;
    private String url;

    private String xpath_ok_1;
    private String xpath_ok_2;
    private String xpath_ok_3;
    private String xpath_ok_4;
    private String xpath_ok_5;
    private String xpath_ok_6;
    private String xpath_ok_7;
    private String xpath_ok_8;
    private String xpath_ok_9;
    private String xpath_ok_10;

    private String xpath_error_1;
    private String xpath_error_2;
    private String xpath_error_3;
    private String xpath_error_4;
    private String xpath_error_5;
    private String xpath_error_6;
    private String xpath_error_7;
    private String xpath_error_8;
    private String xpath_error_9;
    private String xpath_error_10;

    private String value_1;
    private String value_2;
    private String value_3;
    private String value_4;
    private String value_5;
    private String value_6;
    private String value_7;
    private String value_8;
    private String value_9;
    private String value_10;
}