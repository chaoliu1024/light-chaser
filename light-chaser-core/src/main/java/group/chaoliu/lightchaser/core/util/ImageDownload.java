package group.chaoliu.lightchaser.core.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownload {

    public static void main(String[] args) {
        try {
            saveUrlFile("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498221605413&di=bec3308f1dc21e31af070057ec1b2c43&imgtype=0&src=http%3A%2F%2Fimgstore.cdn.sogou.com%2Fapp%2Fa%2F100540002%2F481521.jpg", "D:\\1333.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取网络文件，转存到fileDes中，fileDes需要带文件后缀名
    public static void saveUrlFile(String fileUrl, String fileDes) throws Exception {
        File toFile = new File(fileDes);
        if (toFile.exists()) {
//          throw new Exception("file exist");
            return;
        }
        toFile.createNewFile();
        FileOutputStream outImgStream = new FileOutputStream(toFile);
        outImgStream.write(getUrlFileData(fileUrl));
        outImgStream.close();
    }

    //获取链接地址文件的byte数据
    public static byte[] getUrlFileData(String fileUrl) throws Exception {
        URL url = new URL(fileUrl);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.connect();
        InputStream cin = httpConn.getInputStream();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = cin.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        cin.close();
        byte[] fileData = outStream.toByteArray();
        outStream.close();
        return fileData;
    }

    //获取链接地址的字符数据，wichSep是否换行标记
    public static String getUrlDetail(String urlStr, boolean withSep) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.connect();
        InputStream cin = httpConn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(cin, "UTF-8"));
        StringBuffer sb = new StringBuffer();
        String rl = null;
        while ((rl = reader.readLine()) != null) {
            if (withSep) {
                sb.append(rl).append(System.getProperty("line.separator"));
            } else {
                sb.append(rl);
            }
        }
        return sb.toString();
    }

}
