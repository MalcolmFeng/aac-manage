package com.ruoyi.system.service;

import com.ruoyi.system.domain.Client;
import com.ruoyi.system.domain.SysUser;

import java.util.List;

/**
 * 租户 业务层
 * 
 * @author Malcolm
 */
public interface IClientService {
    /**
     * 根据租户Id查询租户信息
     * @param client_id
     * @return
     */
    Client getClientInfo(String client_id);

    /**
     * 根据条件分页查询用户列表
     * 
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public List<Client> selectClientList(Client user);

    /**
     * 新增租户
     * @param client
     * @return
     */
    public int insertClient(Client client);

    /**
     * 修改租户信息
     * @param client
     * @return
     */
    int updateClient(Client client);

    /**
     * 删除租户
     * @param ids
     * @return
     */
    int deleteClientByIds(String ids);
}
