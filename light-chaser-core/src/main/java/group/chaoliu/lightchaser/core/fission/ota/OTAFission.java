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

package group.chaoliu.lightchaser.core.fission.ota;

import com.alibaba.fastjson.JSON;
import group.chaoliu.lightchaser.core.fission.BaseFission;
import group.chaoliu.lightchaser.core.fission.ota.domain.po.OTACalendarPricePO;
import group.chaoliu.lightchaser.core.fission.ota.domain.po.OTAProductPO;
import group.chaoliu.lightchaser.core.fission.ota.domain.vo.OTACalendarPriceVO;
import group.chaoliu.lightchaser.core.fission.ota.domain.vo.OTAProductVO;
import group.chaoliu.lightchaser.core.fission.ota.mapper.CalendarPriceMapper;
import group.chaoliu.lightchaser.core.fission.ota.mapper.ProductMapper;
import group.chaoliu.lightchaser.core.wrapper.template.Wrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * OTA data fission
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
@Component("ota" + BaseFission.FISSION_BEAN_SUFFIX)
public class OTAFission extends BaseFission {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CalendarPriceMapper calendarPriceMapper;

    @Override
//    @Transactional
    public void fission(Map data) {

        String suffix = (String) data.get(Wrapper.CATEGORY_SUFFIX);

        if (!data.isEmpty() && null != data.get(Wrapper.DATA_KEY)) {
            OTAProductVO otaProductVO = JSON.parseObject(JSON.toJSONString(data.get(Wrapper.DATA_KEY)), OTAProductVO.class);
            saveProduct(otaProductVO, suffix);
            saveCalendarPricePO(otaProductVO, suffix);
        }
    }

    @Override
    public void finish() {
    }

    private void saveProduct(OTAProductVO otaProductVO, String suffix) {
        OTAProductPO product = new OTAProductPO();
        product.setProductId(otaProductVO.getProductId());
        product.setProductName(otaProductVO.getProductName());
        product.setUrl(otaProductVO.getUrl());
        product.setDepartureCity(otaProductVO.getDepartureCity());
        product.setDestination(otaProductVO.getDestination());
        product.setPrice(otaProductVO.getPrice());
        product.setProductType(1);
        product.setSite(1);
        product.setCrawlTime(otaProductVO.getCrawlTime());
        productMapper.insertProduct(product, suffix);
    }

    private void saveComment(OTAProductVO otaProductVO, String suffix) {

    }

    private void saveCalendarPricePO(OTAProductVO otaProductVO, String suffix) {
        List<OTACalendarPriceVO> calendarPrices = otaProductVO.getCalendarPrice();

        List<OTACalendarPricePO> calendarPricesPO = new ArrayList<>();

        for (OTACalendarPriceVO calendarPrice : calendarPrices) {
            OTACalendarPricePO calendarPricePO = new OTACalendarPricePO();
            calendarPricePO.setSite(1);
            calendarPricePO.setProductType(1);
            calendarPricePO.setProductId(otaProductVO.getProductId());
            calendarPricePO.setDate(calendarPrice.getDate());
            calendarPricePO.setPrice(calendarPrice.getPrice());
            calendarPricePO.setCrawlTime(otaProductVO.getCrawlTime());
            calendarPricesPO.add(calendarPricePO);
        }
        calendarPriceMapper.insertCalendarPrice(calendarPricesPO, suffix);
    }
}