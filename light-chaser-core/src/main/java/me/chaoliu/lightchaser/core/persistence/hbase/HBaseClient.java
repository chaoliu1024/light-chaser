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

package me.chaoliu.lightchaser.core.persistence.hbase;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.List;

/**
 * HBase Client
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class HBaseClient {

    private Configuration conf = HBaseConfiguration.create();
    private HBaseAdmin admin = new HBaseAdmin(conf);
    private HTable table;

    public HBaseClient(String tableName) throws IOException {
        Configuration conf = HBaseConfiguration.create();
        table = new HTable(conf, tableName);
    }

    /**
     * Create HBase table.
     *
     * @param tableName table name
     * @param families  hbase column families
     * @param isForced  if is true, remove the old table and create a new one
     */
    public void createTable(String tableName, String[] families, boolean isForced) {
        HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(tableName));

        for (String f : families) {
            desc.addFamily(new HColumnDescriptor(f));
        }

        try {
            if (admin.tableExists(tableName)) {
                log.info("HBase table {} is already existed", tableName);
                if (isForced) {
                    log.info("Forced create table {} ...", tableName);
                    admin.disableTable(tableName);
                    admin.deleteTable(tableName);
                    admin.createTable(desc);
                }
            } else {
                admin.createTable(desc);
            }
        } catch (IOException e) {
            log.error("Create HBase table {} failed.", tableName);
        }
    }

    public void put(String row, String family, String qualifier, String value) {
        Put put = new Put(Bytes.toBytes(row));
        put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
        try {
            table.put(put);
        } catch (InterruptedIOException | RetriesExhaustedWithDetailsException e) {
            log.error("put {} to HBase error", value);
        }
    }

    public void batchPut(List<Put> puts) {
        try {
            table.put(puts);
        } catch (InterruptedIOException | RetriesExhaustedWithDetailsException e) {
            log.error("batch put data to HBase error");
        }
    }

    /**
     * Get data from HBase.
     *
     * @param rowKey    row key
     * @param family    family
     * @param qualifier column
     */
    public Result get(String rowKey, String family, String qualifier) {

        Result result = new Result();
        byte[] row = Bytes.toBytes(rowKey);
        byte[] fn = Bytes.toBytes(family);
        byte[] col = Bytes.toBytes(qualifier);

        Get get = new Get(row);
        get.addColumn(fn, col);

        try {
            if (table.exists(get)) {
                result = table.get(get);
                return result;
            } else {
                log.info("HBase has no data of the {} {}...", family, qualifier);
            }
        } catch (IOException e) {
            log.error("HBase get data error. {}", e);
        }
        return result;
    }

    public void scan() {
        Scan scan = new Scan();
        ResultScanner scanner;
        try {
            scanner = table.getScanner(scan);
            for (Result res : scanner) {
                System.out.println(Bytes.toString(res.getValue(Bytes.toBytes("page"), Bytes.toBytes("content"))));
            }
            scanner.close();
        } catch (IOException e) {
            log.error("HBase scan IOException {}", e);
        }
    }
}