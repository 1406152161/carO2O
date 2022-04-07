package cn.wolfcode.car.business.query;

import cn.wolfcode.car.common.base.query.QueryObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Setter
@Getter
@ToString
public class EmployeeQuery extends QueryObject {
    private String keyword;
    private String email;
    private Integer admin;
    private Long deptId;
    @DateTimeFormat(pattern = "yyyy-MM-dd" )
    private Date beginTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
}
