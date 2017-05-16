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

package me.chaoliu.lightchaser.core.fission.ota;

import lombok.extern.slf4j.Slf4j;
import me.chaoliu.lightchaser.core.fission.BaseFission;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * TODO
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
@Component("ota" + BaseFission.FISSION_BEAN_SUFFIX)
public class OTAFission extends BaseFission {

//    SqlSession sqlSession = MyBatisUtil.sqlSessionFactory().openSession();
//
//    public void saveProductInfo(String data) {
//        log.info("save product...");
//        ProductMapper mapper = sqlSession.getMapper(ProductMapper.class);
//        ProductVO product = JSON.parseObject(data, ProductVO.class);
//        mapper.insertProduct(product);
//        sqlSession.commit();
//    }
//
//    public void savePrice(String data) {
//        log.info("save price...");
//        DatePriceMapper mapper = sqlSession.getMapper(DatePriceMapper.class);
//        List<DatePriceVO> datePrices = JSON.parseObject(data, new TypeReference<List<DatePriceVO>>() {
//        });
//        for (DatePriceVO datePrice : datePrices) {
//            mapper.insertDatePrice(datePrice);
//        }
//        sqlSession.commit();
//    }
//
//    public void fission(JSONObject data) {
//
//        String prices = data.get("prices").toString();
//        savePrice(prices);
//
//        String productId = data.get("productId").toString();
//        String destination = data.get("destination").toString();
//        String productName = data.get("productName").toString();
//
//        Map<String, Object> product = new HashMap<>();
//        product.put("productId", productId);
//        product.put("destination", destination);
//        product.put("productName", productName);
//        product.put("updateTime", new Date());
//        saveProductInfo(JSON.toJSONString(product));
//
//        sqlSession.close();
//    }

    @Override
    public void fission(Map data) {
        System.out.println(data);
    }

    @Override
    public void waitFissionFinish() {
    }
}