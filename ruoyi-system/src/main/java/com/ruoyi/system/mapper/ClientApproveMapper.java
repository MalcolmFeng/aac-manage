package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.Approve;

import java.util.List;

public interface ClientApproveMapper
{
    public int insertApprove(Approve approve);

    public List<Approve> selectApproveListByUserId(Long id);

    public int updateApproveById(Long id, int result);
}
