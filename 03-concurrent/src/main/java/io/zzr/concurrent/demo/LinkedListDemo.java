package io.zzr.concurrent.demo;

import io.zzr.concurrent.list.LinkedList;

import java.util.Iterator;

/**
 * @author zrzhao
 * @date 2022/6/25
 */
public class LinkedListDemo {

    public static void main(String[] args) {
        LinkedList<String> list = new LinkedList<>();
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
