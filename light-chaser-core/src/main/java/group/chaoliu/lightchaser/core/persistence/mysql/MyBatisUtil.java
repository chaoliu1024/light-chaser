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

package group.chaoliu.lightchaser.core.persistence.mysql;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;

/**
 * MyBatisUtil
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class MyBatisUtil {


    private static SqlSessionFactory sqlSessionFactory = null;

    private static final String resource = "mybatis-config.xml";

    private MyBatisUtil() {
    }

    /**
     * singleton object of sqlSessionFactory
     *
     * @return sqlSessionFactory
     */
    public static SqlSessionFactory sqlSessionFactory() {
        if (null == sqlSessionFactory) {
            synchronized (MyBatisUtil.class) {
                if (null == sqlSessionFactory) {
                    return getSqlSessionFactory();
                } else {
                    return sqlSessionFactory;
                }
            }
        } else {
            return sqlSessionFactory;
        }
    }

    private static SqlSessionFactory getSqlSessionFactory() {

        SqlSessionFactory sessionFactory = null;
        try {
            sessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader(resource));
        } catch (IOException e) {
            log.error("init mybatis SqlSessionFactory Exception {}", e);
        }
        return sessionFactory;
    }
}
