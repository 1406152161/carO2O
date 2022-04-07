package cn.wolfcode.car.business.service;

import cn.wolfcode.car.business.domain.ServiceItem;
import cn.wolfcode.car.business.query.ServiceItemQuery;
import cn.wolfcode.car.common.base.page.TablePageInfo;

import java.util.List;

public interface IServiceItemService {
    /**
     * 分页
     * @param qo
     * @return
     */
    TablePageInfo<ServiceItem> query(ServiceItemQuery qo);

    /**
     * 查单个
     * @param id
     * @return
     */
    ServiceItem get(Long id);


    /**
     * 保存
     * @param post
     */
    void save(ServiceItem post);


    /**
     * 更新
     * @param post
     */
    void update(ServiceItem post);

    /**
     *  批量删除
     * @param ids
     */
    void deleteBatch(String ids);

    /**
     * 查询全部
     * @return
     */
    List<ServiceItem> list();

    void saleOff(Long id);

    void saleOn(Long id);

    void startAudit(Long id, Long bpmnInfoId, Long director, Long finance, String info);

    void changeAuditStatus(Long id, Integer auditStatus);
}
