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

package me.chaoliu.lightchaser.core.fission.archive;

import lombok.extern.slf4j.Slf4j;
import me.chaoliu.lightchaser.core.fission.BaseFission;
import me.chaoliu.lightchaser.core.fission.archive.domain.ArchiveVO;
import me.chaoliu.lightchaser.core.fission.archive.mapper.ArchiveMapper;
import me.chaoliu.lightchaser.core.wrapper.template.Wrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component("archive" + BaseFission.FISSION_BEAN_SUFFIX)
public class ArchiveFission extends BaseFission {

    @Autowired
    private ArchiveMapper archiveMapper;

    @Override
    public void fission(Map data) {

        Map info = (Map) data.get(Wrapper.INFO_KEY);
        ArchiveVO archiveVO;
        if (null != info) {
            archiveVO = ctrip(data, info, false);
            log.info("insert {} to db", data.get("url").toString());
            archiveMapper.insertArchive(archiveVO);
        } else if ((int) data.get("url_level") == 100) {
            archiveVO = ctrip(data, null, true);
            log.info("insert {} to db", data.get("url").toString());
            archiveMapper.insertArchive(archiveVO);
        }
    }

    private ArchiveVO ctrip(Map data, Map info, Boolean isFail) {

        ArchiveVO archiveVO = new ArchiveVO();

        String xpath_1 = "/html/body/div/div/div/div/div/h1/text()";
        String xpath_2 = "/html/body/div/div/div/div/div/ul/li/text()[normalize-space()]";
        String xpath_3 = "/html/body/div/div/div/div/div/div/div/a[@class='score']/text()";

        archiveVO.setPage(data.get("page").toString());
        archiveVO.setUrl(data.get("url").toString());

        if (isFail) {
            archiveVO.setXpath_error_1(xpath_1);
            archiveVO.setXpath_error_2(xpath_2);
            archiveVO.setXpath_error_3(xpath_3);
        } else {
            if (null != info.get("productName")) {
                archiveVO.setXpath_ok_1(xpath_1);
                archiveVO.setValue_1(info.get("productName").toString());
            } else {
                archiveVO.setXpath_error_1(xpath_1);
            }

            if (null != info.get("productId")) {
                archiveVO.setXpath_ok_2(xpath_2);
                archiveVO.setValue_2(info.get("productId").toString());
            } else {
                archiveVO.setXpath_error_2(xpath_2);
            }
            if (null != info.get("score")) {
                archiveVO.setXpath_ok_3(xpath_3);
                archiveVO.setValue_3(info.get("score").toString());
            } else {
                archiveVO.setXpath_error_3(xpath_3);
            }
        }

        return archiveVO;
    }

    private ArchiveVO amazon_co_jp(Map data, Map info) {

        ArchiveVO archiveVO = new ArchiveVO();

        String xpath_1 = "/html/body/div/div/div/div/div/h1/span/text()";
        String xpath_2 = "/html/body/div/div/div/div/div/table/tbody/tr[2]/td/span[1]/text()";
        String xpath_3 = "/html/body/div/div/div/div/div/table/tbody/tr/td/span[1]/text()";
        String xpath_4 = "/html/body/div/div/div/div/div/div/form/div/div/div/span[2]/text()";

        archiveVO.setPage(data.get("page").toString());
        archiveVO.setUrl(data.get("url").toString());

        if (null != info.get("productName")) {
            archiveVO.setXpath_ok_1(xpath_1);
            archiveVO.setValue_1(info.get("productName").toString());
        } else {
            archiveVO.setXpath_error_1(xpath_1);
        }

        if (null != info.get("price1")) {
            archiveVO.setXpath_ok_2(xpath_2);
            archiveVO.setValue_2(info.get("price1").toString());
        } else {
            archiveVO.setXpath_error_2(xpath_2);
        }

        if (null != info.get("price2")) {
            archiveVO.setXpath_ok_3(xpath_3);
            archiveVO.setValue_3(info.get("price2").toString());
        } else {
            archiveVO.setXpath_error_3(xpath_3);
        }

        if (null != info.get("totalPrice")) {
            archiveVO.setXpath_ok_4(xpath_4);
            archiveVO.setValue_4(info.get("totalPrice").toString());
        } else {
            archiveVO.setXpath_error_4(xpath_4);
        }
        return archiveVO;
    }

    private ArchiveVO ebay(Map data, Map info, Boolean isFail) {

        ArchiveVO archiveVO = new ArchiveVO();

        String xpath_1 = "/html/body/main/div/div/div/div/div/h3/a/span/text()";
        String xpath_2 = "/html/body/main/div/div/div/div/div/div[1]/span[1]/text()";

        archiveVO.setPage(data.get("page").toString());
        archiveVO.setUrl(data.get("url").toString());

        if (isFail) {
            archiveVO.setXpath_error_1(xpath_1);
            archiveVO.setXpath_error_2(xpath_2);
        } else {
            if (null != info.get("productName")) {
                archiveVO.setXpath_ok_1(xpath_1);
                archiveVO.setValue_1(info.get("productName").toString());
            } else {
                archiveVO.setXpath_error_1(xpath_1);
            }

            if (null != info.get("price")) {
                archiveVO.setXpath_ok_2(xpath_2);
                archiveVO.setValue_2(info.get("price").toString());
            } else {
                archiveVO.setXpath_error_2(xpath_2);
            }
        }

        return archiveVO;
    }

    @Override
    public void waitFissionFinish() {
    }
}
