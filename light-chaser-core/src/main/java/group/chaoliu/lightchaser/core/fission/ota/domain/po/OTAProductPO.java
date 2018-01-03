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

package group.chaoliu.lightchaser.core.fission.ota.domain.po;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * product
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Data
public class OTAProductPO {

    /**
     * 主键
     */
    private int id;

    /**
     * 站点,关联site.id
     */
    private int site;

    /**
     * 产品类型,关联ota_product_type.id
     */
    private int productType;

    /**
     * 产品id
     */
    private String productId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品url
     */
    private String url;

    /**
     * 出发地
     */
    private String departureCity;

    /**
     * 目的地
     */
    private String destination;

    /**
     * 产品起价
     */
    private BigDecimal price;

    /**
     * 抓取时间
     */
    private Date crawlTime;

}