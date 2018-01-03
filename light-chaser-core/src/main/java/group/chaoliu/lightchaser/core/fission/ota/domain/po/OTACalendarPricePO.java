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

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Calendar Price PC
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Setter
@Getter
public class OTACalendarPricePO {

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
     * 预订地
     */
    private String bookCity;

    /**
     * 团期
     */
    private Date date;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 抓取时间
     */
    private Date crawlTime;

    @Override
    public String toString() {
        return "OTACalendarPricePO{id=" + id + ", site=" + site + ", productType=" + productType +
                ", productId='" + productId + '\'' + ", bookCity='" + bookCity + '\'' +
                ", date=" + date + ", price=" + price + ", crawlTime=" + crawlTime + "}";
    }

}