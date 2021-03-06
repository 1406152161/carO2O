package cn.wolfcode.car.business.service;

import cn.wolfcode.car.business.domain.CarPackageAudit;
import cn.wolfcode.car.business.query.CarPackageAuditQuery;
import cn.wolfcode.car.common.base.page.TablePageInfo;

import java.io.InputStream;
import java.util.List;

public interface ICarPackageAuditService {
    /**
     * 分页
     * @param qo
     * @return
     */
    TablePageInfo<CarPackageAudit> query(CarPackageAuditQuery qo);

    /**
     * 查单个
     * @param id
     * @return
     */
    CarPackageAudit get(Long id);


    /**
     * 保存
     * @param post
     */
    void save(CarPackageAudit post);


    /**
     * 更新
     * @param post
     */
    void update(CarPackageAudit post);

    /**
     *  批量删除
     * @param ids
     */
    void deleteBatch(String ids);

    /**
     * 查询全部
     * @return
     */
    List<CarPackageAudit> list();

    InputStream processImg(Long id);

    /**
     *更新流程id
     * @param id
     * @param instanceId
     */
    void updateInstanceId(Long id, String instanceId);

    void cancelApply(Long id);

    void audit(Long id, int auditStatus, String auditRecord);
}
