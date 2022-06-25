package io.zzr.concurrent.demo;

import io.zzr.concurrent.list.ArrayList;

import java.util.Iterator;

/**
 * @author zrzhao
 * @date 2022/6/25
 */
public class ArrayListDemo {

    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>(2);
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        list.add("e");
        list.add("f");
        list.add("g");


        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

    }


}
