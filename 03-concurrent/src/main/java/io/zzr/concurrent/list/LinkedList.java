package io.zzr.concurrent.list;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * @author zrzhao
 * @date 2022/6/25
 */
public class LinkedList<E> implements Iterable<E> {

    private Node<E> head;
    private Node<E> tail;

    public boolean add(E ele) {
        Node<E> curNode = new Node<>();
        curNode.data = ele;

        if (head == null) {
            head = curNode;
        }
        if (tail == null) {
            tail = curNode;
        } else {
            tail.next = curNode;
            curNode.pre = tail;
            tail = curNode;
        }

        return true;
    }

    private static class Node<E> {

        private Node<E> pre;
        private E data;
        private Node<E> next;

    }


    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            private Node<E> next = head;

            @Override
            public boolean hasNext() {
                return next != tail;
            }

            @Override
            public E next() {
                Node<E> ele = next;
                next = next.next;

                return ele.data;
            }
        };
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<E> spliterator() {
        return Iterable.super.spliterator();
    }
}
