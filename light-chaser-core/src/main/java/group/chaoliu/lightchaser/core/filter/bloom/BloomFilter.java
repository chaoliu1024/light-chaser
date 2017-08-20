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

package group.chaoliu.lightchaser.core.filter.bloom;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * TODO
 * 分布式环境，需要放在公共池中，例如Redis
 * http://sobuhu.com/algorithm/2013/03/04/java-bloom-filter.html
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class BloomFilter {

    private BitSet bitSet;
    private int bitSetSize;
    private int addedElements;
    private int hashFunctionNumber;

    /**
     * 构造一个布隆过滤器，过滤器的容量为c * n 个bit.
     *
     * @param c 当前过滤器预先开辟的最大包含记录,通常要比预计存入的记录多一倍.
     * @param n 当前过滤器预计所要包含的记录.
     * @param k 哈希函数的个数，等同每条记录要占用的bit数.
     */
    public BloomFilter(int c, int n, int k) {
        this.hashFunctionNumber = k;
        this.bitSetSize = (int) Math.ceil(c * k);
        this.addedElements = n;
        this.bitSet = new BitSet(this.bitSetSize);
    }

    /**
     * 通过文件初始化过滤器.
     *
     * @param file
     */
    public void init(String file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null && line.length() > 0) {
                this.put(line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void put(String str) {
        int[] positions = createHashes(str.getBytes(), hashFunctionNumber);
        for (int i = 0; i < positions.length; i++) {
            int position = Math.abs(positions[i] % bitSetSize);
            bitSet.set(position, true);
        }
    }

    public boolean contains(String str) {
        byte[] bytes = str.getBytes();
        int[] positions = createHashes(bytes, hashFunctionNumber);
        for (int i : positions) {
            int position = Math.abs(i % bitSetSize);
            if (!bitSet.get(position)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 得到当前过滤器的错误率.
     *
     * @return
     */
    public double getFalsePositiveProbability() {
        // (1 - e^(-k * n / m)) ^ k
        return Math.pow((1 - Math.exp(-hashFunctionNumber * (double) addedElements / bitSetSize)),
                hashFunctionNumber);
    }

    /**
     * 将字符串的字节表示进行多哈希编码.
     *
     * @param bytes      待添加进过滤器的字符串字节表示.
     * @param hashNumber 要经过的哈希个数.
     * @return 各个哈希的结果数组.
     */
    public static int[] createHashes(byte[] bytes, int hashNumber) {
        int[] result = new int[hashNumber];
        int k = 0;
        while (k < hashNumber) {
            result[k] = HashFunctions.hash(bytes, k);
            k++;
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        BloomFilter bloomfilter = new BloomFilter(30000000, 10000000, 8);
        System.out.println("Bloom Filter Initialize ... ");
        bloomfilter.init("data/base.txt");
        System.out.println("Bloom Filter Ready");
        System.out.println("False Positive Probability : "
                + bloomfilter.getFalsePositiveProbability());
        // 查找新数据
        List<String> result = new ArrayList<String>();
        long t1 = System.currentTimeMillis();
        BufferedReader reader = new BufferedReader(new FileReader("data/input.txt"));
        String line = reader.readLine();
        while (line != null && line.length() > 0) {
            if (!bloomfilter.contains(line)) {
                result.add(line);
            }
            line = reader.readLine();
        }
        reader.close();
        long t2 = System.currentTimeMillis();
        System.out.println("Parse 9900000 items, Time : " + (t2 - t1) + "ms , find "
                + result.size() + " new items.");
        System.out.println("Average : " + 9900000 / ((t2 - t1) / 1000) + " items/second");
    }
}