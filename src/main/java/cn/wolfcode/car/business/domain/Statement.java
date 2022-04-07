package cn.wolfcode.car.business.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
@Getter
@Setter
@ToString
public class Statement {
    public static final Integer STATUS_CONSUME = 0;//消费中
    public static final Integer STATUS_PAID = 1;//已支付
    private static final long serialVersionUID = 1L;
    /** */
    private Long id;

    /** 客户姓名*/
    private String customerName;

    /** 客户联系方式*/
    private String customerPhone;

    /** 实际到店时间*/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date actualArrivalTime;

    /** 车牌号码*/
    private String licensePlate;

    /** 汽车类型*/
    private String carSeries;

    /** 服务类型【维修/保养】*/
    private Long serviceType;

    /** 预约单ID【通过这个来判断是否预约用户,唯一标识】*/
    private Long appointmentId;

    /** 结算状态【消费中0/已支付1】*/
    private Integer status= STATUS_CONSUME;

    /** 收款时间*/
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date payTime;

    /** 收款人id*/
    private Long payeeId;

    /** 总消费金额*/
    private BigDecimal totalAmount= new BigDecimal("0.00");

    /** 服务项数量*/
    private BigDecimal totalQuantity= new BigDecimal("0");

    /** 折扣金额*/
    private BigDecimal discountAmount= new BigDecimal("0.00");

    /** 创建时间*/
    private Date createTime;

    /** 备注信息*/
    private String info;

}