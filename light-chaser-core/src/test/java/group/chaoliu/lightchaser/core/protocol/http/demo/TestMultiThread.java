package group.chaoliu.lightchaser.core.protocol.http.demo;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class TestMultiThread {


    public static void main(String[] args) throws InterruptedException {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();

        // URIs to perform GETs on
        String[] urisToGet = {
                "http://gny.ly.com/guoneiyou/tours/2340.html",
                "http://news.youth.cn/gj/201610/t20161031_8797627.htm",
                "http://news.youth.cn/zt/tf/ynjgdz/",
                "http://news.163.com/special/00013A7D/icelandvolcano.html",
                "http://news.163.com/special/000144GG/shanxiyunchengdizhen.html",
                "http://news.163.com/special/000146MO/zhilidizhen.html",
                "http://news.youth.cn/zt/tf/nbedz/"
        };

        // create a thread for each URI
        GetThread[] threads = new GetThread[urisToGet.length];
        for (int i = 0; i < threads.length; i++) {
            HttpGet httpget = new HttpGet(urisToGet[i]);
            threads[i] = new GetThread(httpClient, httpget);
        }

        // start the threads
        for (int j = 0; j < threads.length; j++) {
            threads[j].start();
        }

        // join the threads
        for (int j = 0; j < threads.length; j++) {
            threads[j].join();
        }
    }
}
