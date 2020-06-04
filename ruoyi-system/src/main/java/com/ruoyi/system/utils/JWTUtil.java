package com.ruoyi.system.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import java.util.Date;

public class JWTUtil {

    public static String SECRET = "inspurhealth";
    public static int EXPIRE_TIME= 60*60*1000;
    public static final Algorithm ALGORITHM= Algorithm.HMAC256("$2a$10$qoeNogET3slC24x24FdrNO");
    public static final JWTVerifier JWT_VERIFIER= JWT.require(ALGORITHM).withIssuer(SECRET).build();


    public static String encode(String username) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        //通过秘钥生成一个算法
        String token = JWT.create()
                //设置签发者
                .withIssuer(SECRET)
                //设置过期时间为一个小时
                .withExpiresAt(date)
                //设置用户信息
                .withClaim("username",username)
                .withClaim("age",20)
                .sign(ALGORITHM);
        return token;
    }

    public static DecodedJWT decode(String token) {
        try {

            //如果校验失败会抛出异常
            //payload可从decodeJWT中获取
            DecodedJWT decodedJWT = JWT_VERIFIER.verify(token);
            return decodedJWT;
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 在jwt中解析userName
     * @param jwt
     * @return
     */
    public static String getUserNameByJWT(String jwt){
        String[] jwts = jwt.split("\\.");
        String headerJson = StringUtils.newStringUtf8(Base64.decodeBase64(jwts[0]));
        String payloadJson = StringUtils.newStringUtf8(Base64.decodeBase64(jwts[1]));
        JSONObject payload = JSON.parseObject(payloadJson);
        String username = payload.getString("user_name");
        return username;
    }

    public static JSONObject getPayLoadJsonByJWT(String jwt){
        String[] jwts = jwt.split("\\.");
        String headerJson = StringUtils.newStringUtf8(Base64.decodeBase64(jwts[0]));
        String payloadJson = StringUtils.newStringUtf8(Base64.decodeBase64(jwts[1]));
        JSONObject payload = JSON.parseObject(payloadJson);
        return payload;
    }

    /**
     * 测试
     * @param args
     */
    public static void main(String[] args) {
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsaWNlbnNlIjoiZW50ZnJtIiwidXNlcl9uYW1lIjoiYWRtaW4wMDIiLCJzY29wZSI6WyJhbGwiXSwiZXhwIjoxNTkwODMxNDU1LCJhdXRob3JpdGllcyI6WyJST0xFX3NzbyJdLCJqdGkiOiI0NGM3MjNlZS00ODAwLTQ0NWItYWEwNi0yYmZlZTVlMWMzYzEiLCJjbGllbnRfaWQiOiJ0ZW5jZW50In0.BMhime4zyoR4B3TPWCahHtaf5axjW4N7EwJsnC1KFsE";
        String[] jwts = jwt.split("\\.");
        String headerJson = StringUtils.newStringUtf8(Base64.decodeBase64(jwts[0]));
        String payloadJson = StringUtils.newStringUtf8(Base64.decodeBase64(jwts[1]));
        JSONObject payload = JSON.parseObject(payloadJson);
        String username = payload.getString("user_name");
        System.out.println(username);

    }
}
