package com.burytomorrow.final_project;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by BuryTomorrow on 2018/1/7.
 */

public class MD5Utils {
    public static String md5Password(String password){
        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(password.getBytes());
            //加密运算，对密码的每一个byte做与运算0xff
            for(byte b:result){
                int number = b & 0xff;
                String str = Integer.toHexString(number);
                if(str.length()==1){
                    sb.append("0");
                }
                sb.append(str);
            }
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return sb.toString();
    }
}
