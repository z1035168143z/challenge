package io.zzr.classloader;

/**
 * @author zrzhao
 * @date 2022/6/9
 */
public class HelloByteCode {

    public static void main(String[] args) throws Exception {
        String ab = "a" + 'b';
        String abc = ab + 'c';

        System.out.println(abc);
    }

}
