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

package group.chaoliu.lightchaser.rpc.netty.proxy;

import lombok.Getter;

import java.util.concurrent.*;

/**
 * 同步写，利用SyncWriter.syncWriteKey，暂存异步返回的结果，然后map.get()获得结果
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class SyncWriteFuture<T> {

    // 请求Id
    @Getter
    private final String reqId;

    @Getter
    private T msg;

    private CountDownLatch latch = new CountDownLatch(1);

    private boolean isSuccess;
    private Throwable cause;
    private boolean isTimeout = false;
    private long timeout;
    private final long begin = System.currentTimeMillis();

    public SyncWriteFuture(String reqId) {
        this.reqId = reqId;
    }

    public SyncWriteFuture(String reqId, long timeout) {
        this.reqId = reqId;
        this.timeout = timeout;
        isSuccess = true;
        isTimeout = false;
    }

    public Throwable cause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public boolean isWriteSuccess() {
        return isSuccess;
    }

    public void setWriteResult(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public void setProxyMsg(T msg) {
        this.msg = msg;
        latch.countDown();
    }

    public T get() throws InterruptedException, ExecutionException {
        latch.wait();
        return this.msg;
    }

    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (latch.await(timeout, unit)) {
            return this.msg;
        }
        return null;
    }

    public boolean isTimeout() {
        if (isTimeout) {
            return isTimeout;
        }
        return System.currentTimeMillis() - begin > timeout;
    }
}
