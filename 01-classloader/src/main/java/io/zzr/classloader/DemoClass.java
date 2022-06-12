package io.zzr.classloader;

/**
 * @author zrzhao
 * @date 2022/6/9
 */
public class DemoClass {

    static {
        System.out.println("static");
    }

    {
        System.out.println("ordinary");
    }


    public DemoClass() {
        System.out.println("Constructor");
    }


}
