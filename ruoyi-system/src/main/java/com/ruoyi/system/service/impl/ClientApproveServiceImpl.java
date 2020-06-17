package com.ruoyi.system.service.impl;

import com.ruoyi.system.service.IClientApproveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.mapper.*;

@Service
public class ClientApproveServiceImpl implements IClientApproveService
{
    @Autowired
    ClientApproveMapper clientApproveMapper;

    @Override
    public int insertApprove(Approve approve) {
        return clientApproveMapper.insertApprove(approve);
    }
}
