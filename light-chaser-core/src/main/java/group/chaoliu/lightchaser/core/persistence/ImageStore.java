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

package group.chaoliu.lightchaser.core.persistence;

import group.chaoliu.lightchaser.core.protocol.http.BasicHttpClient;
import group.chaoliu.lightchaser.core.util.FileUtil;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * save image
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class ImageStore {

    public static String BASEPATH;

    private static final List<String> IMAGE_FORM = new ArrayList<String>() {{
        add("jpg");
        add("jpeg");
        add("gif");
        add("png");
        add("bmp");
    }};

    public static void save(InputStream input, String url) {

        String domainKey = BasicHttpClient.getDomainKey(url);
        
        if (StringUtils.isNotBlank(domainKey)) {
            String[] urlInfo = url.split("/");

            // 文件名
            String fileName = urlInfo[urlInfo.length - 1].split("\\.")[0];

            // 照片所属专辑(倒数第二个 /)
            String specialName = urlInfo[urlInfo.length - 2];

            String[] imageFileInfo = url.split("\\.");

            // 文件后缀名
            String form = imageFileInfo[imageFileInfo.length - 1];

            try {
                byte[] bytes = IOUtils.toByteArray(input);
                File file = new File(BASEPATH + File.separator + domainKey + File.separator +
                        specialName);

                if (!file.exists()) {
                    FileUtil.recursionMkDir(file);
                }

                File imageFile = new File(BASEPATH + File.separator + domainKey + File.separator +
                        specialName + File.separator + fileName + "." + form);
                if (imageFile.createNewFile()) {
                    FileOutputStream outImgStream = new FileOutputStream(imageFile);
                    outImgStream.write(bytes);
                    outImgStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isImage(String fileForm) {
        return IMAGE_FORM.contains(fileForm);
    }

    public static boolean isImageURL(String url) {
        String[] urlInfo = url.split("\\.");
        String form = urlInfo[urlInfo.length - 1];
        return isImage(form);
    }

}