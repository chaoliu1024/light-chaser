package group.chaoliu.lightchaser.core.fission.ota.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OTATableMapper {

    int createOtaCalendarPriceTable(@Param("suffix") String suffix);

    int createOtaProductTable(@Param("suffix") String suffix);

    int createOtaProductGroupingTable(@Param("suffix") String suffix);

}
