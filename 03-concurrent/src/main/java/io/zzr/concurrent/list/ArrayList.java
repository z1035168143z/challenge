package io.zzr.concurrent.list;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * @author zrzhao
 * @date 2022/6/19
 */
public class ArrayList<E> implements Iterable<E> {

    private Object[] data;
    private int initSize = 16;
    private int size;

    public ArrayList() {

    }

    public ArrayList(int initSize) {
        this.initSize = initSize;
    }

    public boolean add(E ele) {
        initList();

        ensureCapacityInternal(size + 1);

        data[size++] = ele;
        return true;
    }

    @SuppressWarnings("unchecked")
    public E get(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return (E) data[index];
    }

    public int size() {
        return size;
    }

    private void ensureCapacityInternal(int targetSize) {
        if (data.length < targetSize) {
            int newSize = data.length * 2;
            if (newSize < targetSize) {
                newSize = targetSize;
            }

            data = Arrays.copyOf(data, newSize);
        }
    }

    private void initList() {
        if (data != null) {
            return;
        }
        data = new Object[initSize];
    }


    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            private int curIndex = 0;


            @Override
            public boolean hasNext() {
                return curIndex < size;
            }

            @Override
            public E next() {
                return get(curIndex++);
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
