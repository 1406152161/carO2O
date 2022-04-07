package cn.wolfcode.car.business.query;

import cn.wolfcode.car.common.base.query.QueryObject;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Setter
@Getter
@ToString
public class CarPackageAuditQuery extends QueryObject {
    private Long auditorId;
    private Integer status;
    private String auditRecord;
    @DateTimeFormat(pattern = "yyyy-MM-dd" )
    private Date beginTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
}
