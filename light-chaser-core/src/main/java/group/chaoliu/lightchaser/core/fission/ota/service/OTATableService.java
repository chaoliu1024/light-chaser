package group.chaoliu.lightchaser.core.fission.ota.service;

import group.chaoliu.lightchaser.core.fission.common.service.TableServer;
import group.chaoliu.lightchaser.core.fission.ota.mapper.OTATableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OTATableService implements TableServer {

    @Autowired
    private OTATableMapper tableMapper;

    @Override
    public void createTable(String suffix) {
        tableMapper.createOtaCalendarPriceTable(suffix);
        tableMapper.createOtaProductGroupingTable(suffix);
        tableMapper.createOtaProductTable(suffix);
    }
}
