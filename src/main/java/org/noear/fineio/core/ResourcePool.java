package org.noear.fineio.core;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 资源池
 * */
public class ResourcePool<R> {
    //队列
    private final LinkedBlockingQueue<R> queue;
    //线程状态
    private final ThreadLocal<R> threadLocal = new ThreadLocal<>();

    //资源工厂
    private ResourceFactory<R> factory;

    public ResourcePool(int coreSize, ResourceFactory<R> factory) {
        this.factory = factory;
        this.queue = new LinkedBlockingQueue<>(coreSize);
    }

    /**
     * 申请资源
     * */
    public R apply() {
        try {
            return apply0();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 释放资源
     * */
    public void free() {
        free0();
    }

    /**
     * 清空资源
     * */
    public void clear(){
        synchronized (queue) {
            queue.forEach(r -> {
                factory.close(r);
            });

            queue.clear();
        }
        threadLocal.set(null);
    }

    /**
     * 创建资源
     * */
    private R create0() {
        try {
            return factory.create();
        } catch (Throwable ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 获取资源
     */
    private R apply0() throws InterruptedException {
        R r = threadLocal.get();

        if (r != null) {
            r = factory.check(r);
        }

        if (r == null) {
            synchronized (queue) {
                if (queue.isEmpty() == false) {
                    r = factory.check(queue.take());
                }

                if (r == null) {
                    if (null != (r = create0())) {
                        queue.offer(r);
                    }
                }
            }

            threadLocal.set(r);
        }

        return r;
    }

    /**
     * 释放资源
     */
    private void free0() {
        R r = threadLocal.get();

        if (r != null) {
            threadLocal.remove();

            if (null != (r = factory.free(r))) {
                synchronized (queue) {
                    queue.offer(r);
                }
            }
        }
    }
}
