package group.chaoliu.lightchaser.core.fission.common.service;

public interface TableServer {

    /**
     * 按自定义的后缀名创建表
     *
     * @param suffix 后缀名
     */
    void createTable(String suffix);
}
