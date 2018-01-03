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
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Repository
public interface ProductMapper {

    /**
     * Fetch product by id.
     *
     * @param id id
     * @return product PO
     */
    OTAProductPO fetchProductByID(int id);

    /**
     * Insert product to db.
     *
     * @param productPO product PO
     * @param suffix    suffix of table
     */
    void insertProduct(@Param("product") OTAProductPO productPO, @Param("suffix") String suffix);
}
