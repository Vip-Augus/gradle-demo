package gradle.demo.util;

import java.security.MessageDigest;

/**
 * MD5加密工具
 *
 * @author by JingQ on 2018/1/1
 */
public class MD5Util {

    private static final String SECURE_KEY = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final int LEN_4 = 4;

    //生成MD5
    public static String genMD5(String message) {
        String md5 = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); // 创建一个md5算法对象
            byte[] messageByte = message.getBytes("UTF-8");
            byte[] md5Byte = md.digest(messageByte); // 获得MD5字节数组,16*8=128位
            md5 = bytesToHex(md5Byte); // 转换为16进制字符串
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5;
    }

    // 二进制转十六进制
    public static String bytesToHex(byte[] bytes) {
        StringBuffer hexStr = new StringBuffer();
        int num;
        for (int i = 0; i < bytes.length; i++) {
            num = bytes[i];
            if (num < 0) {
                num += 256;
            }
            if (num < 16) {
                hexStr.append("0");
            }
            hexStr.append(Integer.toHexString(num));
        }
        return hexStr.toString().toUpperCase();
    }

    public static String getCheckCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < LEN_4; i++)
        {
            int rand = (int) (Math.random() * SECURE_KEY.length());
            sb.append(SECURE_KEY.charAt(rand));
        }
        return sb.toString();
    }

}
