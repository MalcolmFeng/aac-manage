package com.ruoyi.system.service.impl;

import com.ruoyi.system.service.IClientApproveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.mapper.*;

import java.util.List;

@Service
public class ClientApproveServiceImpl implements IClientApproveService
{
    @Autowired
    ClientApproveMapper clientApproveMapper;

    @Override
    public int insertApprove(Approve approve) {
        return clientApproveMapper.insertApprove(approve);
    }

    @Override
    public List<Approve> selectApproveList(Long user_id){
        return clientApproveMapper.selectApproveListByUserId(user_id);
    }

    @Override
    public int updateApprove(Long id, int result){
        return clientApproveMapper.updateApproveById(id, result);
    }
}
