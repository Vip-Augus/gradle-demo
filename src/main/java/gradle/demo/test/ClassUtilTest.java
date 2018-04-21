package gradle.demo.test;

import java.lang.reflect.Method;

/**
 * @author by JingQ on 2018/4/17
 */
public class ClassUtilTest {

    public static void printClassMethodMessage(Object object) {
        Class c = object.getClass();
        System.out.println("类名：" + c.getName());

        Method[] methods = c.getMethods();

        for (int i = 0; i < methods.length; i++) {
            Class returnType = methods[i].getReturnType();
            System.out.print(returnType.getName() + " " + methods[i].getName() + "(");
            Class[] paramterTypes = methods[i].getParameterTypes();
            for (Class temp : paramterTypes) {
                System.out.print(temp + ", ");
            }
            System.out.println(")");
        }
    }
}
