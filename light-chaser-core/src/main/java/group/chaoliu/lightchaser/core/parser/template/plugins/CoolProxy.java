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

package group.chaoliu.lightchaser.core.parser.template.plugins;

import group.chaoliu.lightchaser.core.wrapper.template.Extract;
import group.chaoliu.lightchaser.core.wrapper.template.WrapperEntity;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Node;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * TODO
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class CoolProxy implements Extract {


    private static final int OFFSET = 13;

    private static final int LOWER_CASE = 97;
    private static final int UPPER_CASE = 65;

    @Override
    public List<String> extract(WrapperEntity entity, Node wrapNode) {
        List<String> results = new ArrayList<>();

        String ciphertext = entity.getText().trim();
        if (!ciphertext.contains("document.write(Base64.decode")) {
            return results;
        }
        log.debug("\ttext     : {}", ciphertext);
        results.add(decryption(ciphertext));
        return results;
    }

    public String decryption(String ciphertext) {

        String plaintext;

        String encodeBase64 = ciphertext.replaceAll("document\\.write\\(Base64\\.decode.*?\\(\"", "");
        encodeBase64 = encodeBase64.replaceAll("\".*", "");

        if (ciphertext.contains("Base64.decode(str_rot13")) {
            plaintext = plaintext(encodeBase64);
        } else {
            plaintext = base64Decode(encodeBase64);
        }

        return plaintext;
    }

    private String base64Decode(String base64) {
        byte[] decode = Base64.getDecoder().decode(base64);
        return new String(decode);
    }

    /**
     * Decode ciphertext to plaintext.
     *
     * @param ciphertext ciphertext
     * @return plaintext
     */
    public String plaintext(String ciphertext) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < ciphertext.length(); i++) {
            int a = (int) ciphertext.charAt(i);
            if (!Character.isLetter((char) a)) {
                result.append(ciphertext.charAt(i));
            } else {
                if (Character.isLowerCase(a)) {
                    char c = (char) ((a - LOWER_CASE + OFFSET) % 26 + LOWER_CASE);
                    result.append(c);
                } else if (!Character.isLowerCase(a)) {
                    char c = (char) ((a - UPPER_CASE + OFFSET) % 26 + UPPER_CASE);
                    result.append(c);
                }
            }
        }
        return base64Decode(result.toString());
    }
}
