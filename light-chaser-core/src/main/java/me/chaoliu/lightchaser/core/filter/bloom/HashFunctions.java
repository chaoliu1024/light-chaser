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

package me.chaoliu.lightchaser.core.filter.bloom;

/**
 * TODO
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class HashFunctions {

    public static int hash(byte[] bytes, int k) {
        switch (k) {
            case 0:
                return RSHash(bytes);
            case 1:
                return JSHash(bytes);
            case 2:
                return ELFHash(bytes);
            case 3:
                return BKDRHash(bytes);
            case 4:
                return APHash(bytes);
            case 5:
                return DJBHash(bytes);
            case 6:
                return SDBMHash(bytes);
            case 7:
                return PJWHash(bytes);
        }
        return 0;
    }

    public static int RSHash(byte[] bytes) {
        int hash = 0;
        int magic = 63689;
        int len = bytes.length;
        for (int i = 0; i < len; i++) {
            hash = hash * magic + bytes[i];
            magic = magic * 378551;
        }
        return hash;
    }

    public static int JSHash(byte[] bytes) {
        int hash = 1315423911;
        for (int i = 0; i < bytes.length; i++) {
            hash ^= ((hash << 5) + bytes[i] + (hash >> 2));
        }
        return hash;
    }

    public static int ELFHash(byte[] bytes) {
        int hash = 0;
        int x = 0;
        int len = bytes.length;
        for (int i = 0; i < len; i++) {
            hash = (hash << 4) + bytes[i];
            if ((x = hash & 0xF0000000) != 0) {
                hash ^= (x >> 24);
                hash &= ~x;
            }
        }
        return hash;
    }

    public static int BKDRHash(byte[] bytes) {
        int seed = 131;
        int hash = 0;
        int len = bytes.length;
        for (int i = 0; i < len; i++) {
            hash = (hash * seed) + bytes[i];
        }
        return hash;
    }

    public static int APHash(byte[] bytes) {
        int hash = 0;
        int len = bytes.length;
        for (int i = 0; i < len; i++) {
            if ((i & 1) == 0) {
                hash ^= ((hash << 7) ^ bytes[i] ^ (hash >> 3));
            } else {
                hash ^= (~((hash << 11) ^ bytes[i] ^ (hash >> 5)));
            }
        }
        return hash;
    }

    public static int DJBHash(byte[] bytes) {
        int hash = 5381;
        int len = bytes.length;
        for (int i = 0; i < len; i++) {
            hash = ((hash << 5) + hash) + bytes[i];
        }
        return hash;
    }

    public static int SDBMHash(byte[] bytes) {
        int hash = 0;
        int len = bytes.length;
        for (int i = 0; i < len; i++) {
            hash = bytes[i] + (hash << 6) + (hash << 16) - hash;
        }
        return hash;
    }

    public static int PJWHash(byte[] bytes) {
        long BitsInUnsignedInt = (4 * 8);
        long ThreeQuarters = ((BitsInUnsignedInt * 3) / 4);
        long OneEighth = (BitsInUnsignedInt / 8);
        long HighBits = (long) (0xFFFFFFFF) << (BitsInUnsignedInt - OneEighth);
        int hash = 0;
        long test = 0;
        for (int i = 0; i < bytes.length; i++) {
            hash = (hash << OneEighth) + bytes[i];
            if ((test = hash & HighBits) != 0) {
                hash = (int) ((hash ^ (test >> ThreeQuarters)) & (~HighBits));
            }
        }
        return hash;
    }
}
