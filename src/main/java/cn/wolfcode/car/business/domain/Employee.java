package cn.wolfcode.car.business.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Setter
@Getter
@ToString
public class Employee {
    public static final Integer CARPACKAGE_NO = 1;//不是管理员
    public static final Integer CARPACKAGE_YES = 0;//是管理员
    /** */
    private Long id;

    /** */
    private String name;

    /** */
    private String email;

    /** */
    private Integer age;

    /** */
    private Integer admin;

    /** */
    private Integer status;

    /** */
    private Long deptId;
    //private Department department;
    /** */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date hiredate;

}