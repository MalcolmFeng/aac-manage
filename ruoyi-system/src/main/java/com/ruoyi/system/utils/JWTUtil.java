package com.ruoyi.system.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.utils.ServletUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class JWTUtil {

    /**
     * 获取令牌超时时间
     * 单位：毫秒
     * @param token
     * @return
     */
    public static long getExp(String token){
        JSONObject tokenPayload = JWTUtil.getPayLoadJsonByJWT(token);
        return tokenPayload.getLong("exp")*1000;
    }


    /**
     * 验证令牌是否有效
     * 1.是否超时
     * 2.是否伪造
     * @param token
     * @return
     */
    public static boolean tokenVerify(String token){
        JSONObject tokenPayload = JWTUtil.getPayLoadJsonByJWT(token);
        if (System.currentTimeMillis() - tokenPayload.getLong("exp")*1000 < 0){
            // 如果令牌未超时，验签
            boolean result = SignerUtils.verify(token);
            return result;
        }
        return false;
    }


    /**
     * 获取当前登录LoginName
     * @return
     */
    public static String getUserNameByJWT(){
        String jwt = getTokenFromHttpServletRequest();
        return getUserNameByJWT(jwt);
    }
    public static String getUserNameByJWT(String jwt){
        String[] jwts = jwt.split("\\.");
        String headerJson = StringUtils.newStringUtf8(Base64.decodeBase64(jwts[0]));
        String payloadJson = StringUtils.newStringUtf8(Base64.decodeBase64(jwts[1]));
        JSONObject payload = JSON.parseObject(payloadJson);
        String username = payload.getString("loginName");
        return username;
    }


    /**
     * 获取令牌的Payload
     * @return
     */
    public static JSONObject getPayLoadJsonByJWT(){
        String jwt = getTokenFromHttpServletRequest();
        return getPayLoadJsonByJWT(jwt);
    }
    public static JSONObject getPayLoadJsonByJWT(String jwt){
        String[] jwts = jwt.split("\\.");
        String headerJson = StringUtils.newStringUtf8(Base64.decodeBase64(jwts[0]));
        String payloadJson = StringUtils.newStringUtf8(Base64.decodeBase64(jwts[1]));
        JSONObject payload = JSON.parseObject(payloadJson);
        return payload;
    }


    /**
     * 从request域获取令牌
     * @return
     */
    public static String getTokenFromHttpServletRequest(){
        HttpServletRequest request = ServletUtils.getRequest();

        return getTokenFromHttpServletRequest(request);
    }
    public static String getTokenFromHttpServletRequest(HttpServletRequest request){
        // 分别在 param、header、cookies中查询Token
        String token = request.getParameter("token");
        if (token == null) {
            token = request.getHeader("token");
        }
        if (token == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null){
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("token")) {
                        // 在cookies中找到Token之后，结束遍历
                        token = cookie.getValue();
                        break;
                    }
                }
            }
        }
        return token;
    }
}
