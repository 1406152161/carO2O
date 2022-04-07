package cn.wolfcode.car.business.query;

import cn.wolfcode.car.common.base.query.QueryObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class StatementQuery extends QueryObject {

    private String name;
    private String phone;
    private Character status;
}
