package gradle.demo.util;

/**
 * 字符操作工具类
 * Author by JingQ on 2018/1/1
 */
public class StringUtil {

    /**
     * 判断字符串是否相等
     * @param param1    参数1
     * @param param2    参数2
     * @return          比较结果
     */
    public static boolean isEquals(String param1, String param2) {
        return param1 == null ? param2 == null : param2.equals(param1);
    }

    public static int getInteger(String s) {
        int result = 0;
        try {
            result = Integer.parseInt(s);
        } catch (Exception e) {
            result = 0;
        }
        return result;
    }

    public static boolean isNullOrEmpty(Object str) {
        if(str == null){
            return true;
        }
        if(str instanceof String){
            String temp=String.valueOf(str);
            temp=temp.trim();
            return temp.length() == 0;
        }
        return false;
    }
}
