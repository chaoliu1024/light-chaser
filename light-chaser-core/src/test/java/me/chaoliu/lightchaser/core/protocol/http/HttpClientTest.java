package me.chaoliu.lightchaser.core.protocol.http;

import netscape.security.Principal;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class HttpClientTest {

    private RequestMessage requestMsg;

    @Before
    public void initRequestMessage() {
        requestMsg = new RequestMessage();
        requestMsg.setURL("http://longmans1985.blog.163.com/blog/static/70605475201101424324384/");
    }

    @Test
    public void testHttpClient() {
        HttpClient httpClient = new HttpClient();
        ResponseMessage rspMsg = httpClient.GET(requestMsg);
        System.out.println(rspMsg.getBody());
    }

    @Test
    public void testHttpClientWithParam() {
        HttpClientBuilder builder = HttpClients.custom();
        CloseableHttpClient closeableHttpClient = builder.build();
        HttpClient httpClient = new HttpClient(closeableHttpClient);
        ResponseMessage rspMsg = httpClient.GET(requestMsg);
        System.out.println(rspMsg.getBody());
    }

    @Test
    public void testUsernamePasswordCredentials() {
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("user", "pwd");
        System.out.println(creds.getUserPrincipal().getName());
        System.out.println(creds.getPassword());
    }
}
