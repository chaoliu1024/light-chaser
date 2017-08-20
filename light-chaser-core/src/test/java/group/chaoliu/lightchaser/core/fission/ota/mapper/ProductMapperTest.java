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

package group.chaoliu.lightchaser.core.fission.ota.mapper;

import group.chaoliu.lightchaser.core.fission.ota.domain.po.OTAProductPO;
import group.chaoliu.lightchaser.core.persistence.mysql.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.Date;

/**
 * TODO
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class ProductMapperTest {

    @Test
    public void testInsertProduct() {

        OTAProductPO product = new OTAProductPO();
        product.setDepartureCity("南京");
        product.setProductId("3212532s11");
        product.setProductName("广西桂林12日游");
        product.setCrawlTime(new Date());

        SqlSession sqlSession = MyBatisUtil.sqlSessionFactory().openSession();
        ProductMapper mapper = sqlSession.getMapper(ProductMapper.class);
        mapper.insertProduct(product);
        sqlSession.commit();
        sqlSession.close();
    }
}