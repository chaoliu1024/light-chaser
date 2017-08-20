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

package group.chaoliu.lightchaser.core.fission.picture;

import com.alibaba.fastjson.JSON;
import group.chaoliu.lightchaser.core.fission.BaseFission;
import group.chaoliu.lightchaser.core.fission.picture.domain.po.ImageCategoryPO;
import group.chaoliu.lightchaser.core.fission.picture.domain.po.ImageInfoPO;
import group.chaoliu.lightchaser.core.fission.picture.domain.vo.ImageInfoVO;
import group.chaoliu.lightchaser.core.fission.picture.service.ImageCategoryService;
import group.chaoliu.lightchaser.core.fission.picture.service.ImageInfoService;
import group.chaoliu.lightchaser.core.wrapper.template.Wrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * TODO
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
@Component("picture" + BaseFission.FISSION_BEAN_SUFFIX)
public class PictureFission extends BaseFission {

    @Autowired
    private ImageCategoryService imageCategoryService;

    @Autowired
    private ImageInfoService imageInfoService;

    @Override
    public void fission(Map data) {
        if (!data.isEmpty() && null != data.get(Wrapper.DATA_KEY)) {
            ImageInfoVO imageInfoVO = JSON.parseObject(JSON.toJSONString(data.get(Wrapper.DATA_KEY)), ImageInfoVO.class);
            int categoryId = saveCategory(imageInfoVO);
            saveImage(imageInfoVO, categoryId);
        }
    }

    private void saveImage(ImageInfoVO imageInfo, int categoryId) {
        ImageInfoPO imageInfoPO = new ImageInfoPO();
        imageInfoPO.setDescription(imageInfo.getDescription());
        imageInfoPO.setFileName(imageInfo.getFileName());
        imageInfoPO.setPath(imageInfo.getPath());
        imageInfoPO.setUrl(imageInfo.getUrl());
        imageInfoPO.setUpdateTime(imageInfo.getUpdateTime());
        imageInfoPO.setCrawlTime(imageInfo.getCrawlTime());
        imageInfoPO.setCategory(categoryId);
        imageInfoService.insertImage(imageInfoPO);
    }

    private int saveCategory(ImageInfoVO imageInfo) {
        ImageCategoryPO imageCategoryPO = new ImageCategoryPO();
        imageCategoryPO.setName(imageInfo.getCategory());
        return imageCategoryService.insertCategory(imageCategoryPO);
    }

    @Override
    public void finish() {

    }
}
