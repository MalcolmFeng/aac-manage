package com.ruoyi.common.utils.security;

import org.mindrot.jbcrypt.BCrypt;

public class BcryptUtil {
    public static String encode(String password,String salt){
        return BCrypt.hashpw(password, salt);             //对明文密码进行加密,并返回加密后的密码
    }


    public static boolean match(String password, String encodePassword){          //将明文密码跟加密后的密码进行匹配，如果一致返回true,否则返回false
        return BCrypt.checkpw(password,encodePassword);
    }
}
