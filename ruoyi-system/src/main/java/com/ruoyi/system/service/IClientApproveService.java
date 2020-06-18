package com.ruoyi.system.service;

import com.ruoyi.system.domain.Approve;

import java.util.List;

public interface IClientApproveService
{
    /**
     * 新增申请
     * @param approve
     * @return
     */
    public int insertApprove(Approve approve);

    /**
     * 获取申请列表
     *
     */
    public List<Approve> selectApproveList(Long user_id);

    /**
     * 同意或拒绝申请
     */
    public int updateApprove(Long id, int result);
}
