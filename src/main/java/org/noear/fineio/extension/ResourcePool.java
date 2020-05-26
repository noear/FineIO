package org.noear.fineio.extension;

import java.util.concurrent.LinkedBlockingQueue;

public class ResourcePool<R> {
    private final LinkedBlockingQueue<R> queue = new LinkedBlockingQueue<>();
    private final ThreadLocal<R> threadLocal = new ThreadLocal<>();

    private int coreSize;
    private ResourceFactory<R> factory;

    public ResourcePool(int coreSize, ResourceFactory<R> factory) {
        this.coreSize = coreSize;
        this.factory = factory;
    }

    public R apply() {
        try {
            return apply0();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void free() {
        free0();
    }

    protected R open(R res) {
        return res;
    }

    protected R close(R res) {
        return res;
    }

    /**
     * 获取资源
     */
    private R apply0() throws InterruptedException {
        R r = threadLocal.get();

        if (r == null) {
            if (queue.isEmpty() == false) {
                r = open(queue.take());
            }

            if(r == null){
                if(null != (r = factory.create())) {
                    queue.offer(r);
                }
            }

            threadLocal.set(r);
        }

        return r;
    }

    /**
     * 释放资源
     * */
    private void free0() {
        R r = threadLocal.get();

        if (r != null) {
            threadLocal.remove();

            if(null != (r = close(r))) {
                queue.offer(r);
            }
        }
    }
}