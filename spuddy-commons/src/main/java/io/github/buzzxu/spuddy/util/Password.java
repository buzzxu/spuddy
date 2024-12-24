package io.github.buzzxu.spuddy.util;


import static org.apache.commons.codec.digest.DigestUtils.*;
import static org.mindrot.jbcrypt.BCrypt.*;


/**
 * @author xux
 * @date 2018/5/22 下午4:33
 */
public abstract class Password {


    /**
     * md5加密
     * @param data
     * @return
     */
    public static String md5(String data){
        return md5Hex(data);
    }
    public static String sha1(String data){
        return sha1Hex(data);
    }
    public static String sha256(String data){
        return sha256Hex(data);
    }
    public static String sha384(String data){
        return sha384Hex(data);
    }
    public static String sha512(String data){
        return sha512Hex(data);
    }



    /**
     * 加盐
     * @param count 长度
     * @return
     */
    public static String salt(int count){
        return gensalt(count);
    }

    /**
     * 默认 加盐字符串
     * @return
     */
    public static String salt(){
        return gensalt();
    }

    /**
     * 密码加密
     * @param password  明文
     * @param salt      加盐
     * @return
     */
    @Deprecated
    public static String password(String password,String salt){
        return hashpw(password,salt);
    }
    /**
     * 密码加密
     * @param password  明文
     * @param count     加盐长度
     * @return
     */
    public static String password(String password,int count){
        return hashpw(password,gensalt(count));
    }
    public static String password(String password){
        return hashpw(password,gensalt());
    }

    /**
     * 检查密码
     * @param password      密码明文
     * @param encryption    加密后密码
     * @return
     */
    public static boolean checkPwd(String password,String encryption){
        return checkpw(password,encryption);
    }

}
