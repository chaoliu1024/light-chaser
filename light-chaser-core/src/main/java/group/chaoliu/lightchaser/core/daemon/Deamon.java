package group.chaoliu.lightchaser.core.daemon;

import group.chaoliu.lightchaser.common.config.Constants;
import group.chaoliu.lightchaser.core.crawl.template.Template;
import group.chaoliu.lightchaser.core.crawl.template.TemplatePathException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public abstract class Deamon {

    public void initTemplateRootPath(final Map lightChaserConfig) {
        String templatePath = lightChaserConfig.get(Constants.TEMPLATE_PATH).toString().trim();
        if (StringUtils.isNotBlank(templatePath)) {
            if (!templatePath.endsWith("\\")) {
                Template.templateRootPath = templatePath + File.separator;
            } else {
                Template.templateRootPath = templatePath;
            }
        } else {
            throw new TemplatePathException("template is null");
        }
    }

    /**
     * 进程ID
     *
     * @return 进程ID
     */
    public int pid() {
        String runtimeBean = ManagementFactory.getRuntimeMXBean().getName();
        return Integer.parseInt(runtimeBean.split("@")[0]);
    }

    /**
     * 本地MAC地址
     *
     * @return MAC地址
     */
    protected String MACAddress() {
        try {
            InetAddress inetAddr = InetAddress.getLocalHost();
            return MACAddress(inetAddr);
        } catch (UnknownHostException e) {
            log.error("local host exception {}", e);
        }
        return null;
    }

    private String MACAddress(InetAddress inetAddr) {
        byte[] mac = new byte[0];
        try {
            mac = NetworkInterface.getByInetAddress(inetAddr).getHardwareAddress();
        } catch (SocketException e) {
            log.error("MAC Address exception {}", e);
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mac.length; i++) {
            if (i != 0) {
                sb.append("-");
            }
            int temp = mac[i] & 0xff;
            String str = Integer.toHexString(temp);
            if (str.length() == 1) {
                sb.append("0").append(str);
            } else {
                sb.append(str);
            }
        }
        return sb.toString().toUpperCase();
    }
}