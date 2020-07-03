package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.*;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.IClientService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 租户 业务层处理
 * 
 * @author Malcolm
 */
@Service
public class ClientServiceImpl implements IClientService {

    private static final Logger log = LoggerFactory.getLogger(ClientServiceImpl.class);

    @Autowired
    OauthClientDetailsMapper clientDetailsMapper;

    /**
     * 根据租户id查询租户信息
     * @param client_id
     * @return
     */
    @Override
    public Client getClientInfo(String client_id){
        return clientDetailsMapper.getClientInfo(client_id);
    }

    /**
     * 根据条件分页查询用户列表
     * 
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<Client> selectClientList(Client user) {
        return clientDetailsMapper.selectClientList(user);
    }

    @Override
    public int insertClient(Client client) {
        client.setScope("all,read,write");
        client.setAuthorized_grant_types("authorization_code,refresh_code");
        client.setAuthorities("ROLE_TRUSTED_CLIENT");
        client.setAccess_token_validity(3600);
        client.setRefresh_token_validity(3600);
        return clientDetailsMapper.insertClient(client);
    }

    @Override
    public int updateClient(Client client) {
        return clientDetailsMapper.updateClient(client);
    }

    @Override
    public int updateClientStatus(Client client) {
        return clientDetailsMapper.updateClientStatus(client);
    }

    @Override
    public int deleteClientByIds(String clientId) {
        return clientDetailsMapper.deleteClientByIds(clientId);
    }
}
