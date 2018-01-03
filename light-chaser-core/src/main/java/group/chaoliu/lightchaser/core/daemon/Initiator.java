package group.chaoliu.lightchaser.core.daemon;

import group.chaoliu.lightchaser.common.Category;
import group.chaoliu.lightchaser.common.config.Constants;
import group.chaoliu.lightchaser.core.fission.common.service.SiteService;
import group.chaoliu.lightchaser.core.fission.common.service.TableServer;
import group.chaoliu.lightchaser.core.persistence.ImageStore;
import group.chaoliu.lightchaser.core.util.SpringBeanUtil;
import group.chaoliu.lightchaser.hbase.HBaseClient;
import group.chaoliu.lightchaser.hbase.StarJobFamily;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.Map;

/**
 * 初始化一个Job，目前采用开启一个进程的方式，初始化redis队列、hbase表、MySql table。
 * 应该有更好的方式实现，例如利用Star进行通信TCP或HTTP初始化，不过要新开端口了。
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class Initiator {

    private SiteService siteService = SpringBeanUtil.siteServiceBean();

    private TableServer tableService = SpringBeanUtil.otaTableServerBean();

    /**
     * 初始化job的相关信息
     */
    public void init(Category category, Map config) {
        log.info("Start {} job...", category.getType());
        if ((boolean) config.get(Constants.STORE_WEB_PAGE)) {
            initHBase(category);
        }

        initRedis(category);

        ImageStore.BASEPATH = config.get(Constants.IMAGE_BASEPATH).toString();

        if ((boolean) config.get(Constants.STORE_MYSQL)) {
            initMySqlTable(category);
            siteService.siteLog(category);
        }
    }

    public void initHBase(Category category) {
        HBaseClient hbase;
        String tableName = category.getType() + "_" + category.getName();
        try {
            System.setProperty("hadoop.home.dir", "d:\\hadoop_home");
            hbase = new HBaseClient(tableName);
            hbase.createTable(tableName, StarJobFamily.HBASE_FAMILIES, false);
        } catch (IOException e) {
            log.error("create HBase table {} error! error info: {}", tableName, e);
        }
    }

    public void initMySqlTable(Category category) {
        String suffix = category.getSuffix();
        if (StringUtils.isNotBlank(suffix)) {
            tableService.createTable(suffix);
        }
    }

    public void initRedis(Category category) {

    }

    public static void main(String[] args) {
        Category category = new Category("diy", "ctrip");
        Initiator initiator = new Initiator();
        initiator.initMySqlTable(category);
    }

}