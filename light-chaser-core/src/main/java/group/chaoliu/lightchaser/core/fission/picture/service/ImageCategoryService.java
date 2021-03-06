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

package group.chaoliu.lightchaser.core.fission.picture.service;

import group.chaoliu.lightchaser.core.fission.picture.domain.po.ImageCategoryPO;
import group.chaoliu.lightchaser.core.fission.picture.mapper.ImageCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Service
public class ImageCategoryService {

    @Autowired
    private ImageCategoryMapper imageCategoryMapper;

    public int insertCategory(ImageCategoryPO imageCategory) {

        ImageCategoryPO existCategory = imageCategoryMapper.fetchCategory(imageCategory);

        if (null != existCategory) {
            return existCategory.getId();
        } else {
            imageCategoryMapper.insertCategory(imageCategory);
            return imageCategoryMapper.fetchCategory(imageCategory).getId();
        }
    }

}