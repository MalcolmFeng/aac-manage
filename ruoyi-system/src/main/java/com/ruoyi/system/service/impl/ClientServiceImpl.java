package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.*;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.IClientService;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.crypto.hash.Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * @param client 租户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<Client> selectClientList(Client client) {
        if (client == null){
            client = new Client();

            Map<String,Object> params = new HashMap<>();
            params.put("beginTime","");
            params.put("endTime","");

            client.setParams(params);
        }

        return clientDetailsMapper.selectClientList(client);
    }

    @Override
    public int insertClient(Client client) {
        client.setScope("all,read,write");
        client.setAuthorized_grant_types("authorization_code,password,refresh_token,implicit,client_credentials");
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
        String[] ids = null;
        if (clientId.contains(",")){
            ids = clientId.split(",");
        }
        for (int i = 0;i<ids.length; i++){
            clientDetailsMapper.deleteClientByIds(ids[i]);
        }
        return 1;
    }
}
