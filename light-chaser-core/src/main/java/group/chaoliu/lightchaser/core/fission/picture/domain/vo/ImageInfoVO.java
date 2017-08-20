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

package group.chaoliu.lightchaser.core.fission.picture.domain.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Image Info
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Setter
@Getter
public class ImageInfoVO {

    /**
     * 图片文件名
     */
    private String fileName;

    /**
     * 图片相对路径
     */
    private String path;

    /**
     * 图片URL
     */
    private String url;

    /**
     * 抓取时间
     */
    private Date crawlTime;

    /**
     * 图片描述
     */
    private String description;

    /**
     * 图片网站标注的更新时间
     */
    private String updateTime;

    /**
     * 类别名称
     */
    private String category;

}