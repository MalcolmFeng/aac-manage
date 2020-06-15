package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.Client;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租户表 数据层
 * 
 * @author Malcolm
 */
public interface OauthClientDetailsMapper {

    /**
     * 根据租户id查询租户信息
     * @param client_id
     * @return
     */
    Client getClientInfo(@Param("client_id") String client_id);

    /**
     * 新增租户
     * @param oauthClientDetails
     * @return
     */
    int insertClient(Client oauthClientDetails);

    /**
     * 查询租户列表
     * @param user
     * @return
     */
    List<Client> selectClientList(Client user);

    /**
     * 修改租户信息
     * @param client
     * @return
     */
    int updateClient(Client client);

    /**
     * 通过租户Id删除租户
     * @param clientId
     * @return
     */
    int deleteClientByIds(String clientId);
}
